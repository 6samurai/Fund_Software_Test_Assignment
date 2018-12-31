package VerifyOffline;

import CardInfo.CCInfo;
import PaymentProcessor.enums.TestCardTypes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VerifyOffline_CVV {

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
    public void testVerifyCVV_CardType_AmericanExpress_Length_3() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "4111111111111111", "11/2010", "123");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyCVV_CardType_AmericanExpress_Length_4() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "4111111111111111", "11/2010", "1233");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertTrue(check);
    }

    @Test
    public void testVerifyCVV_CardType_AmericanExpress_Length_Invalid() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "4111111111111111", "11/2010", "1233333333");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyCVV_CardType_VISA_Length_3() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "123");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertTrue(check);
    }

    @Test
    public void testVerifyCVV_CardType_VISA_Length_4() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "1233");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyCVV_CardType_VISA_Length_Invalid() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2010", "1233333333");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }


    @Test
    public void testVerifyCVV_CardType_Mastercard_Length_3() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.MASTERCARD.toString(), "4111111111111111", "11/2010", "124");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertTrue(check);
    }

    @Test
    public void testVerifyCVV_CardType_Mastercard_Length_4() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.MASTERCARD.toString(), "4111111111111111", "11/2010", "1233");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyCVV_CardType_Mastercard_Length_Invalid() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", TestCardTypes.MASTERCARD.toString(), "4111111111111111", "11/2010", "1233333333");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyCVV_CardType_Invalid_Length_3() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", "Invalid", "4111111111111111", "11/2010", "123");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyCVV_CardType_Invalid_Length_4() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", "Invalid", "4111111111111111", "11/2010", "1233");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyCVV_CardType_Invalid_Length_Invalid() {
        //setup
        ccInfo = new CCInfo("Chris", "222, Test", "Invalid", "4111111111111111", "11/2010", "1233333333");

        //exercise
        boolean check = offlineVerification.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType());

        //verify
        assertFalse(check);
    }



}
