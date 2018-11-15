package RefundTests;

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
import java.util.Random;

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
        bank = mock(BankProxy.class);
        transactionID =  new Random().nextLong();
        transactionID = (transactionID>0)? transactionID : transactionID *-1;
        when(bank.auth(ccInfo, amount)).thenReturn(transactionID);
        when(bank.capture(transactionID)).thenReturn(0);
        when(bank.refund(transactionID,amount)).thenReturn(0);
        PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionID, transactionDB, BankOperations.REFUND, logs);

        //exercise
        int result = paymentProcessor.processPayment(ccInfo, amount);

        //verify
        assertEquals(0, result);
        assertEquals(0,logs.size());
        assertEquals(1,transactionDB.countTransactions());
        assertEquals("refund",transactionDB.getTransaction(transactionID).getState());
    }
}
