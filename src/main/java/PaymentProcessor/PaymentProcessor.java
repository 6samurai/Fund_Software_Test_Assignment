package PaymentProcessor;

import Bank.BankProxy;
import CardInfo.CCInfo;
import PaymentProcessor.ErrorMessages.BankError;
import PaymentProcessor.ErrorMessages.DatabaseError;
import PaymentProcessor.ErrorMessages.UnknownError;
import PaymentProcessor.ErrorMessages.UserError;
import TransactionDatabase.Transaction;
import TransactionDatabase.TransactionDatabase;
import TransactionDatabase.enums.States;
import VerifyOffline.VerifyOffline;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Character.getNumericValue;

public class PaymentProcessor {

    BankProxy bank;
    TransactionDatabase transactionDB;
    List<String> logs = new ArrayList<String>();


    public PaymentProcessor(BankProxy bank, TransactionDatabase transactionDB, List<String> logs) {
        this.bank = bank;
        this.transactionDB = transactionDB;

        this.logs = logs;
    }

    public PaymentProcessor() {
    }

    //checks if the inputted card number string satisfies the Luhn algorithm
    public boolean verifyLuhn(String cardNumber) {

        if (cardNumber.length() == 0)
            return false;

        else {
            int value;
            int temp;
            int total = 0;
            for (int i = cardNumber.length() - 1; i >= 0; i--) {
                //checks if the current character in the string is a numeric n
                if (!Character.isDigit(cardNumber.charAt(i))) return false;

                value = getNumericValue(cardNumber.charAt(i));
                if ((cardNumber.length() - i) % 2 == 0) {
                    temp = value * 2;
                    if (temp > 9) {
                        value = temp - 9;
                    } else value = temp;
                }
                total = total + value;
            }
            if (total % 10 == 0)
                return true;
        }
        return false;
    }

