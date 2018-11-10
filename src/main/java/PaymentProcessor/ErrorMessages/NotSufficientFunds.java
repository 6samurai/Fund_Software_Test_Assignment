package PaymentProcessor.ErrorMessages;

public class NotSufficientFunds extends Exception{

    public NotSufficientFunds(String message){
        super("The account does not have sufficient funds " + message);
    }
}
