package py.com.jmbr.mcs.icejas.mapper;

import org.springframework.jdbc.core.RowMapper;
import py.com.jmbr.java.commons.domain.mcs.icejas.MonthSummaryGetRes;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MonthSummaryMapper implements RowMapper<MonthSummaryGetRes> {
    @Override
    public MonthSummaryGetRes mapRow(ResultSet rs, int rowNum) throws SQLException {
        MonthSummaryGetRes res = new MonthSummaryGetRes();
        res.setTotalCredit(rs.getBigDecimal("ingreso"));
        res.setTotalDebit(rs.getBigDecimal("egreso"));
        res.setMonth(rs.getInt("mes"));
        res.setTotalSum(rs.getBigDecimal("diferencia"));
        return res;
    }
}
