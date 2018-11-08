package OfflineVerification;

import java.util.concurrent.ExecutionException;

import static java.lang.Character.getNumericValue;

public class OfflineVerification {

    public  OfflineVerification(){

    }

    public boolean verifyLuhn(String cardNumber){
        try{
            if(cardNumber.length()==0)
                return false;

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
                    return  true;
            }

            return  false;

        }catch (Exception e){
            return  false;
        }
    }
}


