package PaymentProcessor.ErrorMessages;

public class BankError extends Exception {

    public BankError() {
        super("Bank is offline");
    }
}
