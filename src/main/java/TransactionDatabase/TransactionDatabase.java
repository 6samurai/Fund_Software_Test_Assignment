package TransactionDatabase;
import java.util.HashMap;
import java.util.List;
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

    public Transaction getTransaction(long transactionId){


        /*    Transaction getTrans =  database.get(id);
            if(getTrans != null)
                return  getTrans;
            else return  null;
*/
            long id = -1L;
            Transaction getTran;

            for(long i = 0; i <database.size(); i++){
                   getTran = database.get(i);
                   if(getTran.getTransactionId().equals(transactionId))
                       id = i;
            }
            // Return the list of keys whose value matches with given value.
            if(id != -1L)
                return database.get(id);

        return  null;

    }

    
    public int countTransactions(){
        return  database.size();
    }

}
