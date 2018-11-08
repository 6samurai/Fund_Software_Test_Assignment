package CardInfo;

public class CCInfo {
    private String customerName;
    private String customerAddress;
    private String cardType;
    private String cardNumber;
    private String cardExpiryDate;
    private String cardCVV;


    public CCInfo(String customerName, String customerAddress, String cardType, String cardNumber, String cardExpiryDate, String cardCVV){

        setCustomerName(customerName);
        setCustomerAddress(customerAddress);
        setCardType(cardType);
        setCardNumber(cardNumber);
        setCardExpiryDate(cardExpiryDate);
        setCardCVV(cardCVV);
    }

    public String getCustomerName()
    {
        return this.customerName;
    }
    public void setCustomerName(String value)
    {
        this.customerName = value;
    }

    public String getCustomerAddress()
    {
        return this.customerAddress;
    }
    public void setCustomerAddress(String value)
    {
        this.customerAddress = value;
    }

    public String getCardType()
    {
        return this.cardType;
    }
    public void setCardType(String value)
    {
        this.cardType = value;
    }

    public String getCardNumber()
    {
        return this.cardNumber;
    }
    public void setCardNumber(String value)
    {
        this.cardNumber = value;
    }

    public String getCardExpiryDate()
    {
        return this.cardExpiryDate;
    }
    public void setCardExpiryDate(String value)
    {
        this.cardExpiryDate = value;
    }

    public String getCardCVV()
    {
        return this.cardCVV;
    }
    public void setCardCVV(String value)
    {
        this.cardCVV = value;
    }

}
