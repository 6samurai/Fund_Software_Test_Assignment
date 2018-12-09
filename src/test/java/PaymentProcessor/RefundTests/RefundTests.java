package PaymentProcessor.RefundTests;

import Bank.BankProxy;
import CardInfo.CCInfo;
import PaymentProcessor.PaymentProcessor;
import PaymentProcessor.enums.TestBankOperation;
import PaymentProcessor.enums.TestCardTypes;
import TransactionDatabase.TransactionDatabase;
import TransactionDatabase.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
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
    public void testValidRefundProcess() {

        //setup
        long amount = 1000L;
        transactionID = 10L;
        Transaction auth_Transaction = new Transaction(transactionID,ccInfo,amount,TestBankOperation.CAPTURE.toString().toLowerCase(), Calendar.getInstance());
        transactionDB.saveTransaction(auth_Transaction);

        bank = mock(BankProxy.class);
        when(bank.refund(transactionID,amount)).thenReturn(0);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.REFUND.toString(),transactionID);

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

        Transaction auth_Transaction = new Transaction(transactionID,ccInfo,amount,TestBankOperation.CAPTURE.toString().toLowerCase(), Calendar.getInstance());
        transactionDB.saveTransaction(auth_Transaction);

        when(bank.refund(transactionID,amount)).thenReturn(-1);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.REFUND.toString(),transactionID);

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

        Transaction auth_Transaction = new Transaction(transactionID,ccInfo,amount,TestBankOperation.CAPTURE.toString().toLowerCase(), Calendar.getInstance());
        transactionDB.saveTransaction(auth_Transaction);

        when(bank.refund(transactionID,amount)).thenReturn(-2);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.REFUND.toString(),transactionID);

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

        Transaction auth_Transaction = new Transaction(transactionID,ccInfo,amount,TestBankOperation.CAPTURE.toString().toLowerCase(), Calendar.getInstance());
        transactionDB.saveTransaction(auth_Transaction);

        when(bank.refund(transactionID,amount)).thenReturn(-3);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB,logs);

        //exercise
         int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.REFUND.toString(),transactionID);

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

        Transaction auth_Transaction = new Transaction(transactionID,ccInfo,amount,TestBankOperation.CAPTURE.toString().toLowerCase(), Calendar.getInstance());
        transactionDB.saveTransaction(auth_Transaction);

        when(bank.refund(transactionID,amount)).thenReturn(-4);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB,  logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.REFUND.toString(),transactionID);

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

        Transaction auth_Transaction = new Transaction(transactionID,ccInfo,amount,TestBankOperation.CAPTURE.toString().toLowerCase(), Calendar.getInstance());
        transactionDB.saveTransaction(auth_Transaction);

       when(bank.refund(transactionID,amount)).thenReturn(-5);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.REFUND.toString(),transactionID);

        //verify
        assertEquals(2, result);
        assertEquals(1,logs.size());
        assertTrue(logs.contains("An unknown error has occurred"));
        assertEquals(1,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID).getState());
    }


    @Test
    public void testInvalidRefundProcess_BankReturnsValid_CreditCardExpired() {
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2000", "1234");

        //setup
        long amount = 1000L;
        bank = mock(BankProxy.class);
        transactionID = 10L;

        Transaction auth_Transaction = new Transaction(transactionID,ccInfo,amount,TestBankOperation.CAPTURE.toString().toLowerCase(), Calendar.getInstance());
        transactionDB.saveTransaction(auth_Transaction);

        when(bank.refund(transactionID,amount)).thenReturn(0);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.REFUND.toString(),transactionID);

        //verify
        assertEquals(1, result);
        assertEquals(1,logs.size());
        assertTrue(logs.contains("Expired card"));
        assertEquals(1,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID).getState());
    }

    @Test
    public void testInvalidRefundProcess_BankReturnsValid_NoRecordInDB() {
        //setup
        long amount = 1000L;
        bank = mock(BankProxy.class);

        when(bank.refund(transactionID,amount)).thenReturn(0);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.REFUND.toString(),transactionID);

        //verify
        assertEquals(2, result);
        assertEquals(1,logs.size());
        assertTrue(logs.get(0).contains("An error has occurred"));
        assertEquals(1,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID).getState());
    }

    @Test
    public void testInvalidRefundProcess_BankReturnsValid_TransactionNotCapturedInDB() {

        //setup
        long amount = 1000L;
        bank = mock(BankProxy.class);
        transactionID = 10L;

        Transaction auth_Transaction = new Transaction(transactionID,ccInfo,amount,TestBankOperation.AUTHORISE.toString().toLowerCase(), Calendar.getInstance());
        transactionDB.saveTransaction(auth_Transaction);

        when(bank.refund(transactionID,amount)).thenReturn(0);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.REFUND.toString(),transactionID);

        //verify
        assertEquals(1, result);
        assertEquals(1,logs.size());
        assertTrue(logs.contains("Refund is not captured"));
        assertEquals(1,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID).getState());
    }


    @Test
    public void testInvalidRefundProcess_BankReturnValid_AlreadyRefundedInDB() {

        //setup
        long amount = 1000L;
        transactionID = 10L;

        Transaction auth_Transaction = new Transaction(transactionID,ccInfo,amount,TestBankOperation.REFUND.toString().toLowerCase(), Calendar.getInstance());
        transactionDB.saveTransaction(auth_Transaction);

        bank = mock(BankProxy.class);

        when(bank.refund(transactionID,amount)).thenReturn(0);

        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);


        int result = paymentProcessor.processPayment(ccInfo, amount,  TestBankOperation.REFUND.toString(),transactionID);

        //verify
        assertEquals(1, result);
        assertEquals(1,logs.size());
        assertTrue(logs.get(0).contains("Transaction already refunded"));
        assertEquals(1,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID).getState());
    }

    @Test
    public void testInvalidRefundProcess_BankReturnValid_RefundValueGreaterThanDB() {

        //setup
        long amountDB = 1000L;
        long amountBank = 2000L;
        transactionID = 10L;

        Transaction auth_Transaction = new Transaction(transactionID,ccInfo,amountDB,TestBankOperation.CAPTURE.toString().toLowerCase(), Calendar.getInstance());
        transactionDB.saveTransaction(auth_Transaction);

        bank = mock(BankProxy.class);

        when(bank.refund(transactionID,amountBank)).thenReturn(0);

        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);


        int result = paymentProcessor.processPayment(ccInfo, amountBank,  TestBankOperation.REFUND.toString(),transactionID);

        //verify
        assertEquals(1, result);
        assertEquals(1,logs.size());
        assertTrue(logs.get(0).contains("Refund is greater than amount captured"));
        assertEquals(1,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID).getState());
    }
}
