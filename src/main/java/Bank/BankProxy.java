package Bank;

import CardInfo.CCInfo;

public interface BankProxy {
    long    auth(CCInfo ccInfo, long value);
    int     capture(long value);
    int     refund(long value, long value2);
}
