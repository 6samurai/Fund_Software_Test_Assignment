package PaymentProcessor.ErrorMessages;

public class InvalidCreditCardDetails extends Exception{

    public InvalidCreditCardDetails(String message){
        super("Invalid credit card details " + message);
    }
}
