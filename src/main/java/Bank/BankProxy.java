package Bank;

import CardInfo.CCInfo;

public interface BankProxy {
    long    auth(CCInfo ccInfo, long amount);
    int     capture(long transactionID);
    int     refund(long transactionID, long amount);
}
