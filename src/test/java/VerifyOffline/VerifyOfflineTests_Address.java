package VerifyOffline;

import CardInfo.CCInfo;
import PaymentProcessor.enums.TestCardTypes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VerifyOfflineTests_Address {

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
}
