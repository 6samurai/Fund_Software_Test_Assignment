package PaymentProcessor.OfflineVerification;
import CardInfo.CCInfo;
import PaymentProcessor.PaymentProcessor;
import PaymentProcessor.enums.TestCardTypes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OfflineVerificationTests {


    CCInfo ccInfo;
    List<String> logs;


    @Before
    public void setup() {
        logs = new ArrayList<String>();
   }

    @After
    public void teardown() {
        ccInfo =null;
        logs.clear();
    }

    @Test
    public void testValid_OfflineVerification_AmericanExpress() {
        boolean errorThrown = false;
        boolean check = false;
        try{
            ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.AMERICAN_EXPRESS.toString(), "371449635398431", "11/2020", "1234");
            PaymentProcessor paymentProcessor = new PaymentProcessor(logs);
            check = paymentProcessor.OfflineVerification(ccInfo);
            assertEquals(0,logs.size());

        }catch (Exception  e){
            errorThrown = true;
        }

        assertTrue(check);

    }


    @Test
    public void testValid_OfflineVerification_Visa() {
        boolean errorThrown = false;
        boolean check = false;
        try{
            ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2020", "123");
            PaymentProcessor paymentProcessor = new PaymentProcessor(logs);
            check = paymentProcessor.OfflineVerification(ccInfo);
            assertEquals(0,logs.size());


        }catch (Exception  e){
            errorThrown = true;
        }
        assertTrue(check);
    }


    @Test
    public void testValid_OfflineVerification_Mastercard() {
        boolean errorThrown = false;
        boolean check = false;
        try{
            ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.MASTERCARD.toString(), "5105105105105100", "11/2020", "124");
            PaymentProcessor paymentProcessor = new PaymentProcessor(logs);
            check = paymentProcessor.OfflineVerification(ccInfo);
            assertEquals(0,logs.size());

        }catch (Exception  e){
            errorThrown = true;
        }
        assertTrue(check);
    }

    @Test
    public void testInvalid_OfflineVerification_CardType_AmericanExpress__CardNumber_Visa() {
        boolean errorThrown = false;
        boolean check = false;
        String errorMessage = "";
        try{
            ccInfo = new CCInfo("Chris", "222,Test", "American Express", "4111111111111111", "11/2020", "1234");
            PaymentProcessor paymentProcessor = new PaymentProcessor(logs);
            check = paymentProcessor.OfflineVerification(ccInfo);

        }catch (Exception  e){
            errorThrown = true;
            errorMessage = e.getMessage();
        }
        assertFalse(check);
        assertTrue(errorThrown);
        assertEquals("Invalid Prefix of card",errorMessage);

    }


    @Test
    public void testInvalid_OfflineVerification_ExpiredCard() {
        boolean errorThrown = false;
        boolean check = false;
        String errorMessage = "";
        try{
            ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2000", "1234");
            PaymentProcessor paymentProcessor = new PaymentProcessor(logs);
            check = paymentProcessor.OfflineVerification(ccInfo);
            assertEquals(1,logs.size());
            assertTrue(logs.contains("An unknown error has occurred"));

        }catch (Exception  e){
            errorThrown = true;
            errorMessage = e.getMessage();
        }
        assertFalse(check);
        assertTrue(errorThrown);
        assertEquals("Card is expired",errorMessage);

    }




    @Test
    public void testInvalid_OfflineVerification_MissingInformation() {
        boolean errorThrown = false;
        boolean check = false;
        String errorMessage = "";
        try{
            ccInfo = new CCInfo("Chris", "222,Test", TestCardTypes.VISA.toString(), "4111111111111111", "11/2030", "");
            PaymentProcessor paymentProcessor = new PaymentProcessor(logs);
            check = paymentProcessor.OfflineVerification(ccInfo);

        }catch (Exception  e){
            errorThrown = true;
            errorMessage = e.getMessage();
        }
        assertFalse(check);
        assertTrue(errorThrown);
        assertEquals("Missing card Information",errorMessage);

    }
}
