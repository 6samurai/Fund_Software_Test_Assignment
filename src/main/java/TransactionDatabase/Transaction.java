package TransactionDatabase;

import CardInfo.CCInfo;
import TransactionDatabase.enums.States;

import java.util.Calendar;

public class Transaction {
    private long id;
    private long transactionId;
    private long amount;
    private String state;
    private CCInfo ccInfo;
    private Calendar date;

    public Transaction(long id, long transactionId, CCInfo ccInfo, long amount, String state, Calendar date) {
        setId(id);
        setTransactionId(transactionId);
        setAmount(amount);
        setState(state);
        setCcInfo(ccInfo);
        setDate(date);

    }

    public Long getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(long value) {
        this.transactionId = value;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long value) {
        this.id = value;
    }

    public long getAmount() {
        return this.amount;
    }

    public void setAmount(long value) {
        this.amount = value;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String value) {
        boolean valid = false;
        value = value.toLowerCase();

        for (States state : States.values())
            if (state.toString().toLowerCase().contains(value)) {
                this.state = value;
                valid = true;
                break;
            }

        if (!valid)
            this.state = "";
    }

    public CCInfo getCcInfo() {
        return this.ccInfo;
    }

    public void setCcInfo(CCInfo value) {
        this.ccInfo = value;
    }

    public Calendar getDate() {
        return this.date;
    }

    public void setDate(Calendar value) {
        value.set(Calendar.HOUR_OF_DAY, 0);
        value.set(Calendar.MINUTE, 0);
        value.set(Calendar.SECOND, 0);

        this.date = value;
    }

}


