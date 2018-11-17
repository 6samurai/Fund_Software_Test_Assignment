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
import java.util.Random;

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
        long transactionID_1 =  new Random().nextLong();
        long transactionID_2 =  new Random().nextLong();
        long transactionID_3 =  new Random().nextLong();
        transactionID_1 = (transactionID_1>0)? transactionID_1 : transactionID_1 *-1;
        transactionID_2 = (transactionID_2>0)? transactionID_2 : transactionID_2 *-1;
        transactionID_3 = (transactionID_3>0)? transactionID_3 : transactionID_3 *-1;

        bank = mock(BankProxy.class);
        when(bank.auth(ccInfo_1, amount_1)).thenReturn(transactionID_1);
        when(bank.capture(transactionID_1)).thenReturn(0);
        when(bank.auth(ccInfo_2, amount_2)).thenReturn(transactionID_2);
        when(bank.capture(transactionID_2)).thenReturn(0);
        when(bank.auth(ccInfo_3, amount_3)).thenReturn(transactionID_3);
        when(bank.capture(transactionID_3)).thenReturn(0);

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
        long transactionID_1 =  new Random().nextLong();
        long transactionID_2 =  new Random().nextLong();
        long transactionID_3 =  new Random().nextLong();
        transactionID_1 = (transactionID_1>0)? transactionID_1 : transactionID_1 *-1;
        transactionID_2 = (transactionID_2>0)? transactionID_2 : transactionID_2 *-1;
        transactionID_3 = (transactionID_3>0)? transactionID_3 : transactionID_3 *-1;

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
    public void testMultiple_ValidandErrorTransactions() {
        //setup

        CCInfo ccInfo_1 = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "1234");
        CCInfo ccInfo_2 = new CCInfo("Joe", "333,Test", "Mastercard", "4111111111111111", "11/2021", "4321");
        CCInfo ccInfo_3 = new CCInfo("Mark", "444,Test", "Mastercard", "5105105105105100", "11/2022", "2143");
        long amount_1 = 1000L;
        long amount_2 = 3000L;
        long amount_3 = 200L;
        long transactionID_1 =  new Random().nextLong();
        long transactionID_2 =  new Random().nextLong();
        long transactionID_3 =  new Random().nextLong();
        transactionID_1 = (transactionID_1>0)? transactionID_1 : transactionID_1 *-1;
        transactionID_2 = (transactionID_2>0)? transactionID_2 : transactionID_2 *-1;
        transactionID_3 = (transactionID_3>0)? transactionID_3 : transactionID_3 *-1;

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




}
