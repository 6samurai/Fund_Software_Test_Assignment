package PaymentProcessor.CaptureTests;

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
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CaptureTests {

    CCInfo ccInfo;
    TransactionDatabase transactionDB;
    BankProxy bank;
    List<String> logs;
    Long transactionID;

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
        transactionID = 0L;
    }

    @Test
    public void testValidCaptureProcess() {

        //setup
        long amount = 1000L;
        transactionID =  new Random().nextLong();
        transactionID = (transactionID>0)? transactionID : transactionID *-1;
        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo, 1000)).thenReturn(transactionID);
        when(bank.capture(transactionID)).thenReturn(0);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionID, transactionDB, BankOperations.CAPTURE, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        //verify
        assertEquals(0, result);
        assertEquals(0,logs.size());
        assertEquals(1,transactionDB.countTransactions());
        assertEquals("capture",transactionDB.getTransaction(transactionID).getState());
    }

    @Test
    public void testInvalidCaptureProcess_TransactionDoesNotExist() {

        //setup
        long amount = 1000L;
        transactionID =  new Random().nextLong();
        transactionID = (transactionID>0)? transactionID : transactionID *-1;

        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo, 1000)).thenReturn(transactionID);
        when(bank.capture(transactionID)).thenReturn(-1);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionID, transactionDB, BankOperations.CAPTURE, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        //verify
        assertEquals(1, result);
        assertEquals(1,logs.size());
        assertTrue(logs.contains("Transaction does not exist"));
        assertEquals(0,transactionDB.countTransactions());
    }

    @Test
    public void testInvalidCaptureProcess_TransactionAlreadyCaptured() {

        //setup

        long amount = 1000L;
        transactionID =  new Random().nextLong();
        transactionID = (transactionID>0)? transactionID : transactionID *-1;

        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo, 1000)).thenReturn(transactionID);
        when(bank.capture(transactionID)).thenReturn(-2);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionID, transactionDB, BankOperations.CAPTURE, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        //verify
        assertEquals(1, result);
        assertEquals(1,logs.size());
        assertTrue(logs.contains("Transaction has already been captured"));
        assertEquals(0,transactionDB.countTransactions());
    }


    @Test
    public void testInvalidCaptureProcess_VoidTransaction() {

        //setup
        long amount = 1000L;
        transactionID =  new Random().nextLong();
        transactionID = (transactionID>0)? transactionID : transactionID *-1;
        bank = mock(BankProxy.class);

        when(bank.auth(ccInfo, 1000)).thenReturn(transactionID);
        when(bank.capture(transactionID)).thenReturn(-3);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionID, transactionDB, BankOperations.CAPTURE, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        //verify
        assertEquals(1, result);
        assertEquals(1,logs.size());
        assertTrue(logs.contains("Transaction has been voided"));
        assertEquals(1,transactionDB.countTransactions());
        assertEquals("void",transactionDB.getTransaction(transactionID).getState());
    }


    @Test
    public void testInvalidCaptureProcess_UnknownError() {

        //setup
        long amount = 1000L;
        transactionID =  new Random().nextLong();
        transactionID = (transactionID>0)? transactionID : transactionID *-1;
        bank = mock(BankProxy.class);

        when(bank.auth(ccInfo, 1000)).thenReturn(transactionID);
        when(bank.capture(transactionID)).thenReturn(-4);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionID, transactionDB, BankOperations.CAPTURE, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        //verify
        assertEquals(2, result);
        assertEquals(1,logs.size());
        assertTrue(logs.contains("An unknown error has occurred"));
        assertEquals(0,transactionDB.countTransactions());
    }
}


