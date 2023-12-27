package py.com.jmbr.mcs.icejas.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import py.com.jmbr.java.commons.domain.mcs.icejas.*;
import py.com.jmbr.java.commons.exception.JMBRException;
import py.com.jmbr.java.commons.exception.JMBRExceptionType;
import py.com.jmbr.java.commons.logger.RequestUtil;
import py.com.jmbr.mcs.icejas.mapper.ChurchMapper;
import py.com.jmbr.mcs.icejas.mapper.TransactionDetailMapper;
import py.com.jmbr.mcs.icejas.mapper.TransactionTypeMapper;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Map;

@Repository
public class TransactionDAOImpl implements TransactionDAO {
    private static final Logger logger = LoggerFactory.getLogger(TransactionDAOImpl.class);

    @Autowired
    private JdbcTemplate jdbcPGS;

    @Override
    public Integer addTransaction(Transaction transaction,Integer churchId,Integer transactionType,Integer userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcPGS.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(SQLQueries.ADD_TRANSACTION, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, transactionType);
                ps.setInt(2, userId);
                ps.setBigDecimal(3, transaction.getAmount());
                ps.setInt(4, churchId);
                ps.setString(5, transaction.getDetails());
                ps.setDate(6, transaction.getRegisterDate());
                return ps;
            }
        }, keyHolder);

        return getTransactionId(keyHolder);
    }
    @Override
    public Boolean addBalanceHistory(String logId,Integer churchId, BigDecimal amount, Integer transactionId,BigDecimal previousAmount) {
        int result = 0;
        try {
            result = jdbcPGS.update(SQLQueries.ADD_BALANCE_HISTORY,
                    amount,previousAmount,churchId,transactionId);
        }catch (DataAccessException e){
            logger.warn(RequestUtil.LOG_FORMATT,logId,"addBalanceHistory:Error inserting balance",e.getMessage());
            throw new JMBRException("Ocurrio un error al insertar saldos", JMBRExceptionType.FALTAL, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return (result > 0);
    }

    @Override
    public Boolean updateBalanceChurch(String logId,Integer churchId, BigDecimal amount) {
        int result = 0;
        try {
            result = jdbcPGS.update(SQLQueries.UPDATE_BALANCE_CHURCH,
                    amount, churchId);
        }catch (DataAccessException e){
            logger.warn(RequestUtil.LOG_FORMATT,logId,"updateBalanceChurch:Error inserting balance",e.getMessage());
            throw new JMBRException("Ocurrio un error al insertar saldos en church", JMBRExceptionType.FALTAL, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result > 0;
    }

    @Override
    public Church getChurch(String logId,Integer churchId) {
        try {
            return jdbcPGS.queryForObject(SQLQueries.GET_CHURCH,new Object []{churchId},new ChurchMapper());
        }catch (DataAccessException e){
            logger.warn(RequestUtil.LOG_FORMATT,logId,"getChurch:Error getting church",e.getMessage());
            throw new JMBRException("Ocurrio un error al obtener los detalles de la iglesia.", JMBRExceptionType.FALTAL, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<TransactionType> getTransactionTypes(String logId) {
        try {
            return jdbcPGS.query(SQLQueries.GET_TRANSACTIONS_TYPES,new TransactionTypeMapper());
        }catch (DataAccessException e){
            logger.warn(RequestUtil.LOG_FORMATT,logId,"getTransactionTypes:Error getting church",e.getMessage());
            throw new JMBRException("Ocurrio un error al obtener los tipos de transacciones",JMBRExceptionType.FALTAL,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<TransactionDetails> getTransactionDetails(String logId,Integer churchId) {
        try {
            return jdbcPGS.query(SQLQueries.GET_TRANSACTION_DETAILS, new Object[]{churchId},new TransactionDetailMapper());
        }catch (DataAccessException e){
            logger.warn(RequestUtil.LOG_FORMATT,logId,"getTransactionDetails:Error getting church",e.getMessage());
            throw new JMBRException("Ocurrio un error al obtener las transacciones",JMBRExceptionType.FALTAL,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Integer getTransactionId(KeyHolder keyHolder) {
        List<Map<String, Object>> keyList = keyHolder.getKeyList();

        Map<String, Object> keyMap = keyList.get(0);

        // Asumiendo que la columna que contiene la clave generada se llama "ID"
        Object idValue = keyMap.get("id");

        return ((Number) idValue).intValue();
    }
}
