package py.com.jmbr.mcs.icejas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import py.com.jmbr.java.commons.beans.mcs.icejas.TransactionPostResData;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionPostReq;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionPostRes;
import py.com.jmbr.java.commons.logger.RequestUtil;
import py.com.jmbr.mcs.icejas.dao.TransactionDAO;

import java.math.BigDecimal;

@Service
public class TransactionServiceImpl implements TransactionService{
    public static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    @Autowired
    private TransactionDAO transactionDAO;
    @Override
    public TransactionPostResData addTransactions(TransactionPostReq req) {
        TransactionPostResData result = new TransactionPostResData();
        String logId = RequestUtil.getLogId();
        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactions:Starting add transaction",req);

        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactions:Before add transaction ",req.getTransaction());
        Integer transactionId = transactionDAO.addTransaction(req.getTransaction());
        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactions:After add transaction id= ",transactionId);

        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactions:Before get current amount churchId=",req.getTransaction().getChurchId());
        BigDecimal currentAmount = transactionDAO.getCurrentAmount(req.getTransaction().getChurchId());
        logger.info(RequestUtil.LOG_FORMATT,logId,"addTransactions:After get current amount =",currentAmount);


        boolean isInsertedBalanceHistory =  transactionDAO.addBalanceHistory(req.getTransaction().getChurchId(),req.getTransaction().getAmount(),transactionId,currentAmount);


        return null;
    }
}
