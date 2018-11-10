package TransactionDatabase;
import java.util.HashMap;
import java.util.Map;

public class TransactionDatabase {

    Map<Long,Transaction> database;

    public TransactionDatabase(){
        database = new HashMap<Long, Transaction>();

    }

    public void saveTransaction(Transaction transaction){

        if(transaction == null){

        } else {
            database.put(transaction.getId() ,transaction);
        }
    }



}
