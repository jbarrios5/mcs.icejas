package py.com.jmbr.mcs.icejas.dao;

public class SQLQueries {
    private SQLQueries(){}

    public static final String ADD_TRANSACTION = "INSERT INTO transactions(transaction_type_id,user_id,amount,church_id,details) values(?,?,?,?,?)";
    public static final String ADD_BALANCE_HISTORY = "INSERT INTO balance_history (current_balance,previous_balance,church_id,transaction_id ) values(?,?,?,?)";
    public static final String UPDATE_BALANCE_CHURCH = "UPDATE church set current_balance = ? WHERE id = ?";
    public static final String GET_CHURCH = "SELECT id,name,current_balance,created FROM church  WHERE id = ?";

    public static final String GET_TRANSACTIONS_TYPES = "SELECT id,name,category,created FROM transaction_type";
}
