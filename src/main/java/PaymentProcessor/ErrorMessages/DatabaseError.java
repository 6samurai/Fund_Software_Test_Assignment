package PaymentProcessor.ErrorMessages;

public class DatabaseError extends Exception {

    public DatabaseError() {
        super("Database is offline");
    }
}
