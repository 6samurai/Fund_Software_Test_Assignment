package OfflineVerification;
import CardInfo.CCInfo;
import TransactionDatabase.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static  org.junit.Assert.*;


public class OfflineVerificationTests {

    CCInfo ccInfo;
    @Before
    public  void setup(){

    }

    @After
    public  void teardown(){

    }

    public CCInfo createValidCCInfo(){
        return new CCInfo("Chris","222,Test","VISA","371449635398431","11/4/2020","Test");

    }

    @Test
    public void testVerifyOperationLuhnCheck(){
        ccInfo = createValidCCInfo();
        OfflineVerification offlineVerification = new OfflineVerification();
        boolean check = offlineVerification.verifyLuhn(ccInfo.getCardNumber());

        assertTrue(check);
    }

}
