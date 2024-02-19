package py.com.jmbr.mcs.icejas.mapper;

import org.springframework.jdbc.core.RowMapper;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionDetails;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionDetailMapper implements RowMapper<TransactionDetails> {

    @Override
    public TransactionDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setTransactionId(rs.getInt("id"));
        transactionDetails.setTransactionDetail(rs.getString("details"));
        transactionDetails.setTransactionTypeName(rs.getString("name"));
        transactionDetails.setTransactionCategory(rs.getString("category"));
        transactionDetails.setAmount(rs.getBigDecimal("amount"));
        transactionDetails.setRegisteredDate(rs.getDate("registered_date").toString());
        return transactionDetails;
    }
}
