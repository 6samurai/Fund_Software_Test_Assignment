package PaymentProcessor;

import Bank.BankProxy;
import CardInfo.CCInfo;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import TransactionDatabase.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PaymentProcessorTests {

    CCInfo ccInfo;
    TransactionDatabase transactionDB;
    @Before
    public  void setup(){
         transactionDB = new TransactionDatabase();
    }

    @After
    public  void teardown(){
        transactionDB= null;
        ccInfo = null;
    }


    @Test
    public void testVerifyOperation_LuhnCheck(){
        ccInfo = new CCInfo("Chris","222,Test","American Express","371449635398431","11/2020","1234");
        PaymentProcessor paymentProcessor = new PaymentProcessor();
        int check = paymentProcessor.verifyLuhn(ccInfo.getCardNumber());
        assertEquals (0,check);

    }

    @Test
    public void testVerifyOperation_LuhnCheck_Blank(){
        ccInfo = new CCInfo("Chris","222,Test","American Express","","11/2020","1234");
        PaymentProcessor paymentProcessor = new PaymentProcessor();
        int check = paymentProcessor.verifyLuhn(ccInfo.getCardNumber());
        assertEquals (1,check);
    }

    @Test
    public void testVerifyOperation_LuhnCheck_InvalidValue(){
        ccInfo = new CCInfo("Chris","222,Test","American Express","2132132131312321","11/2020","1234");
        PaymentProcessor paymentProcessor = new PaymentProcessor();
        int check = paymentProcessor.verifyLuhn(ccInfo.getCardNumber());
        assertEquals (0,check);
    }

    @Test
    public void testValidAuthorisationRequest(){

        //setup
        ccInfo = new CCInfo("Chris","222,Test","American Express","2132132131312321","11/2020","1234");
       // Transaction transaction = new Transaction();

        BankProxy bank = mock(BankProxy.class);
        when(bank.auth(ccInfo,1000)).thenReturn(10000L);

      //  transactionDB.saveTransaction();
    }

}
