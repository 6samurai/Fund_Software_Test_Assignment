package PaymentProcessor.CaptureTests;

import Bank.BankProxy;
import CardInfo.CCInfo;

import PaymentProcessor.PaymentProcessor;
import PaymentProcessor.enums.TestBankOperation;
import PaymentProcessor.enums.TestCardTypes;
import TransactionDatabase.TransactionDatabase;
import  TransactionDatabase.Transaction;
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
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");

    }

    @After
    public void teardown() {
        transactionDB = null;
        ccInfo =null;
        logs.clear();
        bank = null;
        transactionID = 0L;
    }

    private Calendar getPresentDate() {
        Calendar presentWeek = Calendar.getInstance();
        presentWeek.set(Calendar.HOUR_OF_DAY, 0);
        presentWeek.set(Calendar.MINUTE, 0);
        presentWeek.set(Calendar.SECOND, 0);
        return presentWeek;
    }

    @Test
    public void testValidCaptureProcess() {

        //setup
        long amount = 1000L;
        transactionID = 10L;

        Transaction auth_Transaction = new Transaction(0,transactionID,ccInfo,amount,TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction);

        bank = mock(BankProxy.class);
        when(bank.capture(transactionID)).thenReturn(0);

        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);


        int result = paymentProcessor.processPayment(ccInfo, amount,  TestBankOperation.CAPTURED.toString(),transactionID);

        //verify
        assertEquals(0, result);
        assertEquals(0,logs.size());
        assertEquals(2,transactionDB.countTransactions());
        assertEquals("captured",transactionDB.getTransaction(transactionID).getState());
    }

    @Test
    public void testInvalidCaptureProcess_TransactionDoesNotExistFromBank() {

        //setup
        long amount = 1000L;
        transactionID = 10L;
        Transaction auth_Transaction = new Transaction(0,transactionID,ccInfo,amount,TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction);

        bank = mock(BankProxy.class);

        when(bank.capture(transactionID)).thenReturn(-1);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB,  logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.CAPTURED.toString(),transactionID);

        //verify
        assertEquals(1, result);
        assertEquals(1,logs.size());
        assertTrue(logs.contains("Transaction does not exist"));
        assertEquals(2,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID).getState());
    }

    @Test
    public void testInvalidCaptureProcess_TransactionAlreadyCapturedFromBank() {

        //setup

        long amount = 1000L;
        transactionID = 10L;
        Transaction auth_Transaction = new Transaction(0,transactionID,ccInfo,amount,TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction);

        bank = mock(BankProxy.class);
        when(bank.capture(transactionID)).thenReturn(-2);

        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.CAPTURED.toString(),transactionID);

        //verify
        assertEquals(1, result);
        assertEquals(1,logs.size());
        assertTrue(logs.contains("Transaction has already been captured"));
        assertEquals(2,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID).getState());
    }


    @Test
    public void testInvalidCaptureProcess_VoidTransaction() {

        //setup
        long amount = 1000L;
        transactionID = 10L;
        Transaction auth_Transaction = new Transaction(0,transactionID,ccInfo,amount,TestBankOperation.AUTHORISED.toString().toLowerCase(),getPresentDate());
        transactionDB.saveTransaction(auth_Transaction);

        bank = mock(BankProxy.class);
        when(bank.capture(transactionID)).thenReturn(-3);

        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.CAPTURED.toString(),transactionID);

        //verify
        assertEquals(1, result);
        assertEquals(2,transactionDB.countTransactions());
        assertEquals("void",transactionDB.getTransaction(transactionID).getState());
    }


    @Test
    public void testInvalidCaptureProcess_UnknownErrorFromBank() {

        //setup
        long amount = 1000L;
        transactionID = 10L;
        Transaction auth_Transaction = new Transaction(0,transactionID,ccInfo,amount,TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction);

        bank = mock(BankProxy.class);

        when(bank.capture(transactionID)).thenReturn(-4);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.CAPTURED.toString(),transactionID);

        //verify
        assertEquals(2, result);
        assertEquals(1,logs.size());
        assertTrue(logs.contains("An unknown error has occurred"));
        assertEquals(2,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID).getState());
    }

    @Test
    public void testInvalidCaptureProcess_ReturnAnUnregisteredValueFromBank() {

        //setup
        long amount = 1000L;
        transactionID = 10L;
        Transaction auth_Transaction = new Transaction(0,transactionID,ccInfo,amount,TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction);

        bank = mock(BankProxy.class);

        when(bank.capture(transactionID)).thenReturn(-40000);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount, TestBankOperation.CAPTURED.toString(),transactionID);

        //verify
        assertEquals(2, result);
        assertEquals(1,logs.size());
        assertTrue(logs.contains("An unknown error has occurred"));
        assertEquals(2,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID).getState());
    }

    @Test
    public void testInvalidCaptureProcess_BankReturnsValid_ExpiredCardFromDB() {

        //setup
        long amount = 1000L;
        transactionID = 10L;
        ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2000", "1234");

        Transaction auth_Transaction = new Transaction(0,transactionID,ccInfo,amount,TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction);

        bank = mock(BankProxy.class);
        when(bank.capture(transactionID)).thenReturn(0);

        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);


        int result = paymentProcessor.processPayment(ccInfo, amount,  TestBankOperation.CAPTURED.toString(),transactionID);

        //verify
        assertEquals(1, result);
        assertEquals(1,logs.size());
        assertTrue(logs.get(0).contains("Expired card"));
        assertEquals(2,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID).getState());
    }

    @Test
    public void testInvalidCaptureProcess_BankReturnValid_NoRecordInDatabase() {

        //setup
        long amount = 1000L;
        transactionID = 10L;

        bank = mock(BankProxy.class);
        when(bank.capture(transactionID)).thenReturn(0);

        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);


        int result = paymentProcessor.processPayment(ccInfo, amount,  TestBankOperation.CAPTURED.toString(),transactionID);

        //verify
        assertEquals(2, result);
        assertEquals(1,logs.size());
        assertTrue(logs.get(0).contains("An error has occurred"));
        assertEquals(1,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID).getState());
    }

    @Test
    public void testInvalidCaptureProcess_BankReturnValid_AlreadyCapturedInDB() {

        //setup
        long amount = 1000L;
        transactionID = 10L;

        Transaction auth_Transaction = new Transaction(0,transactionID,ccInfo,amount,TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction);

        bank = mock(BankProxy.class);
        when(bank.capture(transactionID)).thenReturn(0);

        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);


        int result = paymentProcessor.processPayment(ccInfo, amount,  TestBankOperation.CAPTURED.toString(),transactionID);

        //verify
        assertEquals(1, result);
        assertEquals(1,logs.size());
        assertTrue(logs.get(0).contains("Transaction already processed"));
        assertEquals(2,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID).getState());
    }

    @Test
    public void testInvalidCaptureProcess_BankReturnValid_TransactionVoided() {

        //setup
        long amount = 1000L;
        transactionID = 10L;

        Calendar date = getPresentDate();
        date.add(Calendar.WEEK_OF_YEAR, -2);
        Transaction auth_Transaction = new Transaction(0,transactionID,ccInfo,amount,TestBankOperation.AUTHORISED.toString().toLowerCase(), date);
        transactionDB.saveTransaction(auth_Transaction);

        bank = mock(BankProxy.class);
        when(bank.capture(transactionID)).thenReturn(0);

        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

        int result = paymentProcessor.processPayment(ccInfo, amount,  TestBankOperation.CAPTURED.toString(),transactionID);

        //verify
        assertEquals(1, result);
        assertEquals(0,logs.size());
        assertEquals(2,transactionDB.countTransactions());
        assertEquals("void",transactionDB.getTransaction(transactionID).getState());
    }

}


