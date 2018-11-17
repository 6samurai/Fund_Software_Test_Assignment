package PaymentProcessor.LuhnTests;
import CardInfo.CCInfo;
import PaymentProcessor.PaymentProcessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LuhnTests {

    CCInfo ccInfo;

    @Before
    public void setup() {
    }

    @After
    public void teardown() {
        ccInfo = null;
    }


    @Test
    public void testVerifyOperation_LuhnCheck() {
        ccInfo = new CCInfo("Chris", "222,Test", "American Express", "371449635398431", "11/2020", "1234");
        PaymentProcessor paymentProcessor = new PaymentProcessor();
        boolean check = paymentProcessor.verifyLuhn(ccInfo.getCardNumber());
        assertTrue(check);

    }

    @Test
    public void testVerifyOperation_LuhnCheck_Blank() {
        ccInfo = new CCInfo("Chris", "222,Test", "American Express", "", "11/2020", "1234");
        PaymentProcessor paymentProcessor = new PaymentProcessor();
        boolean check = paymentProcessor.verifyLuhn(ccInfo.getCardNumber());
        assertFalse(check);
    }

    @Test
    public void testVerifyOperation_LuhnCheck_InvalidValue() {
        ccInfo = new CCInfo("Chris", "222,Test", "American Express", "2132132131312321", "11/2020", "1234");
        PaymentProcessor paymentProcessor = new PaymentProcessor();
        boolean check = paymentProcessor.verifyLuhn(ccInfo.getCardNumber());
        assertFalse(check);
    }


}