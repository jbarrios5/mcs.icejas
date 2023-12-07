package py.com.jmbr.mcs.icejas.mapper;

import org.springframework.jdbc.core.RowMapper;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionTypesMapper implements RowMapper<TransactionType> {

    @Override
    public TransactionType mapRow(ResultSet rs, int rowNum) throws SQLException {
        TransactionType result  =  new TransactionType();
        result.setCategory(rs.getString("category"));
        result.setDescription(rs.getString("name"));
        result.setId(rs.getInt("id"));
        result.setCreated(rs.getTimestamp("created").toString());
        return result;
    }
}
