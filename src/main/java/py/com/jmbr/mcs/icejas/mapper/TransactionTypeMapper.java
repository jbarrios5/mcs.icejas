package py.com.jmbr.mcs.icejas.mapper;

import org.springframework.jdbc.core.RowMapper;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionTypeMapper implements RowMapper<TransactionType> {
    @Override
    public TransactionType mapRow(ResultSet rs, int rowNum) throws SQLException {
        TransactionType res = new TransactionType();
        res.setCategory(rs.getString("category"));
        res.setDescription(rs.getString("name"));
        res.setId(rs.getInt("id"));
        res.setCreated(rs.getTimestamp("created").toString());
        return res;
    }
}
