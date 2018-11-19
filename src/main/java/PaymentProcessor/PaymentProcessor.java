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
    Long transactionID;

    public PaymentProcessor(BankProxy bank, Long transactionID, TransactionDatabase transactionDB, BankOperations operation, List<String> logs) {
        this.bank = bank;
        this.transactionDB = transactionDB;
        this.operation = operation;
        this.logs = logs;
        this.transactionID = transactionID;
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

        try {
            Transaction currentTransaction = new Transaction(transactionID, ccInfo, amount, "");

            if (OfflineVerification(ccInfo)) {

                //authentication check
                long bankAction = bank.auth(ccInfo, amount);
                int actionResult = Authorise(bankAction);

                if (operation == BankOperations.AUTHORISE) {
                    return actionResult;
                }

                if (actionResult == 0) {

                    if (operation == BankOperations.CAPTURE) {

                        bankAction = bank.capture(transactionID);
                        return Capture(bankAction, currentTransaction);

                    } else if (operation == BankOperations.REFUND) {

                        bankAction = bank.refund(transactionID, amount);
                        return Refund(bankAction, currentTransaction);
                    }
                }
            }

        } catch (UserError e) {
            logs.add(e.getMessage());
            return 1;

        } catch (UnknownError e) {
            logs.add(e.getMessage());
            return 2;

        } catch (Exception e) {
            logs.add("An error has occurred");
            return 2;
        }
        return 2;
    }

    private int Authorise(long bankAction) throws Exception {

        if (bankAction > 0) {
            return 0;
        } else if (bankAction == -1) {
            throw new UserError("Credit card details are invalid");
        } else if (bankAction == -2) {
            throw new UserError("Insufficient funds on credit card");
        } else if (bankAction == -3) {
            throw new UnknownError();

        }
        throw new UnknownError();
    }

    public int Capture(long bankAction, Transaction currentTransaction) throws Exception {


        if (bankAction == 0) {
            currentTransaction.setState(operation.toString());
            transactionDB.saveTransaction(currentTransaction);
            return 0;

        } else if (bankAction == -1) {

            throw new UserError("Transaction does not exist");

        } else if (bankAction == -2) {

            throw new UserError("Transaction has already been captured");

        } else if (bankAction == -3) {

            currentTransaction.setState(BankOperations.VOID.toString());
            transactionDB.saveTransaction(currentTransaction);
            throw new UserError("Transaction has been voided");

        } else if (bankAction == -4) {
            throw new UnknownError();

        }
        throw new UnknownError();
    }

    public int Refund(long bankAction, Transaction currentTransaction) throws Exception {


        if (bankAction == 0) {

            currentTransaction.setState(operation.toString());
            transactionDB.saveTransaction(currentTransaction);
            return 0;

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
}

