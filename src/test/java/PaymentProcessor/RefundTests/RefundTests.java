package PaymentProcessor.RefundTests;

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
        ccInfo = null;
        logs.clear();
        bank = null;
    }


    private Calendar getPresentDate() {
        Calendar presentWeek = Calendar.getInstance();
        presentWeek.set(Calendar.HOUR_OF_DAY, 0);
        presentWeek.set(Calendar.MINUTE, 0);
        presentWeek.set(Calendar.SECOND, 0);
        return presentWeek;
    }


    @Test
    public void testValidRefundProcess() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            int bankAction = 0;
            transactionID = 10L;
            Transaction capt_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(capt_Transaction);
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
            result = paymentProcessor.refund(amount,bankAction, capt_Transaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }
        //verify
        assertEquals(0, result);
        assertEquals(0, logs.size());
        assertEquals(2, transactionDB.countTransactions());
        assertEquals("refunded", transactionDB.getTransactionByTransactionID(transactionID).getState());
    }

    @Test
    public void testInvalidRefundProcess_TransactionDoesNotExistFromBank() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            int bankAction = -1;
            transactionID = 10L;

            Transaction capt_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(capt_Transaction);
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
            //exercise
            result = paymentProcessor.refund(amount,bankAction, capt_Transaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }

        //verify
        assertTrue(exceptionMsg.contains("Transaction does not exist"));

    }

    @Test
    public void testInvalidRefundProcess_TransactionHasNotBeenCapturedFromBank() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            int bankAction = -2;
            transactionID = 10L;

            Transaction capt_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(capt_Transaction);
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
            result = paymentProcessor.refund(amount,bankAction, capt_Transaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }
        //verify
        assertTrue(exceptionMsg.contains("Transaction has not been captured"));
    }

    @Test
    public void testInvalidRefundProcess_TransactionHasAlreadyBeenRefundedFromBank() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            int bankAction = -3;
            transactionID = 10L;

            Transaction capt_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.REFUNDED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(capt_Transaction);

            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
            result = paymentProcessor.refund(amount,bankAction, capt_Transaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }
        //verify
        assertTrue(exceptionMsg.contains("Transaction has already been refunded"));


    }

    @Test
    public void testInvalidRefundProcess_RefundAmountGreaterThanCapturedAmountFromBank() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            int bankAction = -4;
            transactionID = 10L;

            Transaction capt_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(capt_Transaction);

            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
            result = paymentProcessor.refund(amount,bankAction, capt_Transaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }
        //verify
        assertTrue(exceptionMsg.contains("Refund is greater than amount captured"));


    }

    @Test
    public void testInvalidRefundProcess_UnknownErrorFromBank() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            int bankAction = -5;
            transactionID = 10L;

            Transaction capt_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(capt_Transaction);

            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
            //exercise
            result = paymentProcessor.refund(amount,bankAction, capt_Transaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }
        //verify
        assertTrue(exceptionMsg.contains("An unknown error has occurred"));
    }

    @Test
    public void testInvalidRefundProcess_ReturnNegativeUnregisteredValueFromBank() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            int bankAction = -6;
            transactionID = 10L;

            Transaction capt_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(capt_Transaction);

            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
            result = paymentProcessor.refund(amount,bankAction, capt_Transaction);

        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }
        //verify
        assertTrue(exceptionMsg.contains("An unknown error has occurred"));

    }

    @Test
    public void testInvalidRefundProcess_ReturnPositiveUnregisteredValueFromBank() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            int bankAction = 1;
            transactionID = 10L;

            Transaction capt_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(capt_Transaction);

            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
            result = paymentProcessor.refund(amount,bankAction, capt_Transaction);

        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }
        //verify
        assertTrue(exceptionMsg.contains("An unknown error has occurred"));

    }

    @Test
    public void testInvalidRefundProcess_BankReturnsValid_TransactionNotCapturedInDB() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            int bankAction = 0;
            transactionID = 10L;

            Transaction capt_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(capt_Transaction);

            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
            result = paymentProcessor.refund(amount,bankAction, capt_Transaction);

        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }
        //verify
        assertTrue(exceptionMsg.contains("Refund is not captured"));

    }


    @Test
    public void testInvalidRefundProcess_BankReturnValid_AlreadyRefundedInDB() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            transactionID = 10L;
            int bankAction = 0;
            Transaction capt_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.REFUNDED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(capt_Transaction);

            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise

            result = paymentProcessor.refund(amount,bankAction, capt_Transaction);

        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }
        //verify

        assertTrue(exceptionMsg.contains("Transaction already refunded"));

    }

    @Test
    public void testInvalidRefundProcess_BankReturnValid_RefundValueGreaterThanDB() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amountDB = 1000L;
            long amountBank = 2000L;
            int bankAction = 0;
            transactionID = 10L;

            Transaction capt_Transaction = new Transaction(0, transactionID, ccInfo, amountDB, TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(capt_Transaction);

            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
            result = paymentProcessor.refund(amountBank,bankAction, capt_Transaction);

        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }
        //verify

        assertTrue(exceptionMsg.contains("Refund is greater than amount captured"));

    }
}
