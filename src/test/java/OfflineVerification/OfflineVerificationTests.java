package OfflineVerification;

import CardInfo.CCInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class OfflineVerificationTests {

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
        ccInfo = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "Test");
        OfflineVerification offlineVerification = new OfflineVerification();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertTrue(check);
    }

    @Test
    public void testVerifyPrefixAndCardType_Valid_MasterCard() {
        ccInfo = new CCInfo("Chris", "222,Test", "Mastercard", "5555555555554444", "11/2020", "Test");
        OfflineVerification offlineVerification = new OfflineVerification();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertTrue(check);
    }

    @Test
    public void testVerifyPrefixAndCardType_Valid_VISA() {
        ccInfo = new CCInfo("Chris", "222,Test", "VISA", "4111111111111111", "11/2020", "Test");
        OfflineVerification offlineVerification = new OfflineVerification();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertTrue(check);
    }


    @Test
    public void testVerifyPrefixAndCardType_Blank() {
        ccInfo = new CCInfo("Chris", "222,Test", "American Express", "0", "11/2020", "Test");
        OfflineVerification offlineVerification = new OfflineVerification();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }


    @Test
    public void testVerifyPrefixAndCardType_InvalidCardType_AmericanExpress() {
        ccInfo = new CCInfo("Chris", "222,Test", "American Expressss", "371449635398431", "11/2020", "Test");
        OfflineVerification offlineVerification = new OfflineVerification();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }

    @Test
    public void testVerifyPrefixAndCardType_InvalidCardNumber_AmericanExpress() {
        ccInfo = new CCInfo("Chris", "222,Test", "American Express", "571449635398431", "11/2020", "Test");
        OfflineVerification offlineVerification = new OfflineVerification();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }


    @Test
    public void testVerifyPrefixAndCardType_InvalidCardType_MasterCard() {
        ccInfo = new CCInfo("Chris", "222,Test", "Mastercardddd", "5555555555554444", "11/2020", "Test");
        OfflineVerification offlineVerification = new OfflineVerification();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }


    @Test
    public void testVerifyPrefixAndCardType_InvalidCardNumber_MasterCard() {
        ccInfo = new CCInfo("Chris", "222,Test", "Mastercard", "0555555555554444", "11/2020", "Test");
        OfflineVerification offlineVerification = new OfflineVerification();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }

    @Test
    public void testVerifyPrefixAndCardType_InvalidCardType_VISA() {
        ccInfo = new CCInfo("Chris", "222,Test", "VISAaaa", "4111111111111111", "11/2020", "Test");
        OfflineVerification offlineVerification = new OfflineVerification();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }

    @Test
    public void testVerifyPrefixAndCardType_InvalidCardNumber_VISA() {
        ccInfo = new CCInfo("Chris", "222,Test", "VISA", "1111111111111111", "11/2020", "Test");
        OfflineVerification offlineVerification = new OfflineVerification();
        boolean check = offlineVerification.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType());

        assertFalse(check);
    }


    @Test
    public void testVerifyExpiryDate_ValidDate() {
        ccInfo = new CCInfo("Chris", "222,Test", "VISA", "4111111111111111", "11/2030", "Test");
        OfflineVerification offlineVerification = new OfflineVerification();
        boolean check = offlineVerification.verifyExpiryDate(ccInfo.getCardExpiryDate());

        assertTrue(check);
    }


    @Test
    public void testVerifyExpiryDate_ExpiredDate() {
        ccInfo = new CCInfo("Chris", "222,Test", "VISA", "4111111111111111", "11/2010", "Test");
        OfflineVerification offlineVerification = new OfflineVerification();
        boolean check = offlineVerification.verifyExpiryDate(ccInfo.getCardExpiryDate());

        assertFalse(check);
    }


    @Test
    public void testVerifyOtherInfoIncluded() {
        ccInfo = new CCInfo("Chris", "222,Test", "VISA", "4111111111111111", "11/2030", "Test");
        OfflineVerification offlineVerification = new OfflineVerification();
        boolean check = offlineVerification.verifyInfoPresent(ccInfo);

        assertTrue(check);
    }


    @Test
    public void testVerifyOtherInfoIncluded_MissingAddress() {
        ccInfo = new CCInfo("Chris", "", "VISA", "4111111111111111", "11/2010", "Test");
        OfflineVerification offlineVerification = new OfflineVerification();
        boolean check = offlineVerification.verifyInfoPresent(ccInfo);

        assertFalse(check);
    }

}
