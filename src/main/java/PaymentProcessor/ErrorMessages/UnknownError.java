package PaymentProcessor.ErrorMessages;

public class UnknownError  extends Exception{

    public UnknownError(){
        super("An unknown error has occurred ");
    }
}
