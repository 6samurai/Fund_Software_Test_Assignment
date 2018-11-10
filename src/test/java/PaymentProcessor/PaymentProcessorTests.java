package PaymentProcessor;

import Bank.BankProxy;
import CardInfo.CCInfo;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import PaymentProcessor.ErrorMessages.InsufficientFunds;
import PaymentProcessor.ErrorMessages.InvalidCreditCardDetails;
import PaymentProcessor.ErrorMessages.UnknownError;
import TransactionDatabase.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PaymentProcessorTests {

    CCInfo ccInfo;
    TransactionDatabase transactionDB;
    BankProxy bank;
    String operation;
    @Before
    public  void setup(){
         transactionDB = new TransactionDatabase();
         operation = "";
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
        assertEquals (1,check);
    }


    @Test
    public void testInvalidAuthorisationRequest_InvalidCreditCardDetails() throws Exception{
        boolean errorThrown = false;
        try{

            //setup
            ccInfo = new CCInfo("Chris","222,Test","American Express","371449635398431","11/2020","1234");
            long amount = 1000L;
            BankProxy bank = mock(BankProxy.class);
            when(bank.auth(ccInfo,amount)).thenReturn(-1L);
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionDB,"");

            //exercise
            paymentProcessor.processPayment(ccInfo,amount);

        }catch (InvalidCreditCardDetails e){
            errorThrown = true;
        }

        //verify
        assertTrue(errorThrown);
    }


    @Test
    public void testInvalidAuthorisationRequest_InsufficientFunds() throws Exception{
        boolean errorThrown = false;
        try{

            //setup
            ccInfo = new CCInfo("Chris","222,Test","American Express","371449635398431","11/2020","1234");
            long amount = 1000L;
            BankProxy bank = mock(BankProxy.class);
            when(bank.auth(ccInfo,amount)).thenReturn(-2L);
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionDB, "");

            //exercise
            paymentProcessor.processPayment(ccInfo,amount);

        }catch (InsufficientFunds e){
            errorThrown = true;
        }

        //verify
        assertTrue(errorThrown);
    }

    @Test
    public void testInvalidAuthorisationRequest_UnknownError() throws Exception{
        boolean errorThrown = false;
        try{

            //setup
            ccInfo = new CCInfo("Chris","222,Test","American Express","371449635398431","11/2020","1234");
            long amount = 1000L;
            BankProxy bank = mock(BankProxy.class);
            when(bank.auth(ccInfo,amount)).thenReturn(-3L);
            PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionDB,"");

            //exercise
            int result = paymentProcessor.processPayment(ccInfo,amount);

        }catch (UnknownError e){
            errorThrown = true;
        }

        //verify
        assertTrue(errorThrown);
    }

    @Test
    public void testValidAuthorisationRequest() throws  Exception{


        //setup
        ccInfo = new CCInfo("Chris","222,Test","American Express","371449635398431","11/2020","1234");
       // Transaction transaction = new Transaction();
        long amount = 1000L;
        BankProxy bank = mock(BankProxy.class);
        when(bank.auth(ccInfo,1000)).thenReturn(10000L);

        PaymentProcessor paymentProcessor = new PaymentProcessor(bank,transactionDB,"authorise");

        //exercise
        int result = paymentProcessor.processPayment(ccInfo,amount);

        assertEquals(0,result);
    }

}
