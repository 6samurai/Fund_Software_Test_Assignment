package VerifyOffline;

import CardInfo.enums.CardTypes;

import java.util.Calendar;

public class VerifyOffline {
    //Verifies that prefix of card matches the respective card type and card length
    public boolean verifyPrefix_CardType_CardLength(String cardNumber, String cardType) {

        if (cardNumber.length() < 13 || cardType == null)
            return false;
        else {
            if (cardType.contains(CardTypes.AMERICAN_EXPRESS.toString().toLowerCase())) {
                if ((cardNumber.substring(0, 2).contains("34") || cardNumber.substring(0, 2).contains("37")) && cardNumber.length() == 15)
                    return true;

            } else if (cardType.contains(CardTypes.MASTERCARD.toString().toLowerCase())) {

                if ((cardNumber.substring(0, 2).contains("51") || cardNumber.substring(0, 2).contains("52") || cardNumber.substring(0, 2).contains("53") || cardNumber.substring(0, 2).contains("54") || cardNumber.substring(0, 2).contains("55"))
                        && cardNumber.length() == 16)
                    return true;

            } else if (cardType.contains(CardTypes.VISA.toString().toLowerCase())) {

                if (cardNumber.substring(0, 1).contains("4") && (cardNumber.length() == 16) || cardNumber.length() == 13)
                    return true;
            }
        }
        return false;
    }

    //Verifies that expiry date of card is greater than or equal to current month and year
    public boolean verifyExpiryDate(String expiryDate) {

        try{
            //get present date
            Calendar presentDate = Calendar.getInstance();
            //sets time values to 0
            presentDate.set(Calendar.MINUTE, 0);
            presentDate.set(Calendar.SECOND, 0);
            presentDate.set(Calendar.HOUR_OF_DAY, 0);

            //extracts month value from string
            int month = Integer.parseInt(expiryDate.substring(0, 2));
            //extract year value from string
            int year = Integer.parseInt(expiryDate.substring(3, 7));

            //sets the value of teh expiry date
            Calendar expiryDateCal = Calendar.getInstance();
            expiryDateCal.set(Calendar.MONTH, month);
            expiryDateCal.set(Calendar.YEAR, year);
            //sets time values to 0
            expiryDateCal.set(Calendar.MINUTE, 0);
            expiryDateCal.set(Calendar.SECOND, 0);
            expiryDateCal.set(Calendar.HOUR_OF_DAY, 0);


            if (expiryDateCal.compareTo(presentDate) >= 0) {

                return true;
            }
            return false;

        } catch (Exception e){
            return  false;
        }

    }

    //verifies that there is some values for Name
    public boolean verifyName(String name) {
        if (name.length() > 0)
            return true;
        return false;
    }

    //verifies that there is some values for Address
    public boolean verifyAddress(String address) {
        if (address.length() > 0)
            return true;
        return false;
    }

    //verify CVV with respective card type
    public boolean verifyCVV(String cvv, String cardType) {
        //verify that only integers are present
        if (cvv.matches("[0-9]+") && cardType!=null)
            if ((cardType.contains(CardTypes.AMERICAN_EXPRESS.toString().toLowerCase()) && cvv.length() == 4) ||
                    (cvv.length() == 3 &&
                            (cardType.contains(CardTypes.VISA.toString().toLowerCase()) ||
                                    cardType.contains(CardTypes.MASTERCARD.toString().toLowerCase())
                            )
                    )
            )
            {
                return true;
            }
        return false;
    }
}

