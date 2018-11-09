package OfflineVerification;

import java.time.LocalDate;

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

    public boolean verifyPrefixAndCardType(String cardNumber, String cardType){

        if(cardNumber.length() <13 || cardType.length() ==0)
            return  false;
        else{
            if(cardType.contains("American Express") && cardType.length() ==16 ){
                if( (cardNumber.substring(0,2).contains("34") || cardNumber.substring(0,2).contains("37")) && cardNumber.length() ==15)
                    return  true;

            } else if (cardType.contains("Mastercard") && cardType.length() ==10 ){

                if( (cardNumber.substring(0,2).contains("51") || cardNumber.substring(0,2).contains("52") || cardNumber.substring(0,2).contains("53") || cardNumber.substring(0,2).contains("54") || cardNumber.substring(0,2).contains("55"))
                        && cardNumber.length() ==16)
                    return  true;

            } else if (cardType.contains("VISA") && cardType.length() ==4 ) {

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

        /*    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yy");
       //     LocalDate expiryDate_Date =  formatter.parse(expiryDate);
            LocalDate localDate = LocalDate.parse(expiryDate, formatter);*/

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

 /*   public boolean verifyExpiryDate(String expiryDate){

            DateFormat dateFormat = new SimpleDateFormat("MM/yy");
            Date currentDate = new Date();

            String reportDate = dateFormat.format(currentDate)

        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int month = localDate.getMonthValue();


            if(currentDate.before(todayDate) || currentDate.equals(todayDate)){
                return  true;
            }

            return false;

    }*/

   /* public boolean verifyInfoPresent(String customerName, String customerAddress, String cardCVV){


        return false;
    }*/

}


