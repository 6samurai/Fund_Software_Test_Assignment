package VerifyOffline;

import CardInfo.CCInfo;
import CardInfo.enums.CardTypes;

import javax.smartcardio.Card;
import java.time.LocalDate;
import java.util.Calendar;

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

        Calendar presentDate = Calendar.getInstance();

        presentDate.set(Calendar.MINUTE, 0);
        presentDate.set(Calendar.SECOND, 0);
        presentDate.set(Calendar.HOUR_OF_DAY, 0);

        int month = Integer.parseInt(expiryDate.substring(0,2));
        int year = Integer.parseInt(expiryDate.substring(3,7));
        Calendar expiryDateCal = Calendar.getInstance();
        expiryDateCal.set(Calendar.MONTH,month);
        expiryDateCal.set(Calendar.YEAR,year);

        if(expiryDateCal.compareTo(presentDate) >= 0){

            return  true;
        }
        return  false;


    }

    public boolean verifyName(String name){
        if(name.length()>0)
            return  true;
        return  false;
    }

    public boolean verifyAddress(String address){
        if (address.length()>0)
            return true;
        return  false;
    }

    public boolean verifyCVV(String cvv, String cardType){
        if( cvv.matches("[0-9]+") )
            if((cardType.contains(CardTypes.AMERICAN_EXPRESS.toString().toLowerCase()) && cvv.length()==4 )||
                    (cvv.length()==3 &&
                            (cardType.contains(CardTypes.VISA.toString().toLowerCase()) ||
                                    cardType.contains(CardTypes.MASTERCARD.toString().toLowerCase())
                            )
                    )
            )
                return  true;

        return false;
    }


}

