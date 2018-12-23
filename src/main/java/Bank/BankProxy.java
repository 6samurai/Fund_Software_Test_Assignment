package Bank;

import CardInfo.CCInfo;

public interface BankProxy {

    //Bank authorise method
    long auth(CCInfo ccInfo, long amount);
    //Bank capture method
    int capture(long transactionID);
    //Bank refund method
    int refund(long transactionID, long amount);
}
