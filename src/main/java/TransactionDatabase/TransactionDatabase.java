package TransactionDatabase;

import java.util.HashMap;
import java.util.Map;

public class TransactionDatabase {

    Map<Long, Transaction> database;
    //Transaction database
    public TransactionDatabase() {
        database = new HashMap<Long, Transaction>();
    }
    //save transaction to database
    public void saveTransaction(Transaction transaction) {

        if (transaction == null) {

        } else {
            database.put(transaction.getId(), transaction);
        }
    }
    //get the latest transaction for the specified transaction ID
    public Transaction getTransactionByTransactionID(long transactionId) {

        long id = -1L;
        Transaction getTran;

        for (long i = 0; i < database.size(); i++) {
            getTran = database.get(i);
            if (getTran.getTransactionId().equals(transactionId))
                id = i;
        }
        // Return the list of keys whose value matches with given value.
        if (id != -1L)
            return database.get(id);

        return null;
    }

    public int countTransactions() {
        return database.size();
    }

}
