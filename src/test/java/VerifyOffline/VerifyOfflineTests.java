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
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "Test");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertTrue(check);
    }

    @Test
    public void testVerifyPrefixAndCardType_Valid_MasterCard() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.MASTERCARD.toString(), "5555555555554444", "11/2020", "Test");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertTrue(check);
    }

    @Test
    public void testVerifyPrefixAndCardType_Valid_VISA() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2020", "Test");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertTrue(check);
    }


    @Test
    public void testVerifyPrefixAndCardType_Blank() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "0", "11/2020", "Test");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }


    @Test
    public void testVerifyPrefixAndCardType_InvalidCardType_AmericanExpress() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.INVALID.toString(), "371449635398431", "11/2020", "Test");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }

    @Test
    public void testVerifyPrefixAndCardType_InvalidCardNumber_AmericanExpress() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "571449635398431", "11/2020", "Test");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }


    @Test
    public void testVerifyPrefixAndCardType_InvalidCardType_MasterCard() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.INVALID.toString(), "5555555555554444", "11/2020", "Test");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }


    @Test
    public void testVerifyPrefixAndCardType_InvalidCardNumber_MasterCard() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.MASTERCARD.toString(), "0555555555554444", "11/2020", "Test");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }

    @Test
    public void testVerifyPrefixAndCardType_InvalidCardType_VISA() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.INVALID.toString(), "4111111111111111", "11/2020", "Test");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }

    @Test
    public void testVerifyPrefixAndCardType_InvalidCardNumber_VISA() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "1111111111111111", "11/2020", "Test");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }


    @Test
    public void testVerifyExpiryDate_ValidDate() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2030", "Test");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyExpiryDate(ccInfo.getCardExpiryDate());

        assertTrue(check);
    }


    @Test
    public void testVerifyExpiryDate_ExpiredDate() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "Test");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyExpiryDate(ccInfo.getCardExpiryDate());

        assertFalse(check);
    }


    @Test
    public void testVerifyOtherInfoIncluded() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2030", "123");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyInfoPresent(ccInfo);

        assertTrue(check);
    }


    @Test
    public void testVerifyOtherInfoIncluded_MissingAddress() {
        ccInfo = new CCInfo("Chris", "", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "Test");
        VerifyOffline offlineVerification = new VerifyOffline();
        boolean check = offlineVerification.verifyInfoPresent(ccInfo);

        assertFalse(check);
    }

}
