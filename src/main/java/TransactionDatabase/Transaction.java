package TransactionDatabase;

import CardInfo.CCInfo;

import java.util.Arrays;
import java.util.List;

public class Transaction {
    private long id;
    private long amount;
    private String state;
    private CCInfo ccInfo;

    public Transaction(long id, long amount, String state, CCInfo ccInfo){
        setId(id);
        setAmount(amount);
        setState(state);
        setCcInfo(ccInfo);

    }

    public long getId()
    {
        return this.id;
    }
    public void setId(long value)
    {
        this.id = value;
    }

    public long getAmount()
    {
        return this.amount;
    }

    public void setAmount(long value)
    {
        this.amount = value;
    }

    public String state()
    {
        return this.state;
    }
    public void setState(String value)
    {
        List<String> validState =  Arrays.asList("capture", "void", "invalid", "authorise", "refund");
        boolean valid = false;
        value =  value.toLowerCase();

        for(String state : validState)
            if(state.contains(value)) {
                this.state = value;
                valid = true;
                break;
            }

        if(!valid)
            this.state = "";
    }

    public CCInfo getCcInfo()
    {
        return this.ccInfo;
    }
    public void setCcInfo(CCInfo value)
    {
        this.ccInfo = value;
    }


}


