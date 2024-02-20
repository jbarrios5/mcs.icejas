package py.com.jmbr.mcs.icejas.dao;

public class SQLQueries {
    private SQLQueries(){}

    public static final String ADD_TRANSACTION = "INSERT INTO transactions(transaction_type_id,user_id,amount,church_id,details,registered_date) values(?,?,?,?,?,?)";
    public static final String ADD_BALANCE_HISTORY = "INSERT INTO balance_history (current_balance,previous_balance,church_id,transaction_id ) values(?,?,?,?)";
    public static final String UPDATE_BALANCE_CHURCH = "UPDATE church set current_balance = ? WHERE id = ?";
    public static final String GET_CHURCH = "SELECT id,name,current_balance,created FROM church  WHERE id = ?";

    public static final String GET_TRANSACTION_DETAILS =
            "select tr.registered_date ,tr.id  ,amount ,details ,user_id ,ty.name ,ty.category "+
            " from transactions tr " +
            " join transaction_type ty on ty.id = tr.transaction_type_id" ;
    public static final String GET_TRANSACTIONS_TYPES = "SELECT id,name,category,created FROM transaction_type";

    public static final String ADD_TRANSACTION_TYPE = "INSERT INTO transaction_type (name,category)values(?,?)";
    public static final String ADD_CLOSED_MONTH = "INSERT INTO balance_history_month (name,category)values(?,?)";
    public static final String GET_BALANCE_MONTH = "" +
            "select SUM(CASE WHEN tp.category  = 'D' THEN amount ELSE 0 END) AS egreso, " +
            "    SUM(CASE when tp.category  = 'C' THEN amount ELSE 0 END) AS ingreso, " +
            "   EXTRACT(MONTH FROM t.registered_date) mes " +
            "    from transactions t join transaction_type tp on tp.id = t.transaction_type_id where  t.church_id = ? GROUP by EXTRACT(MONTH FROM t.registered_date) order by mes ASC";

    public static final String UPDATE_TRANSACTION = "UPDATE transactions  SET transaction_type_id=? amount=? registered_date=? WHERE id = ?";

    public static final String GET_CURRENT_BALANCE = "SELECT  (SUM(CASE WHEN tp.category = 'C' THEN amount ELSE 0 END) - SUM(CASE WHEN tp.category = 'D' THEN amount ELSE 0 END)) AS saldo_actual FROM transactions t JOIN transaction_type tp ON tp.id = t.transaction_type_id " +
            " WHERE t.church_id = ?";

}
