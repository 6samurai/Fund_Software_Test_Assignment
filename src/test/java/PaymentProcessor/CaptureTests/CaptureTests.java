package PaymentProcessor.CaptureTests;

import Bank.BankProxy;
import CardInfo.CCInfo;
import PaymentProcessor.PaymentProcessor;
import PaymentProcessor.enums.TestBankOperation;
import PaymentProcessor.enums.TestCardTypes;
import TransactionDatabase.Transaction;
import TransactionDatabase.TransactionDatabase;
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
        ccInfo = null;
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
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            transactionID = 10L;

            Transaction auth_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(auth_Transaction);

            bank = mock(BankProxy.class);
            when(bank.capture(transactionID)).thenReturn(0);

            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);


            //exercise
            result = paymentProcessor.capture(auth_Transaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }
        //verify
        assertEquals(0, result);
        assertEquals(0, logs.size());
        assertEquals(2, transactionDB.countTransactions());
        assertEquals("captured", transactionDB.getTransactionByTransactionID(transactionID).getState());
    }

    @Test
    public void testInvalidCaptureProcess_TransactionDoesNotExistFromBank() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            transactionID = 10L;
            Transaction auth_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(auth_Transaction);

            bank = mock(BankProxy.class);

            when(bank.capture(transactionID)).thenReturn(-1);
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
            result = paymentProcessor.capture(auth_Transaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }
        //verify

        assertTrue(exceptionMsg.contains("Transaction does not exist"));

    }

    @Test
    public void testInvalidCaptureProcess_TransactionAlreadyCapturedFromBank() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup

            long amount = 1000L;
            transactionID = 10L;
            Transaction auth_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(auth_Transaction);

            bank = mock(BankProxy.class);
            when(bank.capture(transactionID)).thenReturn(-2);

            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
            result = paymentProcessor.capture(auth_Transaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }

        //verify

        assertTrue(exceptionMsg.contains("Transaction has already been captured"));

    }

    @Test
    public void testInvalidCaptureProcess_VoidTransaction() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            transactionID = 10L;
            Transaction auth_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(auth_Transaction);

            bank = mock(BankProxy.class);
            when(bank.capture(transactionID)).thenReturn(-3);

            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
            result = paymentProcessor.capture(auth_Transaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }
        //verify
        assertEquals(1, result);
        assertEquals(2, transactionDB.countTransactions());
        assertEquals("void", transactionDB.getTransactionByTransactionID(transactionID).getState());
    }

    @Test
    public void testInvalidCaptureProcess_BankReturnValid_TransactionVoided() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            transactionID = 10L;

            Calendar date = getPresentDate();
            date.add(Calendar.WEEK_OF_YEAR, -2);
            Transaction auth_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.AUTHORISED.toString().toLowerCase(), date);
            transactionDB.saveTransaction(auth_Transaction);

            bank = mock(BankProxy.class);
            when(bank.capture(transactionID)).thenReturn(0);

            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
            result = paymentProcessor.capture(auth_Transaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }
        //verify
        assertEquals(1, result);
        assertEquals(0, logs.size());
        assertEquals(2, transactionDB.countTransactions());
        assertEquals("void", transactionDB.getTransactionByTransactionID(transactionID).getState());
    }

    @Test
    public void testInvalidCaptureProcess_UnknownErrorFromBank() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            transactionID = 10L;
            Transaction auth_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(auth_Transaction);

            bank = mock(BankProxy.class);

            when(bank.capture(transactionID)).thenReturn(-4);
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
            result = paymentProcessor.capture(auth_Transaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }

        //verify
        assertTrue(exceptionMsg.contains("An unknown error has occurred"));
    }

    @Test
    public void testInvalidCaptureProcess_ReturnNegativeUnregisteredValueFromBank() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            transactionID = 10L;
            Transaction auth_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(auth_Transaction);

            bank = mock(BankProxy.class);

            when(bank.capture(transactionID)).thenReturn(-5);
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
            result = paymentProcessor.capture(auth_Transaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }

        //verify

        assertTrue(exceptionMsg.contains("An unknown error has occurred"));

    }

    @Test
    public void testInvalidCaptureProcess_ReturnPositiveUnregisteredValueFromBank() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            transactionID = 10L;
            Transaction auth_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(auth_Transaction);

            bank = mock(BankProxy.class);

            when(bank.capture(transactionID)).thenReturn(1);
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
            result = paymentProcessor.capture(auth_Transaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }

        //verify

        assertTrue(exceptionMsg.contains("An unknown error has occurred"));

    }

    @Test
    public void testInvalidCaptureProcess_BankReturnValid_AlreadyCapturedInDB() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            transactionID = 10L;

            Transaction auth_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(auth_Transaction);

            bank = mock(BankProxy.class);
            when(bank.capture(transactionID)).thenReturn(0);

            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
            result = paymentProcessor.capture(auth_Transaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }
        //verify

        assertTrue(exceptionMsg.contains("Transaction already captured"));

    }


}


