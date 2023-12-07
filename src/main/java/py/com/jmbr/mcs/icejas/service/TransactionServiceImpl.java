package py.com.jmbr.mcs.icejas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import py.com.jmbr.java.commons.beans.mcs.icejas.TransactionPostResData;
import py.com.jmbr.java.commons.domain.mcs.icejas.Transaction;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionPostReq;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionPostRes;
import py.com.jmbr.java.commons.logger.RequestUtil;
import py.com.jmbr.mcs.icejas.constant.TransactionConstant;
import py.com.jmbr.mcs.icejas.dao.TransactionDAO;

import java.math.BigDecimal;
import java.math.BigInteger;

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
        Integer transactionId = transactionDAO.addTransaction(transaction);
        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactions:After add transaction id= ",transactionId);
         
        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactions:Before get current amount churchId=",transaction.getChurchId());
        BigDecimal currentAmount = transactionDAO.getCurrentAmount(transaction.getChurchId());
        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactions:After get current amount =",currentAmount);

        BigDecimal totalAmount = currentAmount;
        BigDecimal previousAmount = currentAmount;
        if(transaction.getTransactionType().getCategory().equals("D"))
            totalAmount.subtract(transaction.getAmount());
        else
            totalAmount.add(transaction.getAmount());
        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactions:Before add balance_history totalAmount =",totalAmount);
        boolean isBalanceHistoryInserted= transactionDAO.addBalanceHistory(transaction.getChurchId(),totalAmount,transactionId,previousAmount);
        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactions:After add balance_history result=",isBalanceHistoryInserted);

        //TODO agregar tambien el saldo en chcurch


        resultData.setTransactionId(transactionId);
        resultData.setMessage(TransactionConstant.MESSAGE_SUCCESS);
        resultData.setStatus(TransactionConstant.STATUS_OK);

        return result;
    }
}
