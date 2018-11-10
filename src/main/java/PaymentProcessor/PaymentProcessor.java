package PaymentProcessor;

import Bank.BankProxy;
import CardInfo.CCInfo;

import static java.lang.Character.getNumericValue;

public class PaymentProcessor {

    BankProxy bank;
    public PaymentProcessor(BankProxy bank){
        this.bank = bank;
    }

    public int verifyLuhn(String cardNumber){
        try{
            if(cardNumber.length()==0)
                return 1;

            else{
                int value = 0;
                int temp = 0;
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

        return  0;

    }

    public int  processPayment(CCInfo ccInfo, long amount){

        int verifyOperation = verifyLuhn(ccInfo.getCardNumber());
        if(verifyOperation == 0){
            verifyOperation = verifyOffline(ccInfo);
            if(verifyOperation == 0){

                long bankAuth = bank.auth(ccInfo,amount);
                if(bankAuth>0){



                } else if(bankAuth == -1){

                }

            } else  return verifyOperation;
        } else return  verifyOperation;

    return  0;
    }
}

