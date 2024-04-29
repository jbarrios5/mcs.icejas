package py.com.jmbr.mcs.icejas.dao;

import org.apache.commons.lang3.StringUtils;
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
import py.com.jmbr.mcs.icejas.mapper.MonthSummaryMapper;
import py.com.jmbr.mcs.icejas.mapper.TransactionDetailMapper;
import py.com.jmbr.mcs.icejas.mapper.TransactionReportMapper;
import py.com.jmbr.mcs.icejas.mapper.TransactionTypeMapper;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
                ps.setDate(6, buildDate(transaction.getRegisterDate()));
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
    public List<TransactionDetails> getTransactionDetails(String logId,Integer churchId,String startDate,String endDate,Integer activiteType,String transactionType) {
        String query = buildGetTransactionDetailQuery(churchId,startDate,endDate,activiteType,transactionType);
        try {
            return jdbcPGS.query(query,new TransactionDetailMapper());
        }catch (DataAccessException e){
            logger.warn(RequestUtil.LOG_FORMATT,logId,"getTransactionDetails:Error getting church",e.getMessage());
            throw new JMBRException("Ocurrio un error al obtener las transacciones",JMBRExceptionType.FALTAL,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Boolean addTransactionType(String logId,TransactionType transactionType) {
        int result = 0;
        try {
            result = jdbcPGS.update(SQLQueries.ADD_TRANSACTION_TYPE, transactionType.getDescription(),transactionType.getCategory());
        }catch (DataAccessException e){
            logger.warn(RequestUtil.LOG_FORMATT,logId,"addTransactionType:Error ",e.getMessage());
            throw new JMBRException("Ocurrio un error al insertar el tipo de transaction",JMBRExceptionType.FALTAL,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return (result > 0 );
    }

    @Override
    public Boolean addCloseMonth(Integer userId, Date closeMonth) {
        int result = 0;
        try {
            result = jdbcPGS.update(SQLQueries.ADD_CLOSED_MONTH, userId,closeMonth);
        }catch (DataAccessException e){
            throw new JMBRException("Ocurrio un error al insertar el tipo de transaction",JMBRExceptionType.FALTAL,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return (result > 0 );
    }

    @Override
    public List<TransactionReportGetRes> getReportMonth(Integer churchId,String logId) {
        try {
            return jdbcPGS.query(SQLQueries.GET_BALANCE_MONTH, new Object[]{churchId},new TransactionReportMapper());
        }catch (DataAccessException e){
            logger.warn(RequestUtil.LOG_FORMATT,logId,"getTransactionDetails:Error getting church",e.getMessage());
            throw new JMBRException("Ocurrio un error al obtener los reportes",JMBRExceptionType.FALTAL,HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public Boolean updateTransaction(String logId, Transaction transaction, Integer transactionType) {
        int result = 0;
        try {
            result = jdbcPGS.update(SQLQueries.UPDATE_TRANSACTION,
                    transactionType,
                    transaction.getAmount(),
                    buildDate(transaction.getRegisterDate()) ,
                    transaction.getDetails(),
                    transaction.getId());
        }catch (DataAccessException e){
            logger.warn(RequestUtil.LOG_FORMATT,logId,"updateTransaction:Error getting transactions",e.getMessage());
            throw new JMBRException("Ocurrio un error al obtener las transacciones",JMBRExceptionType.FALTAL,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return (result > 0);
    }

    private Integer getTransactionId(KeyHolder keyHolder) {
        List<Map<String, Object>> keyList = keyHolder.getKeyList();

        Map<String, Object> keyMap = keyList.get(0);

        // Asumiendo que la columna que contiene la clave generada se llama "ID"
        Object idValue = keyMap.get("id");

        return ((Number) idValue).intValue();
    }

    @Override
    public BigDecimal getTotalAmount(String logId, Integer chruchId) {
        try {
            return jdbcPGS.queryForObject(SQLQueries.GET_NEW_CURRENT_BALANCE, new Object[]{chruchId}, BigDecimal.class);
        }catch (DataAccessException e){
            logger.warn(RequestUtil.LOG_FORMATT,logId,"newTotalAmount:Error getting total amount",e.getMessage());
            throw new JMBRException("Ocurrio un error al el nuevo saldo ",JMBRExceptionType.FALTAL,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Boolean deleteTransaction(String logId, Integer transactionId) {
        int result = 0;
        try {
            result = jdbcPGS.update(SQLQueries.DELETE_TRANSACTION, transactionId);
        }catch (DataAccessException e){
            logger.warn(RequestUtil.LOG_FORMATT,logId,"deleteTransaction:Error getting total amount",e.getMessage());
            throw new JMBRException("Ocurrio un error al eliminar una transaccion",JMBRExceptionType.FALTAL,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return (result >0);
    }

    @Override
    public List<MonthSummaryGetRes> getSummaryMonths(String startMonth, String endMonth, Integer churchId, String logId) {
        try {
            return jdbcPGS.query(SQLQueries.GET_SUMMARY_MONTH, new Object[]{churchId,buildDate(startMonth),buildDate(endMonth)},new MonthSummaryMapper());
        }catch (DataAccessException e){
            logger.warn(RequestUtil.LOG_FORMATT,logId,"getSummaryMonths:Error getting summary",e.getMessage());
            throw new JMBRException("Ocurrio un error al obtener los reportes",JMBRExceptionType.FALTAL,HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public BigDecimal getChurchCurrentBalance(String logId, Integer churchId) {
        try {
            return jdbcPGS.queryForObject(SQLQueries.GET_CHURCH_CURRENT_BALANCE, new Object[]{churchId}, BigDecimal.class);
        }catch (DataAccessException e){
            logger.warn(RequestUtil.LOG_FORMATT,logId,"getChurchCurrentBalance:Error getting total amount",e.getMessage());
            throw new JMBRException("Ocurrio un error al obtener el  saldo actual ",JMBRExceptionType.FALTAL,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String buildGetTransactionDetailQuery(Integer churchId, String startDate, String endDate, Integer activiteType, String transactionType){
        StringBuilder query  = new StringBuilder();
        query.append(SQLQueries.GET_TRANSACTION_DETAILS);
        query.append( " where tr.church_id = "+churchId.toString()) ;

        if(StringUtils.isNotBlank(startDate))
            query.append(String.format(" AND tr.registered_date >= '%s'",startDate));
        if(StringUtils.isNotBlank(endDate))
            query.append(String.format(" AND tr.registered_date <= '%s'",endDate));
        if(activiteType != null)
            query.append(" AND ty.id = "+activiteType);
        if(StringUtils.isNotBlank(transactionType))
            query.append( String.format(" AND ty.category = '%s'",transactionType) );

        query.append(" order by tr.id DESC");

        return query.toString();
    }

    private Date buildDate(String date){
        // Parsear el String a LocalDate
        LocalDate fechaLocal = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);

        // Convertir LocalDate a java.sql.Date
        return Date.valueOf(fechaLocal);
    }
}
