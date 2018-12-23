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


    @Test
    public void testValid_AmericanExpress() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertTrue(check);
    }

    @Test
    public void testValidPrefix_InvalidCardType_AmericanExpress() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.MASTERCARD.toString(), "371449635398431", "11/2020", "1234");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testInvalidPrefix_ValidCardType_AmericanExpress() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "571449635398431", "11/2020", "1234");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }


    @Test
    public void testInvalidCardLength_ValidCardType_AmericanExpress() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "571449635391", "11/2020", "1234");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testValid_MasterCard() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.MASTERCARD.toString(), "5555555555554444", "11/2020", "123");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertTrue(check);
    }


    @Test
    public void testValidCardNumber_InvalidCardType_MasterCard() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "5555555555554444", "11/2020", "123");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testValidCardType_InvalidCardNumber_MasterCard() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.MASTERCARD.toString(), "0555555555554444", "11/2020", "123");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testValidCardType_InvalidCardLength_MasterCard() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.MASTERCARD.toString(), "5555555555", "11/2020", "123");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }
    @Test
    public void testValid_VISA() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2020", "123");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertTrue(check);
    }



    @Test
    public void testValidCardNumber_InvalidCardType_VISA() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.MASTERCARD.toString(), "4111111111111111", "11/2020", "123");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testValidCardType_InvalidCardNumber_VISA() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "1111111111111111", "11/2020", "132");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }


    @Test
    public void testValidCardType_InvalidCardLength_VISA() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "41111111111111", "11/2020", "132");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }



    @Test
    public void test_InvalidCardType() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.INVALID.toString(), "4111111111111111", "11/2020", "132");

        //exercise
        boolean check = offlineVerification.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }


    @Test
    public void test_BlankCardNumber() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "", "11/2020", "Test");

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
    public void testVerifyInvalidDate_withLetters() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/20L0", "123");

        //exercise
        boolean check = offlineVerification.verifyExpiryDate(ccInfo.getCardExpiryDate());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyInvalidDate_withSpecialCharacters() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/20%0", "123");

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

    @Test
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
    public void testVerifyOtherInfoIncluded_InvalidCVV_ContainsLetters() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "4111111111111111", "11/2010", "abc1");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyOtherInfoIncluded_InvalidCVV_ContainsSpecialCharacters() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.MASTERCARD.toString(), "4111111111111111", "11/2010", "!@3");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyOtherInfoIncluded_InvalidCVV_ContainsNoDigits() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "Ad@");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }
}
