package LuhnTests;

import CardInfo.CCInfo;
import CardInfo.enums.CardTypes;
import PaymentProcessor.PaymentProcessor;
import PaymentProcessor.enums.TestCardTypes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LuhnTests {

    CCInfo ccInfo;
    PaymentProcessor paymentProcessor;

    @Before
    public void setup() {
        paymentProcessor = new PaymentProcessor();
    }

    @After
    public void teardown() {
        ccInfo = null;
    }


    @Test
    public void testVerifyOperation_LuhnCheck_AmericanExpressCardNo() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");

        //exercise
        boolean check = paymentProcessor.verifyLuhn(ccInfo.getCardNumber());

        //verify
        assertTrue(check);

    }

    @Test
    public void testVerifyOperation_LuhnCheck_MasterCardCardNo() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", CardTypes.MASTERCARD.toString(), "5555555555554444", "11/2020", "124");

        //exercise
        boolean check = paymentProcessor.verifyLuhn(ccInfo.getCardNumber());

        //verify
        assertTrue(check);

    }

    @Test
    public void testVerifyOperation_LuhnCheck_VisaCardNo() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", CardTypes.VISA.toString(), "4111111111111111", "11/2020", "123");

        //exercise
        boolean check = paymentProcessor.verifyLuhn(ccInfo.getCardNumber());

        //verify
        assertTrue(check);

    }

    @Test
    public void testVerifyOperation_LuhnCheck_Blank() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "", "11/2020", "1234");

        //exercise
        boolean check = paymentProcessor.verifyLuhn(ccInfo.getCardNumber());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyOperation_LuhnCheck_InvalidValue() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "2132132131312321", "11/2020", "1234");

        //exercise
        boolean check = paymentProcessor.verifyLuhn(ccInfo.getCardNumber());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyOperation_LuhnCheck_Invalid_LetterCharacters() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "3714496353984dddd31", "11/2020", "1234");

        //exercise
        boolean check = paymentProcessor.verifyLuhn(ccInfo.getCardNumber());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyOperation_LuhnCheck_Invalid_SpecialCharacters() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398!!@#!431", "11/2020", "1234");

        //exercise
        boolean check = paymentProcessor.verifyLuhn(ccInfo.getCardNumber());

        //verify
        assertFalse(check);
    }

    @Test
    public void testVerifyOperation_LuhnCheck_Invalid_NoDigits() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "assererwer!!@#!431", "11/2020", "1234");

        //exercise
        boolean check = paymentProcessor.verifyLuhn(ccInfo.getCardNumber());

        //verify
        assertFalse(check);
    }
}
