package OfflineVerification;

import CardInfo.CCInfo;
import PaymentProcessor.PaymentProcessor;
import PaymentProcessor.enums.TestCardTypes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OfflineVerificationTests {

    CCInfo ccInfo;

    boolean errorThrown;
    boolean check;
    PaymentProcessor paymentProcessor;

    @Before
    public void setup() {
        errorThrown = false;
        check = false;
        paymentProcessor = new PaymentProcessor();
    }

    @After
    public void teardown() {
        ccInfo = null;
    }

    @Test
    public void testValid_OfflineVerification_AmericanExpress() {
        try {
            //setup
            ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");

            //exercise
            check = paymentProcessor.OfflineVerification(ccInfo);

        } catch (Exception e) {
            errorThrown = true;
        }

        //verify
        assertTrue(check);

    }

    @Test
    public void testValid_OfflineVerification_Visa() {

        try {
            //setup
            ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2020", "123");

            //exercise
            check = paymentProcessor.OfflineVerification(ccInfo);

        } catch (Exception e) {
            errorThrown = true;
        }

        //verify
        assertTrue(check);
    }

    @Test
    public void testValid_OfflineVerification_Mastercard() {

        try {
            //setup
            ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.MASTERCARD.toString(), "5105105105105100", "11/2020", "124");

            //exercise
            check = paymentProcessor.OfflineVerification(ccInfo);

        } catch (Exception e) {
            errorThrown = true;
        }

        //verify
        assertTrue(check);
    }

    @Test
    public void testInvalid_OfflineVerification_CardType_AmericanExpress_CardNumber_Visa() {
        //setup
        String errorMessage = "";
        try {
            ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "4111111111111111", "11/2020", "1234");

            //exercise
            check = paymentProcessor.OfflineVerification(ccInfo);

        } catch (Exception e) {
            errorThrown = true;
            errorMessage = e.getMessage();
        }

        //verify
        assertFalse(check);
        assertTrue(errorThrown);
        assertEquals("Invalid Prefix of card", errorMessage);
    }

    @Test
    public void testInvalid_OfflineVerification_ExpiredCard() {

        //setup
        String errorMessage = "";
        try {
            ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2000", "1234");

            //exercise
            check = paymentProcessor.OfflineVerification(ccInfo);

        } catch (Exception e) {
            errorThrown = true;
            errorMessage = e.getMessage();
        }

        //verify
        assertFalse(check);
        assertTrue(errorThrown);
        assertEquals("Expired card", errorMessage);

    }

    @Test
    public void testInvalid_OfflineVerification_MissingInformation_Name() {

        //setup
        String errorMessage = "";
        try {
            ccInfo = new CCInfo("", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2030", "123");

            //exercise
            check = paymentProcessor.OfflineVerification(ccInfo);

        } catch (Exception e) {
            errorThrown = true;
            errorMessage = e.getMessage();
        }

        //verify
        assertFalse(check);
        assertTrue(errorThrown);
        assertEquals("Missing Name", errorMessage);

    }


    @Test
    public void testInvalid_OfflineVerification_MissingInformation_Address() {
        //setup
        String errorMessage = "";
        try {
            ccInfo = new CCInfo("Chris", "", TestCardTypes.VISA.toString(), "4111111111111111", "11/2030", "123");

            //exercise
            check = paymentProcessor.OfflineVerification(ccInfo);

        } catch (Exception e) {
            errorThrown = true;
            errorMessage = e.getMessage();
        }

        //verify
        assertFalse(check);
        assertTrue(errorThrown);
        assertEquals("Missing Address", errorMessage);

    }

    @Test
    public void testInvalid_OfflineVerification_MissingInformation_CVV() {
        //setup
        String errorMessage = "";
        try {
            ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2030", "");

            //exercise
            check = paymentProcessor.OfflineVerification(ccInfo);

        } catch (Exception e) {
            errorThrown = true;
            errorMessage = e.getMessage();
        }

        //verify
        assertFalse(check);
        assertTrue(errorThrown);
        assertEquals("Invalid CVV", errorMessage);

    }

    @Test
    public void testInvalid_FailedLuhnTest() {
        //setup
        String errorMessage = "";
        try {
            ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398432", "11/2020", "1234");

            //exercise
            check = paymentProcessor.OfflineVerification(ccInfo);

        } catch (Exception e) {
            errorThrown = true;
            errorMessage = e.getMessage();
        }

        //verify
        assertFalse(check);
        assertTrue(errorThrown);
        assertEquals("Invalid Card Number", errorMessage);

    }
}
