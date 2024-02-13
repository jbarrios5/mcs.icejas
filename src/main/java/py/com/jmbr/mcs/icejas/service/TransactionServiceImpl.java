package py.com.jmbr.mcs.icejas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import py.com.jmbr.java.commons.beans.mcs.icejas.*;
import py.com.jmbr.java.commons.domain.mcs.icejas.*;
import py.com.jmbr.java.commons.exception.JMBRException;
import py.com.jmbr.java.commons.exception.JMBRExceptionType;
import py.com.jmbr.java.commons.logger.RequestUtil;
import py.com.jmbr.mcs.icejas.constant.TransactionConstant;
import py.com.jmbr.mcs.icejas.dao.TransactionDAO;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService{
    public static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    @Autowired
    private TransactionDAO transactionDAO;
    @Override
    public TransactionPostResData addTransactions(TransactionPostReq req) {
        TransactionPostResData result = new TransactionPostResData();
        TransactionPostRes resultData = new TransactionPostRes();
        Transaction transaction = req.getTransaction();
        String logId = RequestUtil.getLogId();
        
        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactions:Starting add transaction",req);

        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactions:Before add transaction ",transaction);
        Integer transactionId = transactionDAO.addTransaction(transaction,req.getChurch().getId(),req.getTransactionType().getId(), req.getUserId());
        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactions:After add transaction id= ",transactionId);
         
        BigDecimal currentAmount = req.getChurch().getCurrentBalance();

        BigDecimal totalAmount = currentAmount;
        BigDecimal previousAmount = currentAmount;
        if(req.getTransactionType().getCategory().equals(TransactionConstant.TRANSACTION_DEBIT))
            totalAmount = totalAmount.subtract(transaction.getAmount());
        else
            totalAmount = totalAmount.add(transaction.getAmount());

        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactions:Before add balance_history totalAmount =",totalAmount);
        boolean isBalanceHistoryInserted= transactionDAO.addBalanceHistory(logId,req.getChurch().getId(),totalAmount,transactionId,previousAmount);
        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactions:After add balance_history result=",isBalanceHistoryInserted);

        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactions:Before update current_balance=",null);
        boolean isUpdateBalanceChurch = transactionDAO.updateBalanceChurch(logId,req.getChurch().getId(),totalAmount);
        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactions:After update current_balance=",isUpdateBalanceChurch);

        resultData.setTransactionId(transactionId);
        resultData.setMessage(TransactionConstant.MESSAGE_SUCCESS);
        resultData.setStatus(TransactionConstant.STATUS_OK);
        result.setData(resultData);
        return result;
    }

    @Override
    public Church getChurch(Integer churchId) {
        String logId = RequestUtil.getLogId();
        logger.info(RequestUtil.LOG_FORMATT,logId,"getChurch:Starting get church",churchId);
        Church church =  transactionDAO.getChurch(logId,churchId);
        logger.info(RequestUtil.LOG_FORMATT,logId,"getChurch:After get church",church);
        return church;
    }

    @Override
    public TransactionTypesGetResData getTransactionTypes() {
        String logId = RequestUtil.getLogId();
        TransactionTypesGetResData result = new TransactionTypesGetResData();
        TransactionTypesGetRes data = new TransactionTypesGetRes();
        logger.info(RequestUtil.LOG_FORMATT,logId,"getTransactionTypes:Starting get transcationTypes",null);
        logger.info(RequestUtil.LOG_FORMATT,logId,"getTransactionTypes:Before get transcationTypes",null);
        List<TransactionType> transactionTypes = transactionDAO.getTransactionTypes(logId);
        logger.info(RequestUtil.LOG_FORMATT,logId,"getTransactionTypes:After get transcationTypes",transactionTypes.size());

        data.setTransactionTypes(transactionTypes);
        result.setData(data);
        return result;
    }

    @Override
    public TransactionDetailGetResData getTransactionDetails(Integer churchId,Date startDate,Date endDate,Integer activiteType,String transactionType) {
        String logId = RequestUtil.getLogId();
        TransactionDetailGetResData result = new TransactionDetailGetResData();
        TransactionDetailGetRes data = new TransactionDetailGetRes();
        logger.info(RequestUtil.LOG_FORMATT,logId,"getTransactionDetails:Starting GET transaction details",null);
        logger.info(RequestUtil.LOG_FORMATT,logId,"getTransactionDetails:Before get all transaction details churchId=",churchId);
        List<TransactionDetails> details = transactionDAO.getTransactionDetails(logId,churchId,startDate,endDate,activiteType,transactionType);
        logger.info(RequestUtil.LOG_FORMATT,logId,"getTransactionDetails:After get all transaction details churchId=",details.size());
        data.setDetails(details);
        result.setData(data);

        return result;
    }

    @Override
    public TransactionTypesPostRestData addTransactionType(TransactionType transactionType) {
        String logId = RequestUtil.getLogId();
        TransactionTypesPostRestData result = new TransactionTypesPostRestData();
        TransactionTypesPostRes data  = new TransactionTypesPostRes();
        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactionType:Starting POST transaction type",null);
        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactionType:Before add transaction type",transactionType.toString());
        Boolean isInserted = transactionDAO.addTransactionType(logId,transactionType);
        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactionType:After add  transaction type result=",isInserted);

        data.setIsInserted(isInserted);
        result.setData(data);
        return result;
    }

    @Override
    public TransactionMonthClosedPostResData closedMonth(Integer userId, Date closedDate) {
        return null;
    }

    @Override
    public TransactionReportGetResData getReportMonth(Integer churchId) {
        String logId = RequestUtil.getLogId();
        logger.info(RequestUtil.LOG_FORMATT,logId,"getReportMonth:Starting GET transaction report",null);
        TransactionReportGetResData result = new TransactionReportGetResData();
        logger.info(RequestUtil.LOG_FORMATT,logId,"getReportMonth:Before get transaction report",null);
        List<TransactionReportGetRes> transactionsReport = transactionDAO.getReportMonth(churchId,logId);
        logger.info(RequestUtil.LOG_FORMATT,logId,"getReportMonth:After get transaction report with result=",transactionsReport.size());
        if(transactionsReport.isEmpty())
            throw new JMBRException("No se obtuvo ningun movimiento", JMBRExceptionType.WARNING, HttpStatus.BAD_REQUEST);

        result.setData(transactionsReport);
        return result;
    }
}
