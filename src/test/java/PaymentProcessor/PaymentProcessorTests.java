package PaymentProcessor;

import Bank.BankProxy;
import CardInfo.CCInfo;
import CardInfo.enums.CardTypes;
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

public class PaymentProcessorTests {

    TransactionDatabase transactionDB;
    BankProxy bank;
    List<String> logs;

    @Before
    public void setup() {
        transactionDB = new TransactionDatabase();
        logs = new ArrayList<String>();
    }

    @After
    public void teardown() {
        transactionDB = null;
        logs.clear();
    }

    private Calendar getPresentDate() {
        Calendar presentWeek = Calendar.getInstance();
        presentWeek.set(Calendar.HOUR_OF_DAY, 0);
        presentWeek.set(Calendar.MINUTE, 0);
        presentWeek.set(Calendar.SECOND, 0);
        return presentWeek;
    }
    @Test
    public void testMultiple_ValidCaptureTransactions_DifferentAccounts() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        CCInfo ccInfo_2 = new CCInfo("Joe", "333,Test", CardTypes.VISA.toString(), "4111111111111111", "11/2021", "421");
        CCInfo ccInfo_3 = new CCInfo("Mark", "444,Test", CardTypes.MASTERCARD.toString(), "5105105105105100", "11/2022", "213");
        long amount_1 = 1000L;
        long amount_2 = 3000L;
        long amount_3 = 200L;
        long transactionID_1 = 1L;
        long transactionID_2 = 2L;
        long transactionID_3 = 3L;
        Transaction auth_Transaction_1 = new Transaction(0, transactionID_1, ccInfo_1, amount_1, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        Transaction auth_Transaction_2 = new Transaction(1, transactionID_2, ccInfo_2, amount_2, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        Transaction auth_Transaction_3 = new Transaction(2, transactionID_3, ccInfo_3, amount_3, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction_1);
        transactionDB.saveTransaction(auth_Transaction_2);
        transactionDB.saveTransaction(auth_Transaction_3);
        bank = mock(BankProxy.class);

        when(bank.capture(transactionID_1)).thenReturn(0);
        when(bank.capture(transactionID_2)).thenReturn(0);
        when(bank.capture(transactionID_3)).thenReturn(0);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment( transactionID_1);
        int result_operation_2 = paymentProcessor.processPayment( transactionID_2);
        int result_operation_3 = paymentProcessor.processPayment( transactionID_3);
        //verify
        assertEquals(0, result_operation_1);
        assertEquals(0, result_operation_2);
        assertEquals(0, result_operation_3);
        assertEquals(0, logs.size());
        assertEquals(6, transactionDB.countTransactions());
        assertEquals("captured", transactionDB.getTransactionByTransactionID(transactionID_1).getState());
        assertEquals("captured", transactionDB.getTransactionByTransactionID(transactionID_2).getState());
        assertEquals("captured", transactionDB.getTransactionByTransactionID(transactionID_3).getState());

    }

    //two transactions have errors in the CCinfo(ccInfo_1 missing card number - ccInfo_2 missing card number) and one transaction results an invalid transaction from the bank
    @Test
    public void testMultiple_ErrorTransactions_DifferentAccounts() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "", "11/2020", "1234");
        CCInfo ccInfo_2 = new CCInfo("Joe", "333,Test", TestCardTypes.MASTERCARD.toString(), "4111111111111111", "11/2021", "431");
        CCInfo ccInfo_3 = new CCInfo("Mark", "444,Test", TestCardTypes.MASTERCARD.toString(), "5105105105105100", "11/2022", "213");
        long amount_1 = 1000L;
        long amount_2 = 3000L;
        long amount_3 = 200L;
        long transactionID_3 = 3L;

        Transaction auth_Transaction_3 = new Transaction(0, transactionID_3, ccInfo_3, amount_3, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());

