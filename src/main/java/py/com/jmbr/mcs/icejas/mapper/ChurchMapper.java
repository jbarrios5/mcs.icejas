package py.com.jmbr.mcs.icejas.mapper;

import org.springframework.jdbc.core.RowMapper;
import py.com.jmbr.java.commons.domain.mcs.icejas.Church;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChurchMapper implements RowMapper<Church> {
    @Override
    public Church mapRow(ResultSet rs, int rowNum) throws SQLException {
        Church church = new Church();
        church.setName(rs.getString("name"));
        church.setId(rs.getInt("id"));
        church.setCurrentBalance(rs.getBigDecimal("current_balance"));
        church.setCreated(rs.getTimestamp("created").toString());
        return church;
    }
}
