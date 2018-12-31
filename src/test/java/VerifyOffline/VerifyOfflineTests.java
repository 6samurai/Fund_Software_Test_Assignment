package VerifyOffline;

import CardInfo.CCInfo;
import PaymentProcessor.enums.TestCardTypes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VerifyOfflineTests {

    CCInfo ccInfo;
    VerifyOffline offlineVerification;

    @Before
    public void setup() {
        offlineVerification = new VerifyOffline();
    }

    @After
    public void teardown() {

        ccInfo = null;
        offlineVerification = null;
    }


   /* @Test
    public void testCardType_AmericanExpress_CardPrefix_37_CardLength_15() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertTrue(check);
    }

    @Test
    public void testCardType_AmericanExpress_CardPrefix_55_CardLength_13() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "5514496353984", "11/2020", "1234");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }


    @Test
    public void testCardType_AmericanExpress_CardPrefix_4_CardLength_Invalid() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "453984311", "11/2020", "1234");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testCardType_AmericanExpress_CardPrefix_Invalid_CardLength_16() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "3714496353121256", "11/2020", "1234");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testCardType_Mastercard_CardPrefix_37_CardLength_Invalid() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.MASTERCARD.toString(), "3755555", "11/2020", "123");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testCardType_Mastercard_CardPrefix_55_CardLength_16() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.MASTERCARD.toString(), "5555515555555444", "11/2020", "123");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertTrue(check);
    }

    @Test
    public void testCardType_Mastercard_CardPrefix_4_CardLength_15() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.MASTERCARD.toString(), "455555555554444", "11/2020", "123");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testCardType_Mastercard_CardPrefix_Invalid_CardLength_13() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.MASTERCARD.toString(), "5555555554444", "11/2020", "123");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testCardType_Visa_CardPrefix_37_CardLength_16() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "3712345678914781", "11/2020", "123");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }
    @Test
    public void testCardType_Visa_CardPrefix_55_CardLength_Invalid() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "551210", "11/2020", "123");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }
    @Test
    public void testCardType_Visa_CardPrefix_4_CardLength_13() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111", "11/2020", "123");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertTrue(check);
    }

    @Test
    public void testCardType_Visa_CardPrefix_Invalid_CardLength_15() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "611111111111111", "11/2020", "123");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testCardType_Invalid_CardPrefix_37_CardLength_13() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", "Invalid", "3712345678912", "11/2020", "123");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }
    @Test
    public void testCardType_Invalid_CardPrefix_55_CardLength_15() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test","Invalid", "553712345678912", "11/2020", "123");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }
    @Test
    public void testCardType_Invalid_CardPrefix_4_CardLength_16() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", "Invalid", "4111111111111111", "11/2020", "123");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testCardType_Invalid_CardPrefix_Invalid_CardLength_Invalid() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test","Invalid", "111411111", "11/2020", "123");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyExpiryDate_ValidDate() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2030", "123");

        //exercise
        boolean check = offlineVerification.verifyExpiryDate(ccInfo.getCardExpiryDate());

        //verify
        assertTrue(check);
    }

    @Test
    public void testVerifyExpiryDate_ExpiredDate() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "123");

        //exercise
        boolean check = offlineVerification.verifyExpiryDate(ccInfo.getCardExpiryDate());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyInvalidDate_withInvalidCharacters() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/20L$", "123");

        //exercise
        boolean check = offlineVerification.verifyExpiryDate(ccInfo.getCardExpiryDate());

        //verify
        assertFalse(check);
    }


    @Test
    public void testVerifyAddress_ValidAddress() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "123");

        //exercise
        boolean check = offlineVerification.verifyAddress(ccInfo.getCustomerAddress());

        //verify
        assertTrue(check);
    }

    @Test
    public void testVerifyAddress_MissingAddress() {
        //setup
        ccInfo = new CCInfo("Chris", "", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "123");

        //exercise
        boolean check = offlineVerification.verifyAddress(ccInfo.getCustomerAddress());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyName_ValidName() {
        //setup
        ccInfo = new CCInfo("Test", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "123");

        //exercise
        boolean check = offlineVerification.verifyName(ccInfo.getCustomerName());

        //verify
        assertTrue(check);
    }

    @Test
    public void testVerifyName_MissingName() {
        //setup
        ccInfo = new CCInfo("", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "123");

        //exercise
        boolean check = offlineVerification.verifyName(ccInfo.getCustomerName());

        //verify
        assertFalse(check);
    }
*/
/*    @Test
    public void testVerifyOtherInfoIncluded_ValidCVV_AmericanExpress() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "4111111111111111", "11/2010", "1234");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertTrue(check);
    }

    @Test
    public void testVerifyOtherInfoIncluded_ValidCVV_Mastercard() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.MASTERCARD.toString(), "4111111111111111", "11/2010", "123");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertTrue(check);
    }

    @Test
    public void testVerifyOtherInfoIncluded_ValidCVV_VISA() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "123");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertTrue(check);
    }

    @Test
    public void testVerifyOtherInfoIncluded_MissingCVV() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyOtherInfoIncluded_InvalidCVV_AmericanExpress() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "4111111111111111", "11/2010", "1233333333");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyOtherInfoIncluded_InvalidCVV_Mastercard() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.MASTERCARD.toString(), "4111111111111111", "11/2010", "1211111113");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyOtherInfoIncluded_InvalidCVV_VISA() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "12");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyOtherInfoIncluded_InvalidCVV_InvalidCharacters() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "4111111111111111", "11/2010", "abc%");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }
*/

}
