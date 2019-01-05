package PaymentProcessor.AuthorisationTests;

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

public class AuthorisationTests {

    CCInfo ccInfo;
    TransactionDatabase transactionDB;
    BankProxy bank;
    List<String> logs;
    Long transactionID;
    Transaction currentTransaction;

    @Before
    public void setup() {
        transactionDB = new TransactionDatabase();
        logs = new ArrayList<String>();
        ccInfo = new CCInfo("Test user American", "222, test address, TST101", TestCardTypes.AMERICAN_EXPRESS.toString(), "378282246310005", "05/2020", "1111");
    }

    @After
    public void teardown() {
        transactionDB = null;
        ccInfo = null;
        logs.clear();
        bank = null;
        currentTransaction = null;
    }


    private Calendar getPresentDate() {
        Calendar presentWeek = Calendar.getInstance();
        presentWeek.set(Calendar.HOUR_OF_DAY, 0);
        presentWeek.set(Calendar.MINUTE, 0);
        presentWeek.set(Calendar.SECOND, 0);
        return presentWeek;
    }

    @Test
    public void testValidAuthorisationRequest() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            transactionID = 10L;
            currentTransaction = new Transaction(transactionDB.countTransactions(), -1L, ccInfo, amount, "", getPresentDate());
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
                    result = paymentProcessor.authorise(transactionID,currentTransaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }

        //verify
        assertEquals(0, result);
        assertEquals(1, transactionDB.countTransactions());
        assertEquals("authorised", transactionDB.getTransactionByTransactionID(transactionID).getState());
    }

    @Test
    public void testInvalidAuthorisationRequest_InvalidCreditCardDetails() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;

            transactionID = -1L;
            currentTransaction = new Transaction(transactionDB.countTransactions(), -1L, ccInfo, amount, "", getPresentDate());
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
                    result = paymentProcessor.authorise(transactionID,currentTransaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }

        //verify
        assertTrue(exceptionMsg.contains("Credit card details are invalid"));

    }

    @Test
    public void testInvalidAuthorisationRequest_InsufficientFunds() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            transactionID = -2L;
            currentTransaction = new Transaction(transactionDB.countTransactions(), -1L, ccInfo, amount, "", getPresentDate());
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
            result = paymentProcessor.authorise(transactionID,currentTransaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }

        //verify
        assertTrue(exceptionMsg.contains("Insufficient funds on credit card"));

    }

    @Test
    public void testInvalidAuthorisationRequest_UnknownError() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            transactionID = -3L;
            currentTransaction = new Transaction(transactionDB.countTransactions(), -1L, ccInfo, amount, "", getPresentDate());
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise

            result = paymentProcessor.authorise(transactionID,currentTransaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }

        //verify
        assertTrue(exceptionMsg.contains("An unknown error has occurred"));
    }

    @Test
    public void testInvalidAuthorisationRequest_ReturnAnUnregisteredValueFromBank() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            transactionID = -4L;

            currentTransaction = new Transaction(transactionDB.countTransactions(), -1L, ccInfo, amount, "", getPresentDate());
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise

            result = paymentProcessor.authorise(transactionID,currentTransaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }
        //verify
        assertTrue(exceptionMsg.contains("An unknown error has occurred"));
    }

    @Test
    public void testInvalidAuthorisationRequest_BankReturnsZero() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            transactionID = 0L;

            currentTransaction = new Transaction(transactionDB.countTransactions(), -1L, ccInfo, amount, "", getPresentDate());
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise

            result = paymentProcessor.authorise(transactionID,currentTransaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }
        //verify
        assertTrue(exceptionMsg.contains("An unknown error has occurred"));
    }

    @Test
    public void testInvalidAuthorisationRequest_AlreadyAuthorised() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = 1000L;
            transactionID = 10L;
            

            currentTransaction = new Transaction(transactionDB.countTransactions(), -1L, ccInfo, amount, "", getPresentDate());
            Transaction auth_Transaction = new Transaction(0, transactionID, ccInfo, amount, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
            transactionDB.saveTransaction(auth_Transaction);
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise

                    result = paymentProcessor.authorise(transactionID,currentTransaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }

        //verify
        assertTrue(exceptionMsg.contains("Transaction has already been authorised"));
    }

    @Test
    public void testInvalidAuthorisationRequest_NegativeAmount() {
        String exceptionMsg = "";
        int result = -1;
        try {
            //setup
            long amount = -1000L;
            transactionID = 10L;
            currentTransaction = new Transaction(transactionDB.countTransactions(), -1L, ccInfo, amount, "", getPresentDate());
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);

            //exercise
                    result = paymentProcessor.authorise(transactionID,currentTransaction);
        } catch (Exception e) {
            exceptionMsg = e.getMessage();
        }

        //verify
        assertTrue(exceptionMsg.contains("Invalid amount"));
    }

}
