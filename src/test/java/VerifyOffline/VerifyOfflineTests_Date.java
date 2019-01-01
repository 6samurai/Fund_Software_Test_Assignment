package VerifyOffline;

import CardInfo.CCInfo;
import PaymentProcessor.enums.TestCardTypes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VerifyOfflineTests_Date {
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
    public void testVerifyInvalidDate_withMissingDate() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "", "123");

        //exercise
        boolean check = offlineVerification.verifyExpiryDate(ccInfo.getCardExpiryDate());

        //verify
        assertFalse(check);
    }

}
