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

    @Before
    public void setup() {

    }

    @After
    public void teardown() {
        ccInfo = null;
    }


    @Test
    public void testVerifyPrefixAndCardType_Valid_AmericanExpress() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertTrue(check);
    }

    @Test
    public void testVerifyPrefixAndCardType_Valid_MasterCard() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.MASTERCARD.toString(), "5555555555554444", "11/2020", "123");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertTrue(check);
    }

    @Test
    public void testVerifyPrefixAndCardType_Valid_VISA() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2020", "123");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertTrue(check);
    }



    @Test
    public void testVerifyPrefixAndCardType_ValidPrefix_InvalidCardType_AmericanExpress() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.INVALID.toString(), "371449635398431", "11/2020", "1234");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }

    @Test
    public void testVerifyPrefixAndCardType_InvalidPrefix_ValidCardType_AmericanExpress() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "571449635398431", "11/2020", "1234");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }


    @Test
    public void testVerifyPrefixAndCardType_InvalidCardType_MasterCard() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.INVALID.toString(), "5555555555554444", "11/2020", "123");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }


    @Test
    public void testVerifyPrefixAndCardType_InvalidCardNumber_MasterCard() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.MASTERCARD.toString(), "0555555555554444", "11/2020", "123");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }

    @Test
    public void testVerifyPrefixAndCardType_InvalidCardType_VISA() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.INVALID.toString(), "4111111111111111", "11/2020", "123");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }

    @Test
    public void testVerifyPrefixAndCardType_InvalidCardNumber_VISA() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "1111111111111111", "11/2020", "132");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }


    @Test
    public void testVerifyPrefixAndCardType_BlankCardNumber() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "", "11/2020", "Test");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }


    @Test
    public void testVerifyExpiryDate_ValidDate() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2030", "123");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyExpiryDate(ccInfo.getCardExpiryDate());

        assertTrue(check);
    }


    @Test
    public void testVerifyExpiryDate_ExpiredDate() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "123");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyExpiryDate(ccInfo.getCardExpiryDate());

        assertFalse(check);
    }


 /*   @Test
    public void testVerifyOtherInfo_Valid() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2030", "123");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyInfoPresent(ccInfo);

        assertTrue(check);
    }*/

    @Test
    public void testVerifyAddress_ValidAddress() {
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "123");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyAddress(ccInfo.getCustomerAddress());

        assertTrue(check);
    }

    @Test
    public void testVerifyAddress_MissingAddress() {
        ccInfo = new CCInfo("Chris", "", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "123");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyAddress(ccInfo.getCustomerAddress());

        assertFalse(check);
    }

    @Test
    public void testVerifyName_ValidName() {
        ccInfo = new CCInfo("Test", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "123");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyName(ccInfo.getCustomerName());

        assertTrue(check);
    }

    @Test
    public void testVerifyName_MissingName() {
        ccInfo = new CCInfo("", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "123");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyName(ccInfo.getCustomerName());

        assertFalse(check);
    }

    @Test
    public void testVerifyOtherInfoIncluded_ValidCVV_AmericanExpress() {
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "4111111111111111", "11/2010", "1234");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(),ccInfo.getCardType());

        assertTrue(check);
    }
    @Test
    public void testVerifyOtherInfoIncluded_ValidCVV_Mastercard() {
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.MASTERCARD.toString(), "4111111111111111", "11/2010", "123");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(),ccInfo.getCardType());

        assertTrue(check);
    }
    @Test
    public void testVerifyOtherInfoIncluded_ValidCVV_VISA() {
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "123");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(),ccInfo.getCardType());

        assertTrue(check);
    }

    @Test
    public void testVerifyOtherInfoIncluded_MissingCVV() {
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(),ccInfo.getCardType());

        assertFalse(check);
    }

    @Test
    public void testVerifyOtherInfoIncluded_InvalidCVV_AmericanExpress() {
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "4111111111111111", "11/2010", "1233333333");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(),ccInfo.getCardType());

        assertFalse(check);
    }
    @Test
    public void testVerifyOtherInfoIncluded_InvalidCVV_Mastercard() {
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.MASTERCARD.toString(), "4111111111111111", "11/2010", "1211111113");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(),ccInfo.getCardType());

        assertFalse(check);
    }
    @Test
    public void testVerifyOtherInfoIncluded_InvalidCVV_VISA() {
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "12");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(),ccInfo.getCardType());

        assertFalse(check);
    }

    @Test
    public void testVerifyOtherInfoIncluded_InvalidCVV_ContainsLetters() {
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "4111111111111111", "11/2010", "abc1");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(),ccInfo.getCardType());

        assertFalse(check);
    }
    @Test
    public void testVerifyOtherInfoIncluded_InvalidCVV_ContainsSpecialCharacters() {
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.MASTERCARD.toString(), "4111111111111111", "11/2010", "!@3");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(),ccInfo.getCardType());

        assertFalse(check);
    }
    @Test
    public void testVerifyOtherInfoIncluded_InvalidCVV_ContainsNoDigits() {
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "Ad@");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(),ccInfo.getCardType());

        assertFalse(check);
    }
}
