package VerifyOffline;

import CardInfo.CCInfo;
import CardInfo.enums.CardTypes;

import javax.smartcardio.Card;
import java.time.LocalDate;

public class VerifyOffline {

    public VerifyOffline(){

    }


    public boolean verifyPrefixAndCardType(String cardNumber, String cardType){

        if(cardNumber.length() <13 || cardType == null)
            return  false;
        else{
            if(cardType.contains(CardTypes.AMERICAN_EXPRESS.toString().toLowerCase()) ){
                if( (cardNumber.substring(0,2).contains("34") || cardNumber.substring(0,2).contains("37")) && cardNumber.length() ==15)
                    return  true;

            } else if (cardType.contains(CardTypes.MASTERCARD.toString().toLowerCase()) ){

                if( (cardNumber.substring(0,2).contains("51") || cardNumber.substring(0,2).contains("52") || cardNumber.substring(0,2).contains("53") || cardNumber.substring(0,2).contains("54") || cardNumber.substring(0,2).contains("55"))
                        && cardNumber.length() ==16)
                    return  true;

            } else if (cardType.contains(CardTypes.VISA.toString().toLowerCase())) {

                if (cardNumber.substring(0, 1).contains("4") && (cardNumber.length() == 16) || cardNumber.length() ==13)
                    return true;
            }
        }
        return false;
    }

   public boolean verifyExpiryDate(String expiryDate){
        try {

            LocalDate currentDate = LocalDate.now();

            int month = Integer.parseInt(expiryDate.substring(0,2));
            int year = Integer.parseInt(expiryDate.substring(3,7));
            LocalDate expiryDate_Date = LocalDate.of(year,month,1);

            if(expiryDate_Date.getYear()>currentDate.getYear())
                return  true;
            else if (expiryDate_Date.getYear()==currentDate.getYear() &&
                    expiryDate_Date.getMonth().getValue()>= currentDate.getMonth().getValue())
                return true;

            return false;
        }catch ( Exception e){
            return  false;
        }
    }

    public boolean verifyInfoPresent(CCInfo ccInfo){

        String CVV = ccInfo.getCardCVV();
        String cardType = ccInfo.getCardType();

        if(ccInfo.getCustomerAddress().length()>0 && ccInfo.getCustomerName().length()>0 && CVV.matches("[0-9]+") )
            if((cardType.contains(CardTypes.AMERICAN_EXPRESS.toString().toLowerCase()) && CVV.length()==4 )||
                    (CVV.length()==3 &&
                            (cardType.contains(CardTypes.VISA.toString().toLowerCase()) ||
                             cardType.contains(CardTypes.MASTERCARD.toString().toLowerCase())
                            )
                    )
                )
                return  true;

        return false;
    }

}

