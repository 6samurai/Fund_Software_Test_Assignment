package LuhnTests;

import PaymentProcessor.PaymentProcessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LuhnTests {


    PaymentProcessor paymentProcessor;

    @Before
    public void setup() {
        paymentProcessor = new PaymentProcessor();
    }

    @After
    public void teardown() {
    }


    @Test
    public void testVerifyOperation_LuhnCheck_Valid() {
        //setup
        String value = "371449635398431";
        //exercise
        boolean check = paymentProcessor.verifyLuhn(value);

        //verify
        assertTrue(check);

    }

    @Test
    public void testVerifyOperation_LuhnCheck_Blank() {
        //setup
     String value = "";
        //exercise
        boolean check = paymentProcessor.verifyLuhn(value);

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyOperation_LuhnCheck_InvalidValue() {
        //setup
        String value = "2132132131312321";
        //exercise
        boolean check = paymentProcessor.verifyLuhn(value);

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyOperation_LuhnCheck_Invalid_LetterCharacters() {
        //setup
        String value = "3714496353984dddd31";
        //exercise
        boolean check = paymentProcessor.verifyLuhn(value);

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyOperation_LuhnCheck_Invalid_SpecialCharacters() {
        //setup
        String value = "371449635398!!@#!431";
        //exercise
        boolean check = paymentProcessor.verifyLuhn(value);

        //verify
        assertFalse(check);
    }

}
