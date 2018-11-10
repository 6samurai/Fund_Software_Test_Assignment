package PaymentProcessor.ErrorMessages;

public class UnknownError  extends Exception{

    public UnknownError(String message){
        super("An unknown error has occurred " + message);
    }
}
