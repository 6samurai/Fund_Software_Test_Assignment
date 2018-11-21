package PaymentProcessor;

import Bank.BankProxy;
import CardInfo.CCInfo;
import PaymentProcessor.Enums.BankOperations;
import PaymentProcessor.ErrorMessages.UnknownError;
import PaymentProcessor.ErrorMessages.UserError;
import TransactionDatabase.Transaction;
import TransactionDatabase.TransactionDatabase;
import VerifyOffline.VerifyOffline;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.getNumericValue;

public class PaymentProcessor {

    BankProxy bank;
    TransactionDatabase transactionDB;
    BankOperations operation;
    List<String> logs = new ArrayList<String>();
    // Long transactionID;

    public PaymentProcessor(BankProxy bank, TransactionDatabase transactionDB, BankOperations operation, List<String> logs) {
        this.bank = bank;
        this.transactionDB = transactionDB;
        this.operation = operation;
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
                if (verifyOffline.verifyInfoPresent(ccInfo))
                    return 0;
                else throw new UserError("Missing card Information");
            } else throw new UserError("Card is expired");
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

    public int processPayment(CCInfo ccInfo, long amount) {
        Transaction currentTransaction = new Transaction(-1L, ccInfo, amount, "");
        try {
            if (OfflineVerification(ccInfo)) {

                // currentTransaction.setState(BankOperations.OFFLINE_VER.toString());
                //  transactionDB.saveTransaction(currentTransaction);

                //authentication check
                long bankAction = bank.auth(ccInfo, amount);
                int actionResult = Authorise(bankAction, currentTransaction);

                if (operation == BankOperations.AUTHORISE) {
                    return actionResult;
                }

                if (actionResult == 0 && currentTransaction.getState().contains(BankOperations.AUTHORISE.toString().toLowerCase())) {
                    bankAction = bank.capture(currentTransaction.getId());

                    actionResult = Capture(bankAction, currentTransaction);

                    if (operation == BankOperations.CAPTURE) {
                        return actionResult;
                    } else if (operation == BankOperations.REFUND && actionResult == 0) {

                        bankAction = bank.refund(currentTransaction.getId(), amount);
                        return Refund(bankAction, currentTransaction);
                    }

                }
            }

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
    }

    private void setTransactionToInvalid(Transaction currentTransaction) {
        if (currentTransaction.getId() != -1) {
            currentTransaction.setState("invalid");
            transactionDB.saveTransaction(currentTransaction);
        }

    }

    private int Authorise(long bankAction, Transaction currentTransaction) throws Exception {

        if (bankAction > 0) {
            currentTransaction.setId(bankAction);
            currentTransaction.setState(BankOperations.AUTHORISE.toString());
            transactionDB.saveTransaction(currentTransaction);
            return 0;
        } else if (bankAction == -1) {
            throw new UserError("Credit card details are invalid");
        } else if (bankAction == -2) {
            throw new UserError("Insufficient funds on credit card");
        } else if (bankAction == -3) {
            throw new UnknownError();

        } else throw new UnknownError();
    }

    public int Capture(long bankAction, Transaction currentTransaction) throws Exception {

        if (bankAction == 0) {

            if (currentTransaction.getState().contains("authorise")) {
                currentTransaction.setState(BankOperations.CAPTURE.toString());
                transactionDB.saveTransaction(currentTransaction);
                return 0;
            } else {
                throw new UserError("Transaction does not exist");
            }


        } else if (bankAction == -1) {

            throw new UserError("Transaction does not exist");

        } else if (bankAction == -2) {

            throw new UserError("Transaction has already been captured");

        } else if (bankAction == -3) {

            currentTransaction.setState(BankOperations.VOID.toString());
            transactionDB.saveTransaction(currentTransaction);
            return 0;
        } else if (bankAction == -4) {
            throw new UnknownError();

        } else throw new UnknownError();
    }

    public int Refund(long bankAction, Transaction currentTransaction) throws Exception {

        if (bankAction == 0) {
            Transaction prevTransaction = transactionDB.getTransaction(currentTransaction.getId());
            if (prevTransaction.getState().contains("capture") && prevTransaction.getAmount() >= currentTransaction.getAmount()) {
                currentTransaction.setState(BankOperations.REFUND.toString());
                transactionDB.saveTransaction(currentTransaction);
                return 0;
            } else throw new UserError("Refund is greater than amount captured");

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
}

