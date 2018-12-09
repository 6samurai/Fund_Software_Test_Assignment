package PaymentProcessor.AuthorisationTests;

import Bank.BankProxy;
import CardInfo.CCInfo;

import PaymentProcessor.PaymentProcessor;
import PaymentProcessor.enums.TestCardTypes;
import PaymentProcessor.enums.TestBankOperation;
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

public class AuthorisationTests {

    CCInfo ccInfo;
    TransactionDatabase transactionDB;
    BankProxy bank;
    List<String> logs;
    Long transactionID;

    @Before
    public void setup() {
        transactionDB = new TransactionDatabase();
        logs = new ArrayList<String>();
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
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
        transactionID =  10L;
        when(bank.auth(ccInfo, 1000)).thenReturn(transactionID);

        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.AUTHORISE.toString());

        //verify
        assertEquals(0, result);
        assertEquals(1,transactionDB.countTransactions());
        assertEquals("authorise",transactionDB.getTransaction(transactionID).getState());
    }


    @Test
    public void testInvalidAuthorisationRequest_InvalidCreditCardDetails() {
        //setup

        long amount = 1000L;
        bank = mock(BankProxy.class);
        transactionID = 10L;
        when(bank.auth(ccInfo, amount)).thenReturn(-1L);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.AUTHORISE.toString());

        //verify
        assertEquals(1, result);
        assertEquals(1,logs.size());
        assertTrue(logs.get(0).contains("Credit card details are invalid"));
        assertEquals(0,transactionDB.countTransactions());
    }

    @Test
    public void testInvalidAuthorisationRequest_InsufficientFunds(){

        //setup
        long amount = 1000L;
        bank = mock(BankProxy.class);
        transactionID = 10L;
        when(bank.auth(ccInfo, amount)).thenReturn(-2L);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.AUTHORISE.toString());

        //verify
        assertEquals(1, result);
        assertEquals(1,logs.size());
        assertTrue(logs.get(0).contains("Insufficient funds on credit card"));
        assertEquals(0,transactionDB.countTransactions());
    }

    @Test
    public void testInvalidAuthorisationRequest_UnknownError() {

        //setup
        long amount = 1000L;
        transactionID =  10L;
        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo, amount)).thenReturn(-3L);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.AUTHORISE.toString());

        //verify
        assertEquals(2, result);
        assertEquals(1,logs.size());
        assertTrue(logs.get(0).contains("An unknown error has occurred"));
        assertEquals(0,transactionDB.countTransactions());
    }

    @Test
    public void testInvalidAuthorisationRequest_ExpiredCard() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2000", "1234");

        //setup
        long amount = 1000L;
        transactionID =  10L;
        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo, amount)).thenReturn(0L);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.AUTHORISE.toString());

        //verify
        assertEquals(1, result);
        assertEquals(1,logs.size());
        assertTrue(logs.get(0).contains("Expired card"));
        assertEquals(0,transactionDB.countTransactions());
    }
}
