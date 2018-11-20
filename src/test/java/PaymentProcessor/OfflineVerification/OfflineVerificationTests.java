package PaymentProcessor.OfflineVerification;

import Bank.BankProxy;
import CardInfo.CCInfo;

import PaymentProcessor.PaymentProcessor;
import TransactionDatabase.TransactionDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OfflineVerificationTests {


    CCInfo ccInfo;
    List<String> logs;


    @Before
    public void setup() {
        logs = new ArrayList<String>();
   }

    @After
    public void teardown() {
        ccInfo =null;
        logs.clear();
    }

    @Test
    public void testValidOfflineVerification_AmericanExpress() {
        boolean errorThrown = false;
        boolean check = false;
        try{
            ccInfo = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "1234");
            PaymentProcessor paymentProcessor = new PaymentProcessor();
            check = paymentProcessor.OfflineVerification(ccInfo);

        }catch (Exception  e){
            errorThrown = true;
        }
        assertTrue(check);
        assertFalse(errorThrown);

    }


    @Test
    public void testValidOfflineVerification_Visa() {
        boolean errorThrown = false;
        boolean check = false;
        try{
            ccInfo = new CCInfo("Chris", "222,Test", "Visa", "4111111111111111", "11/2020", "1234");
            PaymentProcessor paymentProcessor = new PaymentProcessor();
            check = paymentProcessor.OfflineVerification(ccInfo);

        }catch (Exception  e){
            errorThrown = true;
        }
        assertTrue(check);
        assertFalse(errorThrown);

    }


    @Test
    public void testValidOfflineVerification_Mastercard() {
        boolean errorThrown = false;
        boolean check = false;
        try{
            ccInfo = new CCInfo("Chris", "222,Test", "Mastercard", "5105105105105100", "11/2020", "1234");
            PaymentProcessor paymentProcessor = new PaymentProcessor();
            check = paymentProcessor.OfflineVerification(ccInfo);

        }catch (Exception  e){
            errorThrown = true;
        }
        assertTrue(check);
        assertFalse(errorThrown);

    }

    @Test
    public void testInvalidOfflineVerification_CardType_AmericanExpress__CardNumber_Visa() {
        boolean errorThrown = false;
        boolean check = false;
        String errorMessage = "";
        try{
            ccInfo = new CCInfo("Chris", "222,Test", "American Express", "4111111111111111", "11/2020", "1234");
            PaymentProcessor paymentProcessor = new PaymentProcessor();
            check = paymentProcessor.OfflineVerification(ccInfo);

        }catch (Exception  e){
            errorThrown = true;
            errorMessage = e.getMessage();
        }
        assertFalse(check);
        assertTrue(errorThrown);
        assertEquals("Invalid Prefix of card",errorMessage);

    }


    @Test
    public void testInvalidOfflineVerification_ExpiredCard() {
        boolean errorThrown = false;
        boolean check = false;
        String errorMessage = "";
        try{
            ccInfo = new CCInfo("Chris", "222,Test", "Visa", "4111111111111111", "11/2000", "1234");
            PaymentProcessor paymentProcessor = new PaymentProcessor();
            check = paymentProcessor.OfflineVerification(ccInfo);

        }catch (Exception  e){
            errorThrown = true;
            errorMessage = e.getMessage();
        }
        assertFalse(check);
        assertTrue(errorThrown);
        assertEquals("Card is expired",errorMessage);

    }




    @Test
    public void testInvalidOfflineVerification_MissingInformation() {
        boolean errorThrown = false;
        boolean check = false;
        String errorMessage = "";
        try{
            ccInfo = new CCInfo("Chris", "222,Test", "Visa", "4111111111111111", "11/2030", "");
            PaymentProcessor paymentProcessor = new PaymentProcessor();
            check = paymentProcessor.OfflineVerification(ccInfo);

        }catch (Exception  e){
            errorThrown = true;
            errorMessage = e.getMessage();
        }
        assertFalse(check);
        assertTrue(errorThrown);
        assertEquals("Missing card Information",errorMessage);

    }
}
