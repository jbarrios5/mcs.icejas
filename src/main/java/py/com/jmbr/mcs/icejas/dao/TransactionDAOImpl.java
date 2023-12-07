package py.com.jmbr.mcs.icejas.dao;

import org.springframework.stereotype.Repository;
import py.com.jmbr.java.commons.domain.mcs.icejas.Transaction;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionPostRes;

import java.math.BigDecimal;

@Repository
public class TransactionDAOImpl implements TransactionDAO{
    @Override
    public Integer addTransaction(Transaction transaction) {
        return 1;
    }

    @Override
    public BigDecimal getCurrentAmount(Integer churchId){
        return null;
    }

    @Override
    public Boolean addBalanceHistory(Integer churchId, BigDecimal amount, Integer transactionId,BigDecimal previousAmount) {
        return null;
    }

    @Override
    public Boolean updateBalanceChurch(Integer churchId, BigDecimal amount) {
        return null;
    }
}
