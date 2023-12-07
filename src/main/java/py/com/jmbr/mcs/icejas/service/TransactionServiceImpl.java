package py.com.jmbr.mcs.icejas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import py.com.jmbr.java.commons.beans.mcs.icejas.TransactionPostResData;
import py.com.jmbr.java.commons.beans.mcs.icejas.TransactionTypesGetResData;
import py.com.jmbr.java.commons.domain.mcs.icejas.Church;
import py.com.jmbr.java.commons.domain.mcs.icejas.Transaction;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionPostReq;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionPostRes;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionType;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionTypesGetRes;
import py.com.jmbr.java.commons.logger.RequestUtil;
import py.com.jmbr.mcs.icejas.constant.TransactionConstant;
import py.com.jmbr.mcs.icejas.dao.TransactionDAO;

import java.math.BigDecimal;
import java.math.BigInteger;
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
        TransactionTypesGetRes resultData = new TransactionTypesGetRes();
        logger.info(RequestUtil.LOG_FORMATT,logId,"getTransactionTypes:Starting get transactions types",null);
        List<TransactionType> transactionTypeList = transactionDAO.getTransactionsType(logId);
        logger.info(RequestUtil.LOG_FORMATT,logId,"getTransactionTypes:After get transactions types",transactionTypeList);

        resultData.setTransactionTypes(transactionTypeList);
        result.setData(resultData);
        return result;


    }
}
