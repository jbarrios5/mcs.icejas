package py.com.jmbr.mcs.icejas.dao;

import py.com.jmbr.java.commons.domain.mcs.icejas.*;


import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public interface TransactionDAO {
    Integer addTransaction(Transaction transaction, Integer churchId,Integer transactionType,Integer userId);


    Boolean addBalanceHistory(String logId,Integer churchId, BigDecimal amount,Integer transactionId,BigDecimal previousAmount);

    Church getChurch(String logId,Integer churchId);

    Boolean updateBalanceChurch(String logId,Integer churchId, BigDecimal amount);

    List<TransactionType> getTransactionTypes(String logId);
    List<TransactionDetails> getTransactionDetails(String logId,Integer churchId,Date startDate,Date endDate,Integer activiteType,String transactionType);

    Boolean addTransactionType(String logId,TransactionType transactionType) ;

    Boolean addCloseMonth(Integer userId, Date closeMonth);
    List<TransactionReportGetRes> getReportMonth(Integer churchId,String logId);


}
