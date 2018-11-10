package PaymentProcessor;

import Bank.BankProxy;
import CardInfo.CCInfo;
import OfflineVerification.OfflineVerification;
import PaymentProcessor.ErrorMessages.InsufficientFunds;
import PaymentProcessor.ErrorMessages.InvalidCreditCardDetails;
import PaymentProcessor.ErrorMessages.UnknownError;
import TransactionDatabase.*;

import static java.lang.Character.getNumericValue;

public class PaymentProcessor {

    BankProxy bank;
    TransactionDatabase transactionDB;
    String operation;

    public PaymentProcessor(BankProxy bank, TransactionDatabase transactionDB,String operation){
        this.bank = bank;
        this.transactionDB = transactionDB;
        this.operation = operation;
    }


    public PaymentProcessor(){

    }

    public int verifyLuhn(String cardNumber){
        try{
            if(cardNumber.length()==0)
                return 1;

            else{
                int value;
                int temp;
                int total = 0;
                for(int i = cardNumber.length()-1;i>=0;i--){

                    value = getNumericValue(cardNumber.charAt(i));
                    if((cardNumber.length() - i)%2 == 0 ){
                        temp = value *2;
                        if(temp>9){
                            value = temp - 9;
                        }  else value = temp;
                    }
                    total = total + value;
                }
                if(total%10 ==0)
                    return  0;
            }

            return  1;

        }catch (Exception e){
            return  2;
        }
    }

    public  int verifyOffline(CCInfo ccInfo){

        try{
            OfflineVerification offlineVerification = new OfflineVerification();
            if(offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(),ccInfo.getCardType()))
                if(offlineVerification.verifyExpiryDate(ccInfo.getCardExpiryDate()))
                    if(offlineVerification.verifyInfoPresent(ccInfo))
                        return  0;
            return  -1;
        }catch (Exception e){
            return  -2;
        }

    }

    public int  processPayment(CCInfo ccInfo, long amount) throws Exception{
        String msg = "";
        long getTransactionID =  Long.parseLong(ccInfo.getCardNumber());

        Transaction currentTransaction = new Transaction(getTransactionID,ccInfo,amount,"");

        int verifyOperation = verifyLuhn(ccInfo.getCardNumber());
        if(verifyOperation == 0){
            verifyOperation = verifyOffline(ccInfo);
            if(verifyOperation == 0){

                long bankAction = bank.auth(ccInfo,amount);
                if(bankAction>0){
                    currentTransaction.setState("authorise");
                    transactionDB.saveTransaction(currentTransaction);
                    if(operation.contains("authorise"))
                        return  0;

                    if(operation.contains("capture")){
                        bankAction = bank.capture(amount);
                        if(bankAction ==0){


                        } else if(bankAction == -1){


                        } else  if(bankAction == -2){


                        } else if (bankAction == -3){



                        } else if(bankAction == -4) {



                        }

                    } else if(operation.contains("refund")){


                        bankAction = bank.refund(getTransactionID, amount);
                        if(bankAction ==0){


                        } else if(bankAction == -1){


                        } else  if(bankAction == -2){


                        } else if (bankAction == -3){



                        } else if(bankAction == -4) {



                        }
                    }
                } else if(bankAction == -1){
                    throw new InvalidCreditCardDetails("");
                } else if(bankAction == -2){
                    throw new InsufficientFunds("");
                } else if (bankAction == -3){
                    throw new UnknownError("");
                }

            } else  return verifyOperation;
        } else return  verifyOperation;

    return  0;
    }
}

