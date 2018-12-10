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

    public boolean verifyLuhn(String cardNumber) {
        try {
            if (cardNumber.length() == 0)
                return false;

            else {
                int value;
                int temp;
                int total = 0;
                for (int i = cardNumber.length() - 1; i >= 0; i--) {

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

        } catch (Exception e) {
            return false;
        }
    }

    public int verifyOffline(CCInfo ccInfo) throws Exception {
        VerifyOffline verifyOffline = new VerifyOffline();
        if (verifyOffline.verifyPrefixAndCardType(ccInfo.getCardNumber(), ccInfo.getCardType())) {
            if (verifyOffline.verifyExpiryDate(ccInfo.getCardExpiryDate())) {
                if (verifyOffline.verifyName(ccInfo.getCustomerName()))
                    if(verifyOffline.verifyAddress(ccInfo.getCustomerAddress()))
                        if(verifyOffline.verifyCVV(ccInfo.getCardCVV(),ccInfo.getCardType()))
                            return 0;
                        else  throw  new UserError("Invalid CVV");
                    else throw new UserError("Missing Address");
                else throw new UserError("Missing Name");
            } else throw new UserError("Expired card");
        } else throw new UserError("Invalid Prefix of card");
    }

    public boolean OfflineVerification(CCInfo ccInfo) throws Exception {
        if (verifyLuhn(ccInfo.getCardNumber())) {
            //verify card details
            int verifyOperation = verifyOffline(ccInfo);
            if (verifyOperation == 0) {
                return true;
            }

        } else throw new UserError("Invalid Card Number");

        return false;
    }

 /*   public int processPayment(CCInfo ccInfo, long amount) {

        Calendar presentDate  = getPresentDate();

        Transaction currentTransaction = new Transaction(-1L, ccInfo, amount, "", presentDate);
        try {
            if (OfflineVerification(ccInfo)) {

                //authentication check
                long bankAction = bank.auth(ccInfo, amount);
                int actionResult = Authorise(bankAction, currentTransaction);

                if (operation == States.AUTHORISE) {
                    return actionResult;
                }

                //since operation is not authorise - the resulting actions are either cpature or refund
                if (actionResult == 0 && currentTransaction.getState().contains(States.AUTHORISE.toString().toLowerCase())) {
                    bankAction = bank.capture(currentTransaction.getId());

                    actionResult = Capture(bankAction, currentTransaction);

                    if (operation == States.CAPTURE) {
                        return actionResult;
                    } else if (operation == States.REFUND && actionResult == 0) {

                        bankAction = bank.refund(currentTransaction.getId(), amount);
                        return Refund(bankAction, currentTransaction);
                    }

                }
            }

            throw new UnknownError();
        } catch (UserError e) {
            setTransactionToInvalid(currentTransaction);
            logs.add(e.getMessage());
            return 1;

        } catch (UnknownError e) {
            setTransactionToInvalid(currentTransaction);
            logs.add(e.getMessage());
            return 2;

        } catch (Exception e) {
            setTransactionToInvalid(currentTransaction);
            logs.add("An error has occurred");
            return 2;
        }
        return 2;
    }*/

    public int processPayment(CCInfo ccInfo, long amount, String state, long transactionID) {

        Calendar presentDate = getPresentDate();

        Transaction currentTransaction = new Transaction(transactionID, ccInfo, amount, "", presentDate);
        try {
            VerifyOffline verifyOffline = new VerifyOffline();
            long bankAction = -1;
            int actionResult = 2;
            //to verify that that card is not expired
            if (verifyOffline.verifyExpiryDate(ccInfo.getCardExpiryDate())) {

                if (state.toLowerCase().contains(States.CAPTURE.toString().toLowerCase())) {

                    bankAction = bank.capture(transactionID);
                    actionResult = Capture(bankAction, transactionID);

                } else if (state.toLowerCase().contains(States.REFUND.toString().toLowerCase())) {

                    bankAction = bank.refund(transactionID, amount);
                    actionResult = Refund(bankAction, amount, transactionID);

                } else {
                    throw new UserError("Invalid operation selected");
                }
            } else throw new UserError("Expired card");
            return actionResult;

        } catch (UserError e) {
            setTransactionToInvalid(currentTransaction);
            logs.add(e.getMessage());
            return 1;

        } catch (UnknownError e) {
            setTransactionToInvalid(currentTransaction);
            logs.add(e.getMessage());
            return 2;

        } catch (Exception e) {
            setTransactionToInvalid(currentTransaction);
            logs.add("An error has occurred");
            return 2;
        }
    }

    public int processPayment(CCInfo ccInfo, long amount, String state) {

        Calendar presentDate = getPresentDate();

        Transaction currentTransaction = new Transaction(-1L, ccInfo, amount, "", presentDate);
        try {

            long bankAction = -1;
            int actionResult = 2;
            if (OfflineVerification(ccInfo)) {

                if (state.toLowerCase().contains(States.AUTHORISE.toString().toLowerCase())) {
                    bankAction = bank.auth(ccInfo, amount);
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

        } catch (Exception e) {
            setTransactionToInvalid(currentTransaction);
            logs.add("An error has occurred");
            return 2;
        }
    }

    private void setTransactionToInvalid(Transaction currentTransaction) {
        if (currentTransaction.getId() != -1) {
            currentTransaction.setState("invalid");
            transactionDB.saveTransaction(currentTransaction);
        }

    }

    private int Authorise(long transactionID, Transaction currentTransaction) throws Exception {
        //maven's default compiler target bytecode version is 1.5 - this version does not support switch statements with strings.
        // Thus for compatibility reasons this is not modified and a sequence of if statements are used instead of a switch(string)
        if (transactionID > 0) {
            currentTransaction.setId(transactionID);
            currentTransaction.setState(States.AUTHORISE.toString());
            transactionDB.saveTransaction(currentTransaction);
            return 0;
        } else if (transactionID == -1) {
            throw new UserError("Credit card details are invalid");
        } else if (transactionID == -2) {
            throw new UserError("Insufficient funds on credit card");
        } else if (transactionID == -3) {
            throw new UnknownError();

        } else throw new UnknownError();
    }

    public int Capture(long bankAction, long transactionID) throws Exception {

        Transaction currentTransaction = transactionDB.getTransaction(transactionID);

        Calendar presentDate = getPresentDate();
        Calendar transactionWeek = currentTransaction.getDate();
        transactionWeek.add(Calendar.WEEK_OF_YEAR, +1);

        //maven's default compiler target bytecode version is 1.5 - this version does not support switch statements with strings.
        // Thus for compatibility reasons this is not modified and a sequence of if statements are used instead of a switch(string)
        if (bankAction == 0 && transactionWeek.compareTo(presentDate) >= 0) {

            if (currentTransaction.getState().contains(States.AUTHORISE.toString().toLowerCase())) {
                currentTransaction.setState(States.CAPTURE.toString());
                transactionDB.saveTransaction(currentTransaction);
                return 0;
            } else    if (currentTransaction.getState().contains(States.CAPTURE.toString().toLowerCase()) ||currentTransaction.getState().contains(States.REFUND.toString().toLowerCase()) )   {

                throw new UserError("Transaction already processed");
            } else{
                throw new UserError("Transaction does not exist");
            }


        } else if (bankAction == -1) {

            throw new UserError("Transaction does not exist");

        } else if (bankAction == -2) {

            throw new UserError("Transaction has already been captured");

        } else if (bankAction == -3 || transactionWeek.compareTo(presentDate) < 0) {

            currentTransaction.setState(States.VOID.toString());
            transactionDB.saveTransaction(currentTransaction);
            return 0;
        } else if (bankAction == -4) {
            throw new UnknownError();

        } else throw new UnknownError();
    }

    public int Refund(long bankAction, long amount, long transactionID) throws Exception {
        Transaction currentTransaction = transactionDB.getTransaction(transactionID);

        Calendar presentDate = getPresentDate();
        Calendar monthRefund = currentTransaction.getDate();
        monthRefund.add(Calendar.MONTH, +1);
        //maven's default compiler target bytecode version is 1.5 - this version does not support switch statements with strings.
        // Thus for compatibility reasons this is not modified and a sequence of if statements are used instead of a switch(string)
        if (bankAction == 0 && monthRefund.compareTo(presentDate) >= 0) {

            if (currentTransaction.getState().contains(States.CAPTURE.toString().toLowerCase()) && currentTransaction.getAmount() == amount) {
                currentTransaction.setState(States.REFUND.toString());
                transactionDB.saveTransaction(currentTransaction);
                return 0;
            } else if (currentTransaction.getState().contains(States.AUTHORISE.toString().toLowerCase()) ) {
                throw new UserError("Refund is not captured");
            } else if (currentTransaction.getState().contains(States.REFUND.toString().toLowerCase()) ) {
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

    private Calendar getPresentDate() {
        Calendar presentWeek = Calendar.getInstance();
        presentWeek.set(Calendar.HOUR_OF_DAY, 0);
        presentWeek.set(Calendar.MINUTE, 0);
        presentWeek.set(Calendar.SECOND, 0);
        return presentWeek;
    }
}

