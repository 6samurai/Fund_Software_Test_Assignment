package PaymentProcessor.AuthenticationTests;

import Bank.BankProxy;
import CardInfo.CCInfo;
import PaymentProcessor.Enums.BankOperations;
import PaymentProcessor.PaymentProcessor;
import TransactionDatabase.TransactionDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationTests {

    CCInfo ccInfo;
    TransactionDatabase transactionDB;
    BankProxy bank;
    List<String> logs;

    @Before
    public void setup() {
        transactionDB = new TransactionDatabase();
        logs = new ArrayList<String>();
        ccInfo = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "1234");
    }

    @After
    public void teardown() {
        transactionDB = null;
        ccInfo =null;
        logs.clear();
        bank = null;
    }

    @Test
    public void testValidAuthorisationRequest() {

        //setup
        long amount = 1000L;
        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo, 1000)).thenReturn(371449635398431L);

        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, BankOperations.AUTHORISE, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        //verify
        assertEquals(0, result);
    }


    @Test
    public void testInvalidAuthorisationRequest_InvalidCreditCardDetails() {
        //setup

        long amount = 1000L;
        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo, amount)).thenReturn(-1L);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, BankOperations.AUTHORISE, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        //verify
        assertEquals(1, result);
        assertTrue(logs.contains("Credit card details are invalid"));
    }

    @Test
    public void testInvalidAuthorisationRequest_InsufficientFunds(){

        //setup
        long amount = 1000L;
        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo, amount)).thenReturn(-2L);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, BankOperations.AUTHORISE, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        //verify
        assertEquals(1, result);
        assertTrue(logs.contains("Insufficient funds on credit card"));
    }

    @Test
    public void testInvalidAuthorisationRequest_UnknownError() {

        //setup
        long amount = 1000L;
        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo, amount)).thenReturn(-3L);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, BankOperations.AUTHORISE, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        //verify
        assertEquals(2, result);
        assertTrue(logs.contains("An unknown error has occurred"));
    }


}
