package PaymentProcessor;

import Bank.BankProxy;
import CardInfo.CCInfo;
import CardInfo.enums.CardTypes;
import PaymentProcessor.enums.TestCardTypes;
import PaymentProcessor.enums.TestBankOperation;
import TransactionDatabase.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaymentProcessorTest {

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

    @Test
    public void testMultiple_CaptureTransactions() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", CardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        CCInfo ccInfo_2 = new CCInfo("Joe", "333,Test", CardTypes.VISA.toString(), "4111111111111111", "11/2021", "421");
        CCInfo ccInfo_3 = new CCInfo("Mark", "444,Test", CardTypes.MASTERCARD.toString(), "5105105105105100", "11/2022", "213");
        long amount_1 = 1000L;
        long amount_2 = 3000L;
        long amount_3 = 200L;
        long transactionID_1 =  1L;
        long transactionID_2 =  2L;
        long transactionID_3 =  3L;
        Transaction auth_Transaction_1 = new Transaction(transactionID_1,ccInfo_1,amount_1,TestBankOperation.AUTHORISE.toString().toLowerCase(), Calendar.getInstance());
        Transaction auth_Transaction_2 = new Transaction(transactionID_2,ccInfo_2,amount_2,TestBankOperation.AUTHORISE.toString().toLowerCase(), Calendar.getInstance());
        Transaction auth_Transaction_3 = new Transaction(transactionID_3,ccInfo_3,amount_3,TestBankOperation.AUTHORISE.toString().toLowerCase(), Calendar.getInstance());
        transactionDB.saveTransaction(auth_Transaction_1);
        transactionDB.saveTransaction(auth_Transaction_2);
        transactionDB.saveTransaction(auth_Transaction_3);
        bank = mock(BankProxy.class);

        when(bank.capture(transactionID_1)).thenReturn(0);
        when(bank.capture(transactionID_2)).thenReturn(0);
        when(bank.capture(transactionID_3)).thenReturn(0);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(ccInfo_1, amount_1, TestBankOperation.CAPTURE.toString(),transactionID_1);
        int result_operation_2 = paymentProcessor.processPayment(ccInfo_2, amount_2, TestBankOperation.CAPTURE.toString(),transactionID_2);
        int result_operation_3 = paymentProcessor.processPayment(ccInfo_3, amount_3, TestBankOperation.CAPTURE.toString(),transactionID_3);
        //verify
        assertEquals(0, result_operation_1);
        assertEquals(0, result_operation_2);
        assertEquals(0, result_operation_3);
        assertEquals(0,logs.size());
        assertEquals(3,transactionDB.countTransactions());
        assertEquals("capture",transactionDB.getTransaction(transactionID_1).getState());
        assertEquals("capture",transactionDB.getTransaction(transactionID_2).getState());
        assertEquals("capture",transactionDB.getTransaction(transactionID_3).getState());

    }

    //two transactions have errors in the CCinfo(ccInfo_1 missing card number - ccInfo_2 missing card number) and one transaction results an invalid transaction from the bank
    @Test
    public void testMultiple_ErrorTransactions() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "", "11/2020", "1234");
        CCInfo ccInfo_2 = new CCInfo("Joe", "333,Test", TestCardTypes.MASTERCARD.toString(), "4111111111111111", "11/2021", "431");
        CCInfo ccInfo_3 = new CCInfo("Mark", "444,Test", TestCardTypes.MASTERCARD.toString(), "5105105105105100", "11/2022", "213");
        long amount_1 = 1000L;
        long amount_2 = 3000L;
        long amount_3 = 200L;
        long transactionID_1 = 1L;
        long transactionID_2 = 2L;
        long transactionID_3 = 3L;

        Transaction auth_Transaction_1 = new Transaction(transactionID_1,ccInfo_1,amount_1,TestBankOperation.AUTHORISE.toString().toLowerCase(), Calendar.getInstance());
        Transaction auth_Transaction_2 = new Transaction(transactionID_2,ccInfo_2,amount_2,TestBankOperation.AUTHORISE.toString().toLowerCase(), Calendar.getInstance());
        Transaction auth_Transaction_3 = new Transaction(transactionID_3,ccInfo_3,amount_3,TestBankOperation.AUTHORISE.toString().toLowerCase(), Calendar.getInstance());

        transactionDB.saveTransaction(auth_Transaction_1);
        transactionDB.saveTransaction(auth_Transaction_2);
        transactionDB.saveTransaction(auth_Transaction_3);

        bank = mock(BankProxy.class);

        when(bank.capture(transactionID_1)).thenReturn(0);
        when(bank.capture(transactionID_2)).thenReturn(0);
        when(bank.capture(transactionID_3)).thenReturn(-1);



        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(ccInfo_1, amount_1, TestBankOperation.CAPTURE.toString(),transactionID_1);

        paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_2 = paymentProcessor.processPayment(ccInfo_2, amount_2, TestBankOperation.CAPTURE.toString(),transactionID_2);

        paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_3 = paymentProcessor.processPayment(ccInfo_3, amount_3, TestBankOperation.CAPTURE.toString(),transactionID_3);
        //verify
        assertEquals(1, result_operation_1);
        assertEquals(1, result_operation_2);
        assertEquals(1, result_operation_3);
        assertEquals(3,logs.size());
        assertEquals(3,transactionDB.countTransactions());
        assertTrue(logs.get(0).contains("Invalid Card Number"));
        assertTrue(logs.get(1).contains("Invalid Prefix of card"));
        assertTrue(logs.get(2).contains("Transaction does not exist"));
        assertEquals("invalid",transactionDB.getTransaction(transactionID_1).getState());
        assertEquals("invalid",transactionDB.getTransaction(transactionID_2).getState());
        assertEquals("invalid",transactionDB.getTransaction(transactionID_3).getState());


    }

    @Test
    public void testMultiple_ValidAndErrorTransactions() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        CCInfo ccInfo_2 = new CCInfo("Joe", "333,Test", TestCardTypes.MASTERCARD.toString(), "4111111111111111", "11/2021", "432");
        CCInfo ccInfo_3 = new CCInfo("Mark", "444,Test", TestCardTypes.MASTERCARD.toString(), "5105105105105100", "11/2022", "243");
        long amount_1 = 1000L;
        long amount_2 = 3000L;
        long amount_3 = 200L;
        long transactionID_1 =  1L;
        long transactionID_2 =  2L;
        long transactionID_3 =  3L;

        Transaction auth_Transaction_1 = new Transaction(transactionID_1,ccInfo_1,amount_1,TestBankOperation.AUTHORISE.toString().toLowerCase(), Calendar.getInstance());
        Transaction auth_Transaction_2 = new Transaction(transactionID_2,ccInfo_2,amount_2,TestBankOperation.AUTHORISE.toString().toLowerCase(), Calendar.getInstance());
        Transaction auth_Transaction_3 = new Transaction(transactionID_3,ccInfo_3,amount_3,TestBankOperation.AUTHORISE.toString().toLowerCase(), Calendar.getInstance());

        transactionDB.saveTransaction(auth_Transaction_1);
        transactionDB.saveTransaction(auth_Transaction_2);
        transactionDB.saveTransaction(auth_Transaction_3);

        bank = mock(BankProxy.class);

        when(bank.capture(transactionID_1)).thenReturn(0);
        when(bank.capture(transactionID_2)).thenReturn(0);
        when(bank.capture(transactionID_3)).thenReturn(-1);

        //exercise

        //valid operation
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB,  logs);
        int result_operation_1 = paymentProcessor.processPayment(ccInfo_1, amount_1, TestBankOperation.CAPTURE.toString(),transactionID_1);
        //invalid prefix of card
        int result_operation_2 = paymentProcessor.processPayment(ccInfo_2, amount_2, TestBankOperation.CAPTURE.toString(),transactionID_2);
        int result_operation_3 = paymentProcessor.processPayment(ccInfo_3, amount_3, TestBankOperation.CAPTURE.toString(),transactionID_3);
        //verify

        assertEquals(0, result_operation_1);
        assertEquals("capture",transactionDB.getTransaction(transactionID_1).getState());
        assertEquals(1, result_operation_2);
        assertEquals(1, result_operation_3);
        assertEquals(2,logs.size());
        assertTrue(logs.get(0).contains("Invalid Prefix of card"));
        assertTrue(logs.get(1).contains("Transaction does not exist"));

        assertEquals(3,transactionDB.countTransactions());


        assertEquals("invalid",transactionDB.getTransaction(transactionID_3).getState());
    }

    @Test
    public void testMultipleValidOperations_SameAccount_DifferentTransactions(){
        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long amount_2 = 2000L;
        long amount_3 = 3000L;
        long amount_4 = 400L;

        long transactionID_1 = 1L;
        long transactionID_2 = 2L;
        long transactionID_3 = 3L;
        long transactionID_4 = 4L;

        Transaction auth_Transaction_1 = new Transaction(transactionID_1,ccInfo_1,amount_1,TestBankOperation.AUTHORISE.toString().toLowerCase(), Calendar.getInstance());
        Transaction auth_Transaction_2 = new Transaction(transactionID_2,ccInfo_1,amount_2,TestBankOperation.CAPTURE.toString().toLowerCase(), Calendar.getInstance());
        Transaction auth_Transaction_3 = new Transaction(transactionID_3,ccInfo_1,amount_3,TestBankOperation.AUTHORISE.toString().toLowerCase(), Calendar.getInstance());
        Transaction auth_Transaction_4 = new Transaction(transactionID_4,ccInfo_1,amount_4,TestBankOperation.CAPTURE.toString().toLowerCase(), Calendar.getInstance());
        transactionDB.saveTransaction(auth_Transaction_1);
        transactionDB.saveTransaction(auth_Transaction_2);
        transactionDB.saveTransaction(auth_Transaction_3);
        transactionDB.saveTransaction(auth_Transaction_4);

        bank = mock(BankProxy.class);

        when(bank.capture(transactionID_1)).thenReturn(0);
        when(bank.refund(transactionID_2,amount_2)).thenReturn(0);
        when(bank.capture(transactionID_3)).thenReturn(0);
        when(bank.refund(transactionID_4,amount_4)).thenReturn(0);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(ccInfo_1, amount_1, TestBankOperation.CAPTURE.toString(),transactionID_1);
        int result_operation_2 = paymentProcessor.processPayment(ccInfo_1, amount_2, TestBankOperation.REFUND.toString(),transactionID_2);
        int result_operation_3 = paymentProcessor.processPayment(ccInfo_1, amount_3, TestBankOperation.CAPTURE.toString(),transactionID_3);
        int result_operation_4 = paymentProcessor.processPayment(ccInfo_1, amount_4, TestBankOperation.REFUND.toString(),transactionID_4);
        //verify

        assertEquals(0, result_operation_1);
        assertEquals("capture",transactionDB.getTransaction(transactionID_1).getState());
        assertEquals(0, result_operation_2);
        assertEquals("refund",transactionDB.getTransaction(transactionID_2).getState());
        assertEquals(0, result_operation_3);
        assertEquals("capture",transactionDB.getTransaction(transactionID_3).getState());
        assertEquals(0, result_operation_4);
        assertEquals("refund",transactionDB.getTransaction(transactionID_4).getState());
        assertEquals(0,logs.size());
        assertEquals(4,transactionDB.countTransactions());

    }


    @Test
    public void testMultipleValidOperations_SameAccount_SameTransactions(){
        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        long amount = 1000L;

        long transactionID = 1L;

        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo_1, amount)).thenReturn(transactionID);
        when(bank.capture(transactionID)).thenReturn(0);

        when(bank.auth(ccInfo_1, amount)).thenReturn(transactionID);
        when(bank.refund(transactionID,amount)).thenReturn(0);
        Transaction auth_Transaction_1 = new Transaction(transactionID,ccInfo_1,amount,TestBankOperation.AUTHORISE.toString().toLowerCase(), Calendar.getInstance());
        transactionDB.saveTransaction(auth_Transaction_1);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(ccInfo_1, amount, TestBankOperation.CAPTURE.toString(),transactionID);
        int result_operation_2 = paymentProcessor.processPayment(ccInfo_1, amount, TestBankOperation.REFUND.toString(),transactionID);


        //verify
        assertEquals(0, result_operation_1);
        assertEquals("refund",transactionDB.getTransaction(transactionID).getState());
        assertEquals(0, result_operation_2);
        assertEquals(0,logs.size());
        assertEquals(1,transactionDB.countTransactions());
    }

    @Test
    public void testMultipleValidAndInvalidOperations_SameAccount(){
        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long amount_2 = 2000L;
        long amount_3 = 3000L;
        long amount_4 = 400L;

        long transactionID_1 = 1L;
        long transactionID_2 = 2L;
        long transactionID_3 = 3L;
        long transactionID_4 = 4L;

        Transaction auth_Transaction_1 = new Transaction(transactionID_1,ccInfo_1,amount_1,TestBankOperation.AUTHORISE.toString().toLowerCase(), Calendar.getInstance());
        Transaction auth_Transaction_2 = new Transaction(transactionID_2,ccInfo_1,amount_2,TestBankOperation.CAPTURE.toString().toLowerCase(), Calendar.getInstance());
        Transaction auth_Transaction_3 = new Transaction(transactionID_3,ccInfo_1,amount_3,TestBankOperation.AUTHORISE.toString().toLowerCase(), Calendar.getInstance());
        Transaction auth_Transaction_4 = new Transaction(transactionID_4,ccInfo_1,amount_4,TestBankOperation.CAPTURE.toString().toLowerCase(), Calendar.getInstance());
        transactionDB.saveTransaction(auth_Transaction_1);
        transactionDB.saveTransaction(auth_Transaction_2);
        transactionDB.saveTransaction(auth_Transaction_3);
        transactionDB.saveTransaction(auth_Transaction_4);

        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo_1, amount_1)).thenReturn(transactionID_1);
        when(bank.capture(transactionID_1)).thenReturn(0);

        when(bank.auth(ccInfo_1, amount_2)).thenReturn(transactionID_2);
        when(bank.capture(transactionID_2)).thenReturn(0);
        when(bank.refund(transactionID_2,amount_2)).thenReturn(-1);

        when(bank.auth(ccInfo_1, amount_3)).thenReturn(transactionID_3);
        when(bank.capture(transactionID_3)).thenReturn(0);
        when(bank.capture(transactionID_3)).thenReturn(-2);

        when(bank.auth(ccInfo_1, amount_4)).thenReturn(transactionID_4);
        when(bank.capture(transactionID_4)).thenReturn(0);
        when(bank.refund(transactionID_4,amount_4)).thenReturn(-4);

        //exercise
        //valid transaction
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank, transactionDB, logs);
        int result_operation_1 = paymentProcessor.processPayment(ccInfo_1, amount_1, TestBankOperation.CAPTURE.toString(),transactionID_1);
        //invalid refund
        int result_operation_2 = paymentProcessor.processPayment(ccInfo_1, amount_2, TestBankOperation.REFUND.toString(),transactionID_2);

        int result_operation_3 = paymentProcessor.processPayment(ccInfo_1, amount_3, TestBankOperation.CAPTURE.toString(),transactionID_3);

        int result_operation_4 = paymentProcessor.processPayment(ccInfo_1, amount_4, TestBankOperation.REFUND.toString(),transactionID_4);
        //verify

        assertEquals(0, result_operation_1);
        assertEquals("capture",transactionDB.getTransaction(transactionID_1).getState());
        assertEquals(1, result_operation_2);
        assertTrue(logs.get(0).contains("Transaction does not exist"));
        assertEquals(1, result_operation_3);
        assertTrue(logs.get(1).contains("Transaction has already been captured"));
        assertEquals(1, result_operation_4);
        assertTrue(logs.get(2).contains("Refund is greater than amount captured"));
        assertEquals(3,logs.size());
        assertEquals(4,transactionDB.countTransactions());
        assertEquals("invalid",transactionDB.getTransaction(transactionID_2).getState());
        assertEquals("invalid",transactionDB.getTransaction(transactionID_3).getState());
        assertEquals("invalid",transactionDB.getTransaction(transactionID_4).getState());


    }


    // CREATE A METHOD WHERE THE PREVIOUS TRANSACTION IS INCORRECTLY CALLED FROM THE BANK AND THE LOCAL TRANSACTION DATABASE IS REFERENCED

}