    //verifies that all of the details provided are valid
    public boolean offlineVerification(CCInfo ccInfo) throws Exception {
        if (verifyLuhn(ccInfo.getCardNumber())) {
            //verify card details
            VerifyOffline verifyOffline = new VerifyOffline();
            if (verifyOffline.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType())) {
                if (verifyOffline.verifyExpiryDate(ccInfo.getCardExpiryDate())) {
                    if (verifyOffline.verifyName(ccInfo.getCustomerName())) {
                        if (verifyOffline.verifyAddress(ccInfo.getCustomerAddress())) {
                            if (verifyOffline.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType())) {
                                return true;
                            } else throw new UserError("Invalid CVV");
                        } else throw new UserError("Missing Address");
                    } else throw new UserError("Missing Name");
                } else throw new UserError("Expired card");
            } else throw new UserError("Invalid Prefix of card");
        }
        throw new UserError("Invalid Card Number");
    }

    //carries out the process payment operations for Authorise request
    public int processPayment(CCInfo ccInfo, long amount) {

        Transaction currentTransaction = null;
        try {

            int result = 2;

            if (transactionDB == null)
                throw new DatabaseError();

            if (bank == null)
                throw new BankError();
            currentTransaction = new Transaction(0, -1L, ccInfo, amount, "", getPresentDate());
            //card details check
            if (offlineVerification(ccInfo)) {

                //carry out operation to system based on bank results
                result = authorise(currentTransaction);

            }
            return result;

        } catch (DatabaseError e) {
            logs.add("Database is offline");
            return 2;
        } catch (BankError e) {
            logs.add("Bank is offline");
            return 2;
        } catch (UserError e) {
            setTransactionToInvalid(currentTransaction);
            logs.add(e.getMessage());
            return 1;
        } catch (UnknownError e) {
            setTransactionToInvalid(currentTransaction);
            logs.add(e.getMessage());
            return 2;
        } catch (Exception e) {
            logs.add("An unexpected error has occurred");
            return 2;
        }
    }

    //carries out the process payment operations for Capture request
    public int processPayment(long transactionID) {

        Transaction currentTransaction = null;
        try {

            int result = 2;

            if (transactionDB == null)
                throw new DatabaseError();

            if (bank == null)
                throw new BankError();

            //get latest transaction with the specified transaction ID
            currentTransaction = transactionDB.getTransactionByTransactionID(transactionID);
            //card details check
            if (offlineVerification(currentTransaction.getCcInfo())) {
                //carry out operation to system based on bank results
                result = capture(currentTransaction);
            }
            return result;

        } catch (DatabaseError e) {
            logs.add("Database is offline");
            return 2;
        } catch (BankError e) {
            logs.add("Bank is offline");
            return 2;
        } catch (UserError e) {
            setTransactionToInvalid(currentTransaction);
            logs.add(e.getMessage());
            return 1;
        } catch (UnknownError e) {
            setTransactionToInvalid(currentTransaction);
            logs.add(e.getMessage());
            return 2;
        } catch (Exception e) {
            logs.add("An unexpected error has occurred");
            return 2;
        }
    }

    //carries out the process payment operations for Refund request
    public int processPayment(long amount, long transactionID) {

        //get latest transaction with the specified transaction ID
        Transaction currentTransaction = null;

        try {

            if (transactionDB == null)
                throw new DatabaseError();

            if (bank == null)
                throw new BankError();

            int result = 2;
            //get latest transaction with the specified transaction ID
            currentTransaction = transactionDB.getTransactionByTransactionID(transactionID);
            //card details check
            if (offlineVerification(currentTransaction.getCcInfo())) {

                //carry out operation to system based on bank results
                result = refund(amount, currentTransaction);

            }
            return result;

        } catch (DatabaseError e) {
            logs.add("Database is offline");
            return 2;
        } catch (BankError e) {
            logs.add("Bank is offline");
            return 2;
        } catch (UserError e) {
            setTransactionToInvalid(currentTransaction);
            logs.add(e.getMessage());
            return 1;
        } catch (UnknownError e) {
            setTransactionToInvalid(currentTransaction);
            logs.add(e.getMessage());
            return 2;
        } catch (Exception e) {
            logs.add("An unexpected error has occurred");
            return 2;
        }
    }

    //transaction is set to Invalid state
    private void setTransactionToInvalid(Transaction currentTransaction) {
        if (currentTransaction.getId() != -1) {

            currentTransaction.setState("invalid");
            currentTransaction.setId(transactionDB.countTransactions());

            transactionDB.saveTransaction(currentTransaction);
        }
    }

    //carries out Authorise operations on system
    public int authorise(Transaction currentTransaction) throws Exception {
        //maven's default compiler target bytecode version is 1.5 - this version does not support switch statements with strings.
        //Thus for compatibility reasons this is not modified and a sequence of if statements are used instead of a switch(string)
        long transactionID = bank.auth(currentTransaction.getCcInfo(), currentTransaction.getAmount());
        Transaction checkTransaction = transactionDB.getTransactionByTransactionID(transactionID);


        //check if a valid transaction ID was provided from bank
        if (transactionID > 0) {
            if(currentTransaction.getAmount()>0){
                if (checkTransaction == null) {
                    //save current transaction to transaction DB with updated info
                    currentTransaction.setId(transactionDB.countTransactions());
                    currentTransaction.setState(States.AUTHORISED.toString());
                    currentTransaction.setTransactionId(transactionID);
                    transactionDB.saveTransaction(currentTransaction);
                    return 0;
                } else {
                    throw new UserError("Transaction has already been authorised");
                }

            } else
                throw new UserError("Invalid amount");


        } else if (transactionID == -1) {
            throw new UserError("Credit card details are invalid");
        } else if (transactionID == -2) {
            throw new UserError("Insufficient funds on credit card");
        } else if (transactionID == -3) {
            throw new UnknownError();
        }
        //in the event where the bank system returns an unknown transaction value
        else throw new UnknownError();
    }

    //carries out capture operation on system
    public int capture(Transaction currentTransaction) throws Exception {

        //get current date
        Calendar presentDate = getPresentDate();

        //get date 1 week ahead of transaction retrieved
        Calendar transactionWeek = currentTransaction.getDate();
        transactionWeek.add(Calendar.WEEK_OF_YEAR, +1);

        int bankAction = bank.capture(currentTransaction.getTransactionId());

        //maven's default compiler target bytecode version is 1.5 - this version does not support switch statements with strings.
        //Thus for compatibility reasons this is not modified and a sequence of if statements are used instead of a switch(string)
        if (bankAction == 0 && transactionWeek.compareTo(presentDate) >= 0) {

            //verify that specified transaction is Authorised
            if (currentTransaction.getState().contains(States.AUTHORISED.toString().toLowerCase())) {

                //save new transaction with Captured state
                Transaction newTran = new Transaction(transactionDB.countTransactions(), currentTransaction.getTransactionId(), currentTransaction.getCcInfo(),
                        currentTransaction.getAmount(), States.CAPTURED.toString(), presentDate);
                transactionDB.saveTransaction(newTran);

                return 0;
            }
            //in the even where the transaction was already Captured or Refunded
            else if (currentTransaction.getState().contains(States.CAPTURED.toString().toLowerCase()) || currentTransaction.getState().contains(States.REFUNDED.toString().toLowerCase())) {

                throw new UserError("Transaction already captured");
            } else {
                throw new UserError("Transaction does not exist");
            }

        } else if (bankAction == -1) {

            throw new UserError("Transaction does not exist");

        } else if (bankAction == -2) {

            throw new UserError("Transaction has already been captured");

        }
        //For the case when the transaction has occurred more than a week before or bank system result
        else if (bankAction == -3 || transactionWeek.compareTo(presentDate) < 0) {

            //set and save new transaction to Void State
            Transaction newTran = new Transaction(transactionDB.countTransactions(), currentTransaction.getTransactionId(), currentTransaction.getCcInfo(),
                    currentTransaction.getAmount(), States.VOID.toString(), presentDate);
            transactionDB.saveTransaction(newTran);

            return 1;
        } else if (bankAction == -4) {
            throw new UnknownError();

        } else throw new UnknownError();
    }

    //carries out refund operation on system
    public int refund(long amount, Transaction currentTransaction) throws Exception {

        //get current date
        Calendar presentDate = getPresentDate();

        //get date 1 month ahead of transaction retrieved
        Calendar monthRefund = currentTransaction.getDate();
        monthRefund.add(Calendar.DAY_OF_MONTH, +30);

        int bankAction = bank.refund(currentTransaction.getTransactionId(), amount);

        //maven's default compiler target bytecode version is 1.5 - this version does not support switch statements with strings.
        //Thus for compatibility reasons this is not modified and a sequence of if statements are used instead of a switch(string)
        if (bankAction == 0 && monthRefund.compareTo(presentDate) >= 0) {

            //verify that specified transaction is Captured and the amount specified is equal to the value recorded in DB
            if (currentTransaction.getState().contains(States.CAPTURED.toString().toLowerCase()) && currentTransaction.getAmount() >= amount) {

                //save new transaction with Refunded state
                Transaction newTran = new Transaction(transactionDB.countTransactions(), currentTransaction.getTransactionId(), currentTransaction.getCcInfo(),
                        currentTransaction.getAmount(), States.REFUNDED.toString(), presentDate);
                transactionDB.saveTransaction(newTran);
                return 0;

            }
            //in the even where the transaction was in the Authorised state
            else if (currentTransaction.getState().contains(States.AUTHORISED.toString().toLowerCase())) {
                throw new UserError("Refund is not captured");
            }
            //in the even where the transaction was already Refunded
            else if (currentTransaction.getState().contains(States.REFUNDED.toString().toLowerCase())) {
                throw new UserError("Transaction already refunded");

            } else if(currentTransaction.getAmount() < amount)
                throw new UserError("Refund is greater than amount captured");

        } else if (bankAction == -1) {

            throw new UserError("Transaction does not exist");

        } else if (bankAction == -2) {

            throw new UserError("Transaction has not been captured");

        } else if (bankAction == -3) {

            throw new UserError("Transaction has already been refunded");

        } else if (bankAction == -4) {

            throw new UserError("Refund is greater than amount captured");

        } else if (bankAction == -5) {

            throw new UnknownError();

        }
        throw new UnknownError();
    }

    //get current date
    private Calendar getPresentDate() {
        Calendar presentWeek = Calendar.getInstance();
        //sets time values to 0
        presentWeek.set(Calendar.HOUR_OF_DAY, 0);
        presentWeek.set(Calendar.MINUTE, 0);
        presentWeek.set(Calendar.SECOND, 0);
        return presentWeek;
    }
}

