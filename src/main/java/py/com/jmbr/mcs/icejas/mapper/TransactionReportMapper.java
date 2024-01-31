package py.com.jmbr.mcs.icejas.mapper;

import org.springframework.jdbc.core.RowMapper;
import py.com.jmbr.java.commons.domain.mcs.icejas.TransactionReportGetRes;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionReportMapper implements RowMapper<TransactionReportGetRes> {
    @Override
    public TransactionReportGetRes mapRow(ResultSet rs, int rowNum) throws SQLException {
        TransactionReportGetRes result = new TransactionReportGetRes();
        result.setMonth(rs.getInt("mes"));
        result.setTotalCredit(rs.getBigDecimal("ingreso"));
        result.setTotalDebit(rs.getBigDecimal("egreso"));

        return result;
    }
}
