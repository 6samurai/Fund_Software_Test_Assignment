package PaymentProcessor;

import Bank.BankProxy;
import CardInfo.CCInfo;
import PaymentProcessor.Enums.BankOperations;
import TransactionDatabase.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
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

    @Test
    public void testMultiple_CaptureTransactions() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "1234");
        CCInfo ccInfo_2 = new CCInfo("Joe", "333,Test", "VISA", "4111111111111111", "11/2021", "4321");
        CCInfo ccInfo_3 = new CCInfo("Mark", "444,Test", "Mastercard", "5105105105105100", "11/2022", "2143");
        long amount_1 = 1000L;
        long amount_2 = 3000L;
        long amount_3 = 200L;
        long transactionID_1 =  1L;
        long transactionID_2 =  2L;
        long transactionID_3 =  3L;


        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo_1, amount_1)).thenReturn(transactionID_1);
        when(bank.capture(transactionID_1)).thenReturn(0);
        when(bank.auth(ccInfo_2, amount_2)).thenReturn(transactionID_2);
        when(bank.capture(transactionID_2)).thenReturn(0);
        when(bank.auth(ccInfo_3, amount_3)).thenReturn(transactionID_3);
        when(bank.refund(transactionID_3,amount_3)).thenReturn(0);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionID_1, transactionDB, BankOperations.CAPTURE, logs);
        int result_operation_1 = paymentProcessor.processPayment(ccInfo_1, amount_1);

        paymentProcessor = new PaymentProcessor(bank,transactionID_2, transactionDB, BankOperations.CAPTURE, logs);
        int result_operation_2 = paymentProcessor.processPayment(ccInfo_2, amount_2);

        paymentProcessor = new PaymentProcessor(bank,transactionID_3, transactionDB, BankOperations.CAPTURE, logs);
        int result_operation_3 = paymentProcessor.processPayment(ccInfo_3, amount_3);
        //verify
        assertEquals(0, result_operation_1);
        assertEquals(0, result_operation_2);
        assertEquals(0, result_operation_3);
        assertEquals(0,logs.size());
        assertEquals(3,transactionDB.countTransactions());
        assertEquals("capture",transactionDB.getTransaction(transactionID_1).getState());

        assertEquals("capture",transactionDB.getTransaction(transactionID_2).getState());

    }

    //two transactions have errors in the CCinfo and one transaction results an invalid transaction from the bank
    @Test
    public void testMultiple_ErrorTransactions() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", "American Express", "", "11/2020", "1234");
        CCInfo ccInfo_2 = new CCInfo("Joe", "333,Test", "Mastercard", "4111111111111111", "11/2021", "4321");
        CCInfo ccInfo_3 = new CCInfo("Mark", "444,Test", "Mastercard", "5105105105105100", "11/2022", "2143");
        long amount_1 = 1000L;
        long amount_2 = 3000L;
        long amount_3 = 200L;
        long transactionID_1 = 1L;
        long transactionID_2 = 2L;
        long transactionID_3 = 3L;

        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo_1, amount_1)).thenReturn(transactionID_1);
        when(bank.capture(transactionID_1)).thenReturn(0);
        when(bank.auth(ccInfo_2, amount_2)).thenReturn(transactionID_2);
        when(bank.capture(transactionID_2)).thenReturn(0);
        when(bank.auth(ccInfo_3, amount_3)).thenReturn(transactionID_3);
        when(bank.capture(transactionID_3)).thenReturn(-1);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionID_1, transactionDB, BankOperations.CAPTURE, logs);
        int result_operation_1 = paymentProcessor.processPayment(ccInfo_1, amount_1);

        paymentProcessor = new PaymentProcessor(bank,transactionID_2, transactionDB, BankOperations.CAPTURE, logs);
        int result_operation_2 = paymentProcessor.processPayment(ccInfo_2, amount_2);

        paymentProcessor = new PaymentProcessor(bank,transactionID_3, transactionDB, BankOperations.CAPTURE, logs);
        int result_operation_3 = paymentProcessor.processPayment(ccInfo_3, amount_3);
        //verify
        assertEquals(1, result_operation_1);
        assertEquals(1, result_operation_2);
        assertEquals(1, result_operation_3);
        assertEquals(3,logs.size());
        assertEquals(0,transactionDB.countTransactions());

    }

    @Test
    public void testMultiple_ValidAndErrorTransactions() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "1234");
        CCInfo ccInfo_2 = new CCInfo("Joe", "333,Test", "Mastercard", "4111111111111111", "11/2021", "4321");
        CCInfo ccInfo_3 = new CCInfo("Mark", "444,Test", "Mastercard", "5105105105105100", "11/2022", "2143");
        long amount_1 = 1000L;
        long amount_2 = 3000L;
        long amount_3 = 200L;
        long transactionID_1 =  1L;
        long transactionID_2 =  2L;
        long transactionID_3 =  3L;


        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo_1, amount_1)).thenReturn(transactionID_1);
        when(bank.capture(transactionID_1)).thenReturn(0);
        when(bank.auth(ccInfo_2, amount_2)).thenReturn(transactionID_2);
        when(bank.capture(transactionID_2)).thenReturn(0);
        when(bank.auth(ccInfo_3, amount_3)).thenReturn(transactionID_3);
        when(bank.capture(transactionID_3)).thenReturn(-1);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionID_1, transactionDB, BankOperations.CAPTURE, logs);
        int result_operation_1 = paymentProcessor.processPayment(ccInfo_1, amount_1);

        paymentProcessor = new PaymentProcessor(bank,transactionID_2, transactionDB, BankOperations.CAPTURE, logs);
        int result_operation_2 = paymentProcessor.processPayment(ccInfo_2, amount_2);

        paymentProcessor = new PaymentProcessor(bank,transactionID_3, transactionDB, BankOperations.CAPTURE, logs);
        int result_operation_3 = paymentProcessor.processPayment(ccInfo_3, amount_3);
        //verify

        assertEquals(0, result_operation_1);
        assertEquals("capture",transactionDB.getTransaction(transactionID_1).getState());
        assertEquals(1, result_operation_2);
        assertEquals(1, result_operation_3);
        assertEquals(2,logs.size());
        assertEquals(1,transactionDB.countTransactions());

    }

    @Test
    public void testMultipleValidOperations_SameAccount_DifferentTransactions(){
        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long amount_2 = 2000L;
        long amount_3 = 3000L;
        long amount_4 = 400L;

        long transactionID_1 = 1L;
        long transactionID_2 = 2L;
        long transactionID_3 = 3L;
        long transactionID_4 = 4L;


        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo_1, amount_1)).thenReturn(transactionID_1);
        when(bank.capture(transactionID_1)).thenReturn(0);

        when(bank.auth(ccInfo_1, amount_2)).thenReturn(transactionID_2);
        when(bank.refund(transactionID_2,amount_2)).thenReturn(0);

        when(bank.auth(ccInfo_1, amount_3)).thenReturn(transactionID_3);
        when(bank.capture(transactionID_3)).thenReturn(0);

        when(bank.auth(ccInfo_1, amount_4)).thenReturn(transactionID_3);
        when(bank.refund(transactionID_4,amount_4)).thenReturn(0);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionID_1, transactionDB, BankOperations.CAPTURE, logs);
        int result_operation_1 = paymentProcessor.processPayment(ccInfo_1, amount_1);

        paymentProcessor = new PaymentProcessor(bank,transactionID_2, transactionDB, BankOperations.REFUND, logs);
        int result_operation_2 = paymentProcessor.processPayment(ccInfo_1, amount_2);

        paymentProcessor = new PaymentProcessor(bank,transactionID_3, transactionDB, BankOperations.CAPTURE, logs);
        int result_operation_3 = paymentProcessor.processPayment(ccInfo_1, amount_3);

        paymentProcessor = new PaymentProcessor(bank,transactionID_4, transactionDB, BankOperations.REFUND, logs);
        int result_operation_4 = paymentProcessor.processPayment(ccInfo_1, amount_4);
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
        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "1234");
        long amount = 1000L;

        long transactionID = 1L;

        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo_1, amount)).thenReturn(transactionID);
        when(bank.capture(transactionID)).thenReturn(0);

        when(bank.auth(ccInfo_1, amount)).thenReturn(transactionID);
        when(bank.refund(transactionID,amount)).thenReturn(0);



        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionID, transactionDB, BankOperations.CAPTURE, logs);
        int result_operation_1 = paymentProcessor.processPayment(ccInfo_1, amount);

        paymentProcessor = new PaymentProcessor(bank,transactionID, transactionDB, BankOperations.REFUND, logs);
        int result_operation_2 = paymentProcessor.processPayment(ccInfo_1, amount);


        //verify
        assertEquals(0, result_operation_1);
        assertEquals("refund",transactionDB.getTransaction(transactionID).getState());
        assertEquals(0, result_operation_2);
        assertEquals(0,logs.size());
        assertEquals(1,transactionDB.countTransactions());
    }

    @Test
    public void testMultipleValidAndInvalidOperations_SameAccount(){
        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "1234");
        long amount_1 = 1000L;
        long amount_2 = 2000L;
        long amount_3 = 3000L;
        long amount_4 = 400L;

        long transactionID_1 = 1L;
        long transactionID_2 = 2L;
        long transactionID_3 = 3L;
        long transactionID_4 = 4L;

        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo_1, amount_1)).thenReturn(transactionID_1);
        when(bank.capture(transactionID_1)).thenReturn(0);

        when(bank.auth(ccInfo_1, amount_2)).thenReturn(transactionID_2);
        when(bank.refund(transactionID_2,amount_2)).thenReturn(-1);

        when(bank.auth(ccInfo_1, amount_3)).thenReturn(transactionID_3);
        when(bank.capture(transactionID_3)).thenReturn(-2);

        when(bank.auth(ccInfo_1, amount_4)).thenReturn(transactionID_3);
        when(bank.refund(transactionID_4,amount_4)).thenReturn(-4);

        //exercise
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionID_1, transactionDB, BankOperations.CAPTURE, logs);
        int result_operation_1 = paymentProcessor.processPayment(ccInfo_1, amount_1);

        paymentProcessor = new PaymentProcessor(bank,transactionID_2, transactionDB, BankOperations.REFUND, logs);
        int result_operation_2 = paymentProcessor.processPayment(ccInfo_1, amount_2);

        paymentProcessor = new PaymentProcessor(bank,transactionID_3, transactionDB, BankOperations.CAPTURE, logs);
        int result_operation_3 = paymentProcessor.processPayment(ccInfo_1, amount_3);

        paymentProcessor = new PaymentProcessor(bank,transactionID_4, transactionDB, BankOperations.REFUND, logs);
        int result_operation_4 = paymentProcessor.processPayment(ccInfo_1, amount_4);
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
        assertEquals(1,transactionDB.countTransactions());

    }


}
