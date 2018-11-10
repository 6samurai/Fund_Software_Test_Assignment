package PaymentProcessor.ErrorMessages;

public class InsufficientFunds extends Exception{

    public InsufficientFunds(String message){
        super("The account does not have sufficient funds " + message);
    }
}
