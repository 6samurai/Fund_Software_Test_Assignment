package PaymentProcessor;

import Bank.BankProxy;
import CardInfo.CCInfo;
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

    //carries out all of the card detail verifications
    public int verifyOffline(CCInfo ccInfo) throws Exception {
        VerifyOffline verifyOffline = new VerifyOffline();
        if (verifyOffline.verifyPrefix_CardType_CardLength(ccInfo.getCardNumber(), ccInfo.getCardType())) {
            if (verifyOffline.verifyExpiryDate(ccInfo.getCardExpiryDate())) {
                if (verifyOffline.verifyName(ccInfo.getCustomerName()))
                    if (verifyOffline.verifyAddress(ccInfo.getCustomerAddress()))
                        if (verifyOffline.verifyCVV(ccInfo.getCardCVV(), ccInfo.getCardType()))
                            return 0;
                        else throw new UserError("Invalid CVV");
                    else throw new UserError("Missing Address");
                else throw new UserError("Missing Name");
            } else throw new UserError("Expired card");
        } else throw new UserError("Invalid Prefix of card");
    }

    //verifies that all of the details provided are valid
    public boolean OfflineVerification(CCInfo ccInfo) throws Exception {
        if (verifyLuhn(ccInfo.getCardNumber())) {
            //verify card details
            int verifyOperation = verifyOffline(ccInfo);
            if (verifyOperation == 0) {
                return true;
            }
        }
        throw new UserError("Invalid Card Number");
    }

    //carries out the process payment operations for an Authorise request
    public int processPayment(CCInfo ccInfo, long amount, String state) {

        Transaction currentTransaction = new Transaction(transactionDB.countTransactions(), -1L, ccInfo, amount, "", getPresentDate());
        try {

            long bankAction = -1;
            int actionResult = 2;
            //card details check
            if (OfflineVerification(ccInfo)) {
                if (state.toLowerCase().contains(States.AUTHORISED.toString().toLowerCase())) {
                    //receive result from bank method
                    bankAction = bank.auth(ccInfo, amount);
                    //carry out operation to system based on bank results
                    actionResult = Authorise(bankAction, currentTransaction);

                } else {
                    throw new UserError("Invalid operation selected");
                }
            }
            return actionResult;

        } catch (UserError e) {
            setTransactionToInvalid(currentTransaction);
            logs.add(e.getMessage());
            return 1;

        } catch (UnknownError e) {
            setTransactionToInvalid(currentTransaction);
            logs.add(e.getMessage());
            return 2;

        }
        //this exception is set up to handle the case when no transaction is obtained or an unknown error has occurred with the bank method
        catch (Exception e) {
            setTransactionToInvalid(currentTransaction);
            logs.add("An error has occurred");
            return 2;
        }
    }

    //carries out the process payment operations for a Capture or Refund request
    public int processPayment(CCInfo ccInfo, long amount, String state, long transactionID) {

        Transaction currentTransaction = new Transaction(transactionDB.countTransactions(), transactionID, ccInfo, amount, "", getPresentDate());
        try {

            long bankAction = -1;
            int actionResult = 2;
            //card details check
            if (OfflineVerification(ccInfo)) {

                if (state.toLowerCase().contains(States.CAPTURED.toString().toLowerCase())) {
                    //receive result from bank method
                    bankAction = bank.capture(transactionID);
                    //carry out operation to system based on bank results
                    actionResult = Capture(bankAction, transactionID);

                } else if (state.toLowerCase().contains(States.REFUNDED.toString().toLowerCase())) {
                    //receive result from bank method
                    bankAction = bank.refund(transactionID, amount);
                    //carry out operation to system based on bank results
                    actionResult = Refund(bankAction, amount, transactionID);

                } else {
                    throw new UserError("Invalid operation selected");
                }
            }
            return actionResult;

        } catch (UserError e) {
            setTransactionToInvalid(currentTransaction);
            logs.add(e.getMessage());
            return 1;

        } catch (UnknownError e) {
            setTransactionToInvalid(currentTransaction);
            logs.add(e.getMessage());
            return 2;

        }
        //this exception is set up to handle the case when no transaction is obtained or an unknown error has occurred with the bank method
        catch (Exception e) {
            setTransactionToInvalid(currentTransaction);
            logs.add("An error has occurred");
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
    private int Authorise(long transactionID, Transaction currentTransaction) throws Exception {
        //maven's default compiler target bytecode version is 1.5 - this version does not support switch statements with strings.
        //Thus for compatibility reasons this is not modified and a sequence of if statements are used instead of a switch(string)

        //check if a valid transaction ID was provided from bank
        if (transactionID > 0) {
            //save current transaction to transaction DB with updated info
            currentTransaction.setId(transactionDB.countTransactions());
            currentTransaction.setState(States.AUTHORISED.toString());
            currentTransaction.setTransactionId(transactionID);
            transactionDB.saveTransaction(currentTransaction);
            return 0;
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
    public int Capture(long bankAction, long transactionID) throws Exception {

        //get latest transaction with the specified transaction ID
        Transaction currentTransaction = transactionDB.getTransactionByTransactionID(transactionID);

        //get current date
        Calendar presentDate = getPresentDate();

        //get date 1 week ahead of transaction retrieved
        Calendar transactionWeek = currentTransaction.getDate();
        transactionWeek.add(Calendar.WEEK_OF_YEAR, +1);

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

                throw new UserError("Transaction already processed");
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
    public int Refund(long bankAction, long amount, long transactionID) throws Exception {
        //get latest transaction with the specified transaction ID
        Transaction currentTransaction = transactionDB.getTransactionByTransactionID(transactionID);

        //get current date
        Calendar presentDate = getPresentDate();

        //get date 1 month ahead of transaction retrieved
        Calendar monthRefund = currentTransaction.getDate();
        monthRefund.add(Calendar.MONTH, +1);

        //maven's default compiler target bytecode version is 1.5 - this version does not support switch statements with strings.
        //Thus for compatibility reasons this is not modified and a sequence of if statements are used instead of a switch(string)
        if (bankAction == 0 && monthRefund.compareTo(presentDate) >= 0) {

            //verify that specified transaction is Captured and the amount specified is equal to the value recorded in DB
            if (currentTransaction.getState().contains(States.CAPTURED.toString().toLowerCase()) && currentTransaction.getAmount() == amount) {

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

            } else
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

        } else throw new UnknownError();
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

