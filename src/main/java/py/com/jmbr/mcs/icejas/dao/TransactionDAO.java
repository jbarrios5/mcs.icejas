package py.com.jmbr.mcs.icejas.dao;

import py.com.jmbr.java.commons.domain.mcs.icejas.Church;
import py.com.jmbr.java.commons.domain.mcs.icejas.Transaction;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionType;


import java.math.BigDecimal;
import java.util.List;

public interface TransactionDAO {
    Integer addTransaction(Transaction transaction, Integer churchId,Integer transactionType,Integer userId);


    Boolean addBalanceHistory(String logId,Integer churchId, BigDecimal amount,Integer transactionId,BigDecimal previousAmount);

    Church getChurch(String logId,Integer churchId);

    Boolean updateBalanceChurch(String logId,Integer churchId, BigDecimal amount);

    List<TransactionType> getTransactionsType(String logId);
}
