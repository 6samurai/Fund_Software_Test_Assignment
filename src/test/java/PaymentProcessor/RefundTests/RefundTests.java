package PaymentProcessor.RefundTests;

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

public class RefundTests {

    CCInfo ccInfo;
    TransactionDatabase transactionDB;
    BankProxy bank;
    List<String> logs;
    long transactionID;
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
    public void testValidRefundProcess() {

        //setup
        long amount = 1000L;
        transactionID = 10L;

        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo, amount)).thenReturn(transactionID);
        when(bank.capture(transactionID)).thenReturn(0);
        when(bank.refund(transactionID,amount)).thenReturn(0);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, BankOperations.REFUND, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        //verify
        assertEquals(0, result);
        assertEquals(0,logs.size());
        assertEquals(1,transactionDB.countTransactions());
        assertEquals("refund",transactionDB.getTransaction(transactionID).getState());
    }

    @Test
    public void testInvalidRefundProcess_TransactionDoesNotExist() {

        //setup
        long amount = 1000L;
        bank = mock(BankProxy.class);
        transactionID = 10L;
        when(bank.auth(ccInfo, amount)).thenReturn(transactionID);
        when(bank.capture(transactionID)).thenReturn(0);
        when(bank.refund(transactionID,amount)).thenReturn(-1);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, BankOperations.REFUND, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        //verify
        assertEquals(1, result);
        assertEquals(1,logs.size());
        assertTrue(logs.contains("Transaction does not exist"));
        assertEquals(1,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID).getState());
    }
    @Test
    public void testInvalidRefundProcess_TransactionHasNotBeenCaptured() {

        //setup
        long amount = 1000L;
        bank = mock(BankProxy.class);
        transactionID = 10L;
        when(bank.auth(ccInfo, amount)).thenReturn(transactionID);
        when(bank.capture(transactionID)).thenReturn(0);
        when(bank.refund(transactionID,amount)).thenReturn(-2);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, BankOperations.REFUND, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        //verify
        assertEquals(1, result);
        assertEquals(1,logs.size());
        assertTrue(logs.contains("Transaction has not been captured"));
        assertEquals(1,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID).getState());

    }

    @Test
    public void testInvalidRefundProcess_TransactionHasAlreadyBeenRefunded() {

        //setup
        long amount = 1000L;
        bank = mock(BankProxy.class);
        transactionID = 10L;
        when(bank.auth(ccInfo, amount)).thenReturn(transactionID);
        when(bank.capture(transactionID)).thenReturn(0);
        when(bank.refund(transactionID,amount)).thenReturn(-3);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, BankOperations.REFUND, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        //verify
        assertEquals(1, result);
        assertEquals(1,logs.size());
        assertTrue(logs.contains("Transaction has already been refunded"));
        assertEquals(1,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID).getState());

    }

    @Test
    public void testInvalidRefundProcess_RefundAmountGreaterThanCapturedAmount() {

        //setup
        long amount = 1000L;
        bank = mock(BankProxy.class);
        transactionID = 10L;
        when(bank.auth(ccInfo, amount)).thenReturn(transactionID);
        when(bank.capture(transactionID)).thenReturn(0);
        when(bank.refund(transactionID,amount)).thenReturn(-4);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, BankOperations.REFUND, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        //verify
        assertEquals(1, result);
        assertEquals(1,logs.size());
        assertTrue(logs.contains("Refund is greater than amount captured"));
        assertEquals(1,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID).getState());

    }
    @Test
    public void testInvalidRefundProcess_UnknownError() {

        //setup
        long amount = 1000L;
        bank = mock(BankProxy.class);
        transactionID = 10L;
        when(bank.auth(ccInfo, amount)).thenReturn(transactionID);
        when(bank.capture(transactionID)).thenReturn(0);
        when(bank.refund(transactionID,amount)).thenReturn(-5);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, BankOperations.REFUND, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        //verify
        assertEquals(2, result);
        assertEquals(1,logs.size());
        assertTrue(logs.contains("An unknown error has occurred"));
        assertEquals(1,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID).getState());
    }
}
