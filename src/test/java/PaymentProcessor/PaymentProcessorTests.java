package PaymentProcessor;

import Bank.BankProxy;
import CardInfo.CCInfo;
import PaymentProcessor.Enums.BankOperations;
import PaymentProcessor.ErrorMessages.UnknownError;
import PaymentProcessor.ErrorMessages.UserError;
import TransactionDatabase.TransactionDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaymentProcessorTests {

    CCInfo ccInfo;
    TransactionDatabase transactionDB;
    BankProxy bank;
    List<String> logs;

    @Before
    public void setup() {
        transactionDB = new TransactionDatabase();
        logs = new ArrayList<String>();
    }

    @After
    public void teardown() {
        transactionDB = null;
        ccInfo = null;
        logs.clear();
    }


    @Test
    public void testVerifyOperation_LuhnCheck() {
        ccInfo = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "1234");
        PaymentProcessor paymentProcessor = new PaymentProcessor();
        boolean check = paymentProcessor.verifyLuhn(ccInfo.getCardNumber());
        assertTrue(check);

    }

    @Test
    public void testVerifyOperation_LuhnCheck_Blank() {
        ccInfo = new CCInfo("Chris", "222,Test", "American Express", "", "11/2020", "1234");
        PaymentProcessor paymentProcessor = new PaymentProcessor();
        boolean check = paymentProcessor.verifyLuhn(ccInfo.getCardNumber());
        assertFalse(check);
    }

    @Test
    public void testVerifyOperation_LuhnCheck_InvalidValue() {
        ccInfo = new CCInfo("Chris", "222,Test", "American Express", "2132132131312321", "11/2020", "1234");
        PaymentProcessor paymentProcessor = new PaymentProcessor();
        boolean check = paymentProcessor.verifyLuhn(ccInfo.getCardNumber());
        assertFalse(check);
    }


    /* @Test
     public void testInvalidAuthorisationRequest_InvalidCreditCardDetails() throws UserError{
         boolean errorThrown = false;
         try{

             //setup
             ccInfo = new CCInfo("Chris","222,Test","American Express","371449635398431","11/2020","1234");
             long amount = 1000L;
             BankProxy bank = mock(BankProxy.class);
             when(bank.auth(ccInfo,amount)).thenReturn(-1L);
             PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionDB,BankOperations.AUTHORISE);

             //exercise
             paymentProcessor.processPayment(ccInfo,amount);

         }catch (UserError e){
             errorThrown = true;
         }

         //verify
         assertTrue(errorThrown);
     }*/
    @Test
    public void testInvalidAuthorisationRequest_InvalidCreditCardDetails() {
        boolean errorThrown = false;

        //setup
        ccInfo = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "1234");
        long amount = 1000L;
        BankProxy bank = mock(BankProxy.class);
        when(bank.auth(ccInfo, amount)).thenReturn(-1L);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, BankOperations.AUTHORISE, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);


        //verify
        assertEquals(1,result);
        assertTrue(logs.contains("Credit card details are invalid"));
    }

  /*  @Test
    public void testInvalidAuthorisationRequest_InsufficientFunds() throws Exception {
        boolean errorThrown = false;
        try {

            //setup
            ccInfo = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "1234");
            long amount = 1000L;
            BankProxy bank = mock(BankProxy.class);
            when(bank.auth(ccInfo, amount)).thenReturn(-2L);
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, BankOperations.AUTHORISE, logs);

            //exercise
            paymentProcessor.processPayment(ccInfo, amount);

        } catch (UserError e) {
            errorThrown = true;
        }

        //verify
        assertTrue(errorThrown);
    }

    @Test
    public void testInvalidAuthorisationRequest_UnknownError() throws Exception {
        boolean errorThrown = false;
        try {

            //setup
            ccInfo = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "1234");
            long amount = 1000L;
            BankProxy bank = mock(BankProxy.class);
            when(bank.auth(ccInfo, amount)).thenReturn(-3L);
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, BankOperations.AUTHORISE, logs);

            //exercise
            int result = paymentProcessor.processPayment(ccInfo, amount);

        } catch (UnknownError e) {
            errorThrown = true;
        }

        //verify
        assertTrue(errorThrown);
    }*/

    @Test
    public void testValidAuthorisationRequest() throws Exception {


        //setup
        ccInfo = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "1234");
        // Transaction transaction = new Transaction();
        long amount = 1000L;
        BankProxy bank = mock(BankProxy.class);
        when(bank.auth(ccInfo, 1000)).thenReturn(10000L);

        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, BankOperations.AUTHORISE, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        assertEquals(0, result);
    }

}
