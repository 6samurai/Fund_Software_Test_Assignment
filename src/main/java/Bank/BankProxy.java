package Bank;

import CardInfo.CCInfo;

public interface BankProxy {
    long    auth(CCInfo ccInfo, long value);
    int     capture(long id);
    int     refund(long id, long value);
}