        transactionDB.saveTransaction(auth_Transaction_3);

        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo_1, amount_1)).thenReturn(0L);
        when(bank.auth(ccInfo_2, amount_2)).thenReturn(0L);
        when(bank.capture(transactionID_3)).thenReturn(-1);


        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(ccInfo_1, amount_1);

        paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_2 = paymentProcessor.processPayment(ccInfo_2, amount_2);

        paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_3 = paymentProcessor.processPayment(transactionID_3);
        //verify
        assertEquals(1, result_operation_1);
        assertEquals(1, result_operation_2);
        assertEquals(1, result_operation_3);
        assertEquals(3, logs.size());
        assertEquals(4, transactionDB.countTransactions());
        assertTrue(logs.get(0).contains("Invalid Card Number"));
        assertTrue(logs.get(1).contains("Invalid Prefix of card"));
        assertTrue(logs.get(2).contains("Transaction does not exist"));
        assertEquals("invalid", transactionDB.getTransactionByTransactionID(transactionID_3).getState());

    }

    @Test
    public void testMultiple_ValidAndErrorTransactions() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        CCInfo ccInfo_2 = new CCInfo("Joe", "333,Test", TestCardTypes.MASTERCARD.toString(), "5555555555554444", "11/2000", "432");
        CCInfo ccInfo_3 = new CCInfo("Mark", "444,Test", TestCardTypes.MASTERCARD.toString(), "5105105105105100", "11/2022", "243");
        long amount_1 = 1000L;
        long amount_2 = 3000L;
        long amount_3 = 200L;
        long transactionID_1 = 1L;
        long transactionID_2 = 2L;
        long transactionID_3 = 3L;

        Transaction auth_Transaction_1 = new Transaction(0, transactionID_1, ccInfo_1, amount_1, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        Transaction auth_Transaction_2 = new Transaction(1, transactionID_2, ccInfo_2, amount_2, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        Transaction auth_Transaction_3 = new Transaction(2, transactionID_3, ccInfo_3, amount_3, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());

        transactionDB.saveTransaction(auth_Transaction_1);
        transactionDB.saveTransaction(auth_Transaction_2);
        transactionDB.saveTransaction(auth_Transaction_3);

        bank = mock(BankProxy.class);

        when(bank.capture(transactionID_1)).thenReturn(0);
        when(bank.capture(transactionID_2)).thenReturn(0);
        when(bank.capture(transactionID_3)).thenReturn(-1);

        //exercise

        //valid operation
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment( transactionID_1);
        //expired card
        int result_operation_2 = paymentProcessor.processPayment( transactionID_2);

        int result_operation_3 = paymentProcessor.processPayment(transactionID_3);

        //verify
        assertEquals(0, result_operation_1);
        assertEquals("captured", transactionDB.getTransactionByTransactionID(transactionID_1).getState());
        assertEquals(1, result_operation_2);
        assertEquals(1, result_operation_3);
        assertEquals(2, logs.size());
        assertTrue(logs.get(0).contains("Expired card"));
        assertTrue(logs.get(1).contains("Transaction does not exist"));
        assertEquals(6, transactionDB.countTransactions());
        assertEquals("invalid", transactionDB.getTransactionByTransactionID(transactionID_3).getState());
    }

    @Test
    public void testMultipleValidOperations_SameAccount_DifferentTransactions() {
        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long amount_2 = 2000L;
        long amount_3 = 3000L;
        long amount_4 = 400L;

        long transactionID_1 = 1L;
        long transactionID_2 = 2L;
        long transactionID_3 = 3L;
        long transactionID_4 = 4L;

        Transaction auth_Transaction_1 = new Transaction(0, transactionID_1, ccInfo_1, amount_1, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        Transaction auth_Transaction_2 = new Transaction(1, transactionID_2, ccInfo_1, amount_2, TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());
        Transaction auth_Transaction_3 = new Transaction(2, transactionID_3, ccInfo_1, amount_3, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        Transaction auth_Transaction_4 = new Transaction(3, transactionID_4, ccInfo_1, amount_4, TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction_1);
        transactionDB.saveTransaction(auth_Transaction_2);
        transactionDB.saveTransaction(auth_Transaction_3);
        transactionDB.saveTransaction(auth_Transaction_4);

        bank = mock(BankProxy.class);

        when(bank.capture(transactionID_1)).thenReturn(0);
        when(bank.refund(transactionID_2, amount_2)).thenReturn(0);
        when(bank.capture(transactionID_3)).thenReturn(0);
        when(bank.refund(transactionID_4, amount_4)).thenReturn(0);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(transactionID_1);
        int result_operation_2 = paymentProcessor.processPayment( amount_2,  transactionID_2);
        int result_operation_3 = paymentProcessor.processPayment(transactionID_3);
        int result_operation_4 = paymentProcessor.processPayment( amount_4, transactionID_4);

        //verify
        assertEquals(0, result_operation_1);
        assertEquals("captured", transactionDB.getTransactionByTransactionID(transactionID_1).getState());
        assertEquals(0, result_operation_2);
        assertEquals("refunded", transactionDB.getTransactionByTransactionID(transactionID_2).getState());
        assertEquals(0, result_operation_3);
        assertEquals("captured", transactionDB.getTransactionByTransactionID(transactionID_3).getState());
        assertEquals(0, result_operation_4);
        assertEquals("refunded", transactionDB.getTransactionByTransactionID(transactionID_4).getState());
        assertEquals(0, logs.size());
        assertEquals(8, transactionDB.countTransactions());

    }

   @Test
    public void testMultipleValidOperations_SameAccount_SameTransaction_MultipleOperations() {
        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        long amount = 1000L;

        long transactionID = 1L;

        bank = mock(BankProxy.class);

        when(bank.capture(transactionID)).thenReturn(0);
        when(bank.refund(transactionID, amount)).thenReturn(0);

        Transaction auth_Transaction_1 = new Transaction(0, transactionID, ccInfo_1, amount, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction_1);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(transactionID);
        int result_operation_2 = paymentProcessor.processPayment( amount, transactionID);

        //verify
        assertEquals(0, result_operation_1);
        assertEquals("refunded", transactionDB.getTransactionByTransactionID(transactionID).getState());
        assertEquals(0, result_operation_2);
        assertEquals(0, logs.size());
        assertEquals(3, transactionDB.countTransactions());
    }

    @Test
    public void testMultipleValidAndInvalidOperations_SameAccount() {
        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long amount_2 = 2000L;
        long amount_3 = 3000L;
        long amount_4 = 400L;

        long transactionID_1 = 1L;
        long transactionID_2 = 2L;
        long transactionID_3 = 3L;
        long transactionID_4 = 4L;

        Transaction auth_Transaction_1 = new Transaction(0, transactionID_1, ccInfo_1, amount_1, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        Transaction auth_Transaction_2 = new Transaction(1, transactionID_2, ccInfo_1, amount_2, TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());
        Transaction auth_Transaction_3 = new Transaction(2, transactionID_3, ccInfo_1, amount_3, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        Transaction auth_Transaction_4 = new Transaction(3, transactionID_4, ccInfo_1, amount_4, TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction_1);
        transactionDB.saveTransaction(auth_Transaction_2);
        transactionDB.saveTransaction(auth_Transaction_3);
        transactionDB.saveTransaction(auth_Transaction_4);

        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo_1, amount_1)).thenReturn(transactionID_1);
        when(bank.capture(transactionID_1)).thenReturn(0);

        when(bank.auth(ccInfo_1, amount_2)).thenReturn(transactionID_2);
        when(bank.capture(transactionID_2)).thenReturn(0);
        when(bank.refund(transactionID_2, amount_2)).thenReturn(-1);

        when(bank.auth(ccInfo_1, amount_3)).thenReturn(transactionID_3);
        when(bank.capture(transactionID_3)).thenReturn(0);
        when(bank.capture(transactionID_3)).thenReturn(-2);

        when(bank.auth(ccInfo_1, amount_4)).thenReturn(transactionID_4);
        when(bank.capture(transactionID_4)).thenReturn(0);
        when(bank.refund(transactionID_4, amount_4)).thenReturn(-4);

        //exercise
        //valid transaction
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(transactionID_1);
        //invalid refund
        int result_operation_2 = paymentProcessor.processPayment( amount_2, transactionID_2);

        int result_operation_3 = paymentProcessor.processPayment(transactionID_3);

        int result_operation_4 = paymentProcessor.processPayment( amount_4, transactionID_4);
        //verify

        assertEquals(0, result_operation_1);
        assertEquals("captured", transactionDB.getTransactionByTransactionID(transactionID_1).getState());
        assertEquals(1, result_operation_2);
        assertTrue(logs.get(0).contains("Transaction does not exist"));
        assertEquals(1, result_operation_3);
        assertTrue(logs.get(1).contains("Transaction has already been captured"));
        assertEquals(1, result_operation_4);
        assertTrue(logs.get(2).contains("Refund is greater than amount captured"));
        assertEquals(3, logs.size());
        assertEquals(8, transactionDB.countTransactions());
        assertEquals("invalid", transactionDB.getTransactionByTransactionID(transactionID_2).getState());
        assertEquals("invalid", transactionDB.getTransactionByTransactionID(transactionID_3).getState());
        assertEquals("invalid", transactionDB.getTransactionByTransactionID(transactionID_4).getState());

    }

    @Test
    public void testInvalidStateInputted() {
        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        long amount = 1000L;

        long transactionID = 1L;

        bank = mock(BankProxy.class);

        when(bank.capture(transactionID)).thenReturn(0);

        Transaction auth_Transaction_1 = new Transaction(0, transactionID, ccInfo_1, amount, "invalid state", getPresentDate());
        transactionDB.saveTransaction(auth_Transaction_1);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(transactionID);


        //verify
        assertEquals(1, result_operation_1);
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("Transaction does not exist"));
        assertEquals(2, transactionDB.countTransactions());
        assertEquals("invalid", transactionDB.getTransactionByTransactionID(transactionID).getState());
    }

    @Test
    public void testMultiple_ValidCaptureTransactions_DifferentAccounts_BankOffline() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        CCInfo ccInfo_2 = new CCInfo("Joe", "333,Test", CardTypes.VISA.toString(), "4111111111111111", "11/2021", "421");
        CCInfo ccInfo_3 = new CCInfo("Mark", "444,Test", CardTypes.MASTERCARD.toString(), "5105105105105100", "11/2022", "213");
        long amount_1 = 1000L;
        long amount_2 = 3000L;
        long amount_3 = 200L;
        long transactionID_1 = 1L;
        long transactionID_2 = 2L;
        long transactionID_3 = 3L;
        Transaction auth_Transaction_1 = new Transaction(0, transactionID_1, ccInfo_1, amount_1, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        Transaction auth_Transaction_2 = new Transaction(1, transactionID_2, ccInfo_2, amount_2, TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());

        transactionDB.saveTransaction(auth_Transaction_1);
        transactionDB.saveTransaction(auth_Transaction_2);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(transactionID_1);
        int result_operation_2 = paymentProcessor.processPayment( amount_2,  transactionID_2);
        int result_operation_3 = paymentProcessor.processPayment(ccInfo_3, amount_3);
        //verify
        assertEquals(2, result_operation_1);
        assertEquals(2, result_operation_2);
        assertEquals(2, result_operation_3);
        assertEquals(3, logs.size());
        assertTrue(logs.get(0).contains("Bank is offline"));
        assertTrue(logs.get(1).contains("Bank is offline"));
        assertTrue(logs.get(2).contains("Bank is offline"));



    }

    @Test
    public void testMultiple_ValidCaptureTransactions_DifferentAccounts_DatabaseOffline() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        CCInfo ccInfo_2 = new CCInfo("Joe", "333,Test", CardTypes.VISA.toString(), "4111111111111111", "11/2021", "421");
        CCInfo ccInfo_3 = new CCInfo("Mark", "444,Test", CardTypes.MASTERCARD.toString(), "5105105105105100", "11/2022", "213");
        long amount_1 = 1000L;
        long amount_2 = 3000L;
        long amount_3 = 200L;
        long transactionID_1 = 1L;
        long transactionID_2 = 2L;
        long transactionID_3 = 3L;

        bank = mock(BankProxy.class);

        when(bank.auth(ccInfo_1, amount_1)).thenReturn(transactionID_1);
        when(bank.capture(transactionID_2)).thenReturn(0);
        when(bank.refund(transactionID_3,amount_3)).thenReturn(0);
        transactionDB = null;

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment( transactionID_1);
        int result_operation_2 = paymentProcessor.processPayment( amount_2, transactionID_2);
        int result_operation_3 = paymentProcessor.processPayment(ccInfo_3, amount_3);
        //verify

        assertEquals(2, result_operation_1);
        assertEquals(2, result_operation_2);
        assertEquals(2, result_operation_3);
        assertEquals(3, logs.size());
        assertTrue(logs.get(0).contains("Database is offline"));
        assertTrue(logs.get(1).contains("Database is offline"));
        assertTrue(logs.get(2).contains("Database is offline"));


    }


    @Test
    public void testSingleOperation_ValidDetails_Authorisation() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
   long amount_1 = 1000L;
   long transactionID_1 = 1L;

        bank = mock(BankProxy.class);

        when(bank.auth(ccInfo_1,amount_1)).thenReturn(transactionID_1);
        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(ccInfo_1, amount_1);

        //verify
        assertEquals(0, result_operation_1);
        assertEquals(0, logs.size());
        assertEquals(1, transactionDB.countTransactions());
        assertEquals("authorised", transactionDB.getTransactionByTransactionID(transactionID_1).getState());

    }


    @Test
    public void testSingleOperation_InvalidCCinfo_InvalidAmount_ValidTransactionID_Authorisation() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "471449635398431", "11/2020", "1234");
        long amount_1 = -1000L;
        long transactionID_1 = 1L;

        bank = mock(BankProxy.class);

        when(bank.auth(ccInfo_1,amount_1)).thenReturn(-2L);
        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(ccInfo_1, amount_1);

        //verify
        assertEquals(1, result_operation_1);
        assertTrue(logs.get(0).contains("Invalid Card Number"));
        assertEquals(1, logs.size());
        assertEquals(1, transactionDB.countTransactions());
        assertEquals("invalid", transactionDB.getTransactionByTransactionID(-1).getState());

    }


    @Test
    public void testSingleOperation_ValidCCinfo_ValidTransactionID_Authorisation() {

        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        long amount_1 = 2000L;
        long transactionID_1 = 1L;

        bank = mock(BankProxy.class);

        when(bank.auth(ccInfo_1,amount_1)).thenReturn(-6L);
        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(ccInfo_1, amount_1);

        //verify
        assertEquals(2, result_operation_1);
        assertTrue(logs.get(0).contains("An unknown error has occurred"));
        assertEquals(1, logs.size());
        assertEquals(1, transactionDB.countTransactions());
        assertEquals("invalid", transactionDB.getTransactionByTransactionID(-1).getState());

    }


    @Test
    public void testSingleOperation_InvalidCCinfo_InvalidTransactionID_Authorisation() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.VISA.toString(), "371449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long transactionID_1 = 1L;

        bank = mock(BankProxy.class);

        when(bank.auth(ccInfo_1,amount_1)).thenReturn(-2L);
        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(ccInfo_1, amount_1);

        //verify
        assertEquals(1, result_operation_1);
        assertTrue(logs.get(0).contains("Invalid Prefix of card"));
        assertEquals(1, logs.size());
        assertEquals(1, transactionDB.countTransactions());
        assertEquals("invalid", transactionDB.getTransactionByTransactionID(-1).getState());

    }

    @Test
    public void testSingleOperation_ValidCCinfo_InvalidAmount_ValidTransactionID_Capture() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long transactionID_1 = 1L;

        bank = mock(BankProxy.class);
        Transaction auth_Transaction_1 = new Transaction(0, transactionID_1, ccInfo_1, amount_1, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction_1);

        when(bank.capture(transactionID_1)).thenReturn(0);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(transactionID_1);

        //verify
        assertEquals(0, result_operation_1);
        assertEquals(0, logs.size());
        assertEquals(2, transactionDB.countTransactions());
        assertEquals("captured", transactionDB.getTransactionByTransactionID(transactionID_1).getState());
    }


    @Test
    public void testSingleOperation_InvalidCCinfo_ValidAmount_ValidTransactionID_Capture() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "471449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long transactionID_1 = 1L;

        bank = mock(BankProxy.class);
        Transaction auth_Transaction_1 = new Transaction(0, transactionID_1, ccInfo_1, amount_1, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction_1);

        when(bank.capture(transactionID_1)).thenReturn(0);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(transactionID_1);

        //verify
        assertEquals(1, result_operation_1);
        assertTrue(logs.get(0).contains("Invalid Card Number"));
        assertEquals(1, logs.size());
        assertEquals(2, transactionDB.countTransactions());
        assertEquals("invalid", transactionDB.getTransactionByTransactionID(transactionID_1).getState());
    }


    @Test
    public void testSingleOperation_ValidCCinfo_InvalidTransactionID_Capture() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long transactionID_1 = 1L;

        bank = mock(BankProxy.class);
        Transaction auth_Transaction_1 = new Transaction(0, transactionID_1, ccInfo_1, amount_1, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction_1);

        when(bank.capture(transactionID_1)).thenReturn(-4);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(transactionID_1);

        //verify
        assertEquals(2, result_operation_1);
        assertTrue(logs.get(0).contains("An unknown error has occurred"));
        assertEquals(1, logs.size());
        assertEquals(2, transactionDB.countTransactions());
        assertEquals("invalid", transactionDB.getTransactionByTransactionID(transactionID_1).getState());
    }

    @Test
    public void testSingleOperation_InvalidCCinfo_InvalidTransactionID_Capture() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.VISA.toString(), "371449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long transactionID_1 = 1L;

        bank = mock(BankProxy.class);
        Transaction auth_Transaction_1 = new Transaction(0, transactionID_1, ccInfo_1, amount_1, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction_1);

        when(bank.capture(transactionID_1)).thenReturn(-4);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(transactionID_1);

        //verify
        assertEquals(1, result_operation_1);
        assertTrue(logs.get(0).contains("Invalid Prefix of card"));
        assertEquals(1, logs.size());
        assertEquals(2, transactionDB.countTransactions());
        assertEquals("invalid", transactionDB.getTransactionByTransactionID(transactionID_1).getState());
    }

    @Test
    public void testSingleOperation_ValidCCinfo_GreaterAmount_ValidTransactionID_Refund() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long transactionID_1 = 1L;
        long invalid_amount = 2000L;
        bank = mock(BankProxy.class);
        Transaction auth_Transaction_1 = new Transaction(0, transactionID_1, ccInfo_1, amount_1, TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction_1);

        when(bank.refund(transactionID_1,amount_1)).thenReturn(0);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(invalid_amount,transactionID_1);

        //verify
        assertEquals(1, result_operation_1);
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("Refund is greater than amount captured"));
        assertEquals(2, transactionDB.countTransactions());
        assertEquals("invalid", transactionDB.getTransactionByTransactionID(transactionID_1).getState());
    }

    @Test
    public void testSingleOperation_InvalidCCinfo_LowerAmount_ValidTransactionID_Refund() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "471449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long transactionID_1 = 1L;
        long invalid_amount = 100L;
        bank = mock(BankProxy.class);
        Transaction auth_Transaction_1 = new Transaction(0, transactionID_1, ccInfo_1, amount_1, TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction_1);

        when(bank.refund(transactionID_1,amount_1)).thenReturn(0);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(invalid_amount,transactionID_1);

        //verify
        assertEquals(1, result_operation_1);
        assertTrue(logs.get(0).contains("Invalid Card Number"));
        assertEquals(1, logs.size());
        assertEquals(2, transactionDB.countTransactions());
        assertEquals("invalid", transactionDB.getTransactionByTransactionID(transactionID_1).getState());
    }

    @Test
    public void testSingleOperation_ValidCCinfo_ValidAmount_InvalidTransactionID_Refund() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long transactionID_1 = 1L;

        bank = mock(BankProxy.class);
        Transaction auth_Transaction_1 = new Transaction(0, transactionID_1, ccInfo_1, amount_1, TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction_1);

        when(bank.refund(transactionID_1,amount_1)).thenReturn(-5);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(amount_1,transactionID_1);

        //verify
        assertEquals(2, result_operation_1);
        assertTrue(logs.get(0).contains("An unknown error has occurred"));
        assertEquals(1, logs.size());
        assertEquals(2, transactionDB.countTransactions());
        assertEquals("invalid", transactionDB.getTransactionByTransactionID(transactionID_1).getState());
    }

    @Test
    public void testSingleOperation_InvalidCCinfo_InvalidAmount_InvalidTransactionID_Refund() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.MASTERCARD.toString(), "371449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long transactionID_1 = 1L;
        long invalid_amount = 100L;
        bank = mock(BankProxy.class);
        Transaction auth_Transaction_1 = new Transaction(0, transactionID_1, ccInfo_1, amount_1, TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction_1);

        when(bank.refund(transactionID_1,amount_1)).thenReturn(-4);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(invalid_amount,transactionID_1);

        //verify
        assertEquals(1, result_operation_1);
        assertTrue(logs.get(0).contains("Invalid Prefix of card"));
        assertEquals(1, logs.size());
        assertEquals(2, transactionDB.countTransactions());
        assertEquals("invalid", transactionDB.getTransactionByTransactionID(transactionID_1).getState());
    }

    @Test
    public void testSingleOperation_VoidState() {
        //setup
        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long transactionID_1 = 1L;

        bank = mock(BankProxy.class);
        Transaction auth_Transaction_1 = new Transaction(0, transactionID_1, ccInfo_1, amount_1, TestBankOperation.VOID.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction_1);

        when(bank.refund(transactionID_1,amount_1)).thenReturn(0);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment( amount_1, transactionID_1);

        //verify
        assertEquals(2, result_operation_1);
        assertTrue(logs.get(0).contains("An unknown error has occurred"));
        assertEquals(1, logs.size());
        assertEquals(2, transactionDB.countTransactions());
        assertEquals("invalid", transactionDB.getTransactionByTransactionID(transactionID_1).getState());
    }

    @Test
    public void testSingleOperation_ResultsInVoidState() {
        //setup
        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long transactionID_1 = 1L;
        Calendar oldDate = getPresentDate();
        oldDate.set(Calendar.MONTH, -1);
        bank = mock(BankProxy.class);
        Transaction auth_Transaction_1 = new Transaction(0, transactionID_1, ccInfo_1, amount_1, TestBankOperation.AUTHORISED.toString().toLowerCase(), oldDate);
        transactionDB.saveTransaction(auth_Transaction_1);

        when(bank.capture(transactionID_1)).thenReturn(0);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(transactionID_1);

        //verify
        assertEquals(1, result_operation_1);
        assertEquals(2, transactionDB.countTransactions());
        assertEquals("void", transactionDB.getTransactionByTransactionID(transactionID_1).getState());
    }

    @Test
    public void testSingleOperation_InvalidState() {
        //setup
        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long transactionID_1 = 1L;

        bank = mock(BankProxy.class);
        Transaction auth_Transaction_1 = new Transaction(0, transactionID_1, ccInfo_1, amount_1, TestBankOperation.INVALID.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction_1);

        when(bank.refund(transactionID_1,amount_1)).thenReturn(0);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment( amount_1, transactionID_1);

        //verify
        assertEquals(2, result_operation_1);
        assertTrue(logs.get(0).contains("An unknown error has occurred"));
        assertEquals(1, logs.size());
        assertEquals(2, transactionDB.countTransactions());
        assertEquals("invalid", transactionDB.getTransactionByTransactionID(transactionID_1).getState());
    }

    @Test
    public void testSingleOperation_ValidDetails_Capture() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long transactionID_1 = 1L;

        bank = mock(BankProxy.class);
        Transaction auth_Transaction_1 = new Transaction(0, transactionID_1, ccInfo_1, amount_1, TestBankOperation.AUTHORISED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction_1);

        when(bank.capture(transactionID_1)).thenReturn(0);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(transactionID_1);

        //verify
        assertEquals(0, result_operation_1);
        assertEquals(0, logs.size());
        assertEquals(2, transactionDB.countTransactions());
        assertEquals("captured", transactionDB.getTransactionByTransactionID(transactionID_1).getState());
    }

    @Test
    public void testSingleOperation_ValidDetails_Refund() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long transactionID_1 = 1L;

        bank = mock(BankProxy.class);
        Transaction auth_Transaction_1 = new Transaction(0, transactionID_1, ccInfo_1, amount_1, TestBankOperation.CAPTURED.toString().toLowerCase(), getPresentDate());
        transactionDB.saveTransaction(auth_Transaction_1);

        when(bank.refund(transactionID_1,amount_1)).thenReturn(0);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment( amount_1, transactionID_1);

        //verify
        assertEquals(0, result_operation_1);
        assertEquals(0, logs.size());
        assertEquals(2, transactionDB.countTransactions());
        assertEquals("refunded", transactionDB.getTransactionByTransactionID(transactionID_1).getState());


    }
}
