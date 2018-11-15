package PaymentProcessor;

import Bank.BankProxy;
import CardInfo.CCInfo;
import PaymentProcessor.Enums.BankOperations;
import TransactionDatabase.TransactionDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaymentProcessorTests {

    CCInfo ccInfo;
    TransactionDatabase transactionDB;
    BankProxy bank;
    List<String> logs;
    Long transactionID;
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
    public void testInvalidAuthorisationRequest_InvalidCreditCardDetails() {
        //setup
        ccInfo = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "1234");
        long amount = 1000L;
        transactionID =  new Random().nextLong();
        transactionID = (transactionID>0)? transactionID : transactionID *-1;
        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo, amount)).thenReturn(-1L);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionID, transactionDB, BankOperations.AUTHORISE, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);


        //verify
        assertEquals(1, result);
        assertTrue(logs.contains("Credit card details are invalid"));
    }

    @Test
    public void testInvalidAuthorisationRequest_InsufficientFunds() throws Exception {

        //setup
        ccInfo = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "1234");
        long amount = 1000L;
        transactionID =  new Random().nextLong();
        transactionID = (transactionID>0)? transactionID : transactionID *-1;
        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo, amount)).thenReturn(-2L);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionID, transactionDB, BankOperations.AUTHORISE, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        //verify
        assertEquals(1, result);
        assertTrue(logs.contains("Insufficient funds on credit card"));
    }

    @Test
    public void testInvalidAuthorisationRequest_UnknownError() throws Exception {

        //setup
        ccInfo = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "1234");
        long amount = 1000L;
        transactionID =  new Random().nextLong();
        transactionID = (transactionID>0)? transactionID : transactionID *-1;
        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo, amount)).thenReturn(-3L);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionID, transactionDB, BankOperations.AUTHORISE, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);


         assertEquals(2, result);
        assertTrue(logs.contains("An unknown error has occurred"));
    }

    @Test
    public void testValidAuthorisationRequest() throws Exception {

        //setup
        ccInfo = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "1234");
        transactionID =  new Random().nextLong();
        transactionID = (transactionID>0)? transactionID : transactionID *-1;
        long amount = 1000L;
        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo, 1000)).thenReturn(10000L);

        PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionID, transactionDB, BankOperations.AUTHORISE, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        assertEquals(0, result);
    }

}
