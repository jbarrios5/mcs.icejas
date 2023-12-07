package py.com.jmbr.mcs.icejas.dao;

import py.com.jmbr.java.commons.domain.mcs.icejas.Transaction;


import java.math.BigDecimal;

public interface TransactionDAO {
    Integer addTransaction(Transaction transaction, Integer churchId,Integer transactionType,Integer userId);


    Boolean addBalanceHistory(String logId,Integer churchId, BigDecimal amount,Integer transactionId,BigDecimal previousAmount);

    Boolean updateBalanceChurch(String logId,Integer churchId, BigDecimal amount);
}
