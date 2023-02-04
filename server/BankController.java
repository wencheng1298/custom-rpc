package server;

import java.util.*;

import bank.*;
import bank.Currency;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.Instant;

import common.request.*;
import common.response.*;

import common.communication.Marshallable;
import common.communication.Message;
import common.communication.MessageHeader;
import common.constants.Opcode;
import common.constants.Status;

/**
 * contains all the service handler functions to process the different kinds of service requests.
 * Each of the process function takes in a request body and returns the response message
 */
public class BankController {
    /**
     * initialise a bank that stores all the accounts created at runtime 
     */
    private Bank bank = new Bank();
    private ServerSkeleton skeleton;

    // used for monitoring, stores the clients who have subscribed to monitor
    private Map<String, Instant> subscribers = new HashMap<>();

    public BankController(ServerSkeleton skeleton) {
        this.skeleton = skeleton;
    }

    /**
     * 
     * format of a transation log to be stored, which includes the time stamp of log creation
     */
    private String formatLog(String log) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.toString().substring(0, 16) + ":" + log;

    }

    /**
     * account is added to the bank with the given details
     * NewAccountResponse (with account number) is created and returned
     * broadcast is used to send updates to all the monitoring clients
     */
    public Message processOpenAccount(NewAccountRequest req) {
        int accountNumber = bank.createAccount(req.name, req.password, Currency.fromId(req.currency), req.balance);
        NewAccountResponse acctRes = new NewAccountResponse(accountNumber);
        MessageHeader respHeader = new MessageHeader(Status.OK, "", Opcode.NEW_ACCOUNT);
        Message message = new Message(respHeader, acctRes);

        broadcast(String.format("User %s opened a new bank account. Account number: %d", req.name, accountNumber));
        return message;
    }

    /**
     * get the account object from bank
     * Error checking for the following:
     *  1. invalid account number
     *  2. invalid name or password
     * if no erros, gets the transactions from the account, and gets the latest n logs (based on user input)
     * convert logs array into a string and create a LogsResponse (with String of logs) to send to client
     *
     */
    public Message processCheckLogs(LogsRequest req) {
        Account account = bank.getAccount(req.accountNumber);
        if (account == null) {
            return getError("Account does not exist", new LogsResponse());
        }
        if (!account.validate(req.name, req.password)) {
            return getError("name or password is incorrect", new LogsResponse());
        }
        ArrayList<String> allLogs = account.getTransactions();
        ArrayList<String> tail = new ArrayList<String>(
                allLogs.subList(Math.max(allLogs.size() - req.count, 0), allLogs.size()));
        Collections.reverse(tail);
        String transactions = String.join(";", tail);
        MessageHeader header = new MessageHeader(Status.OK, "", Opcode.CHECK_LOGS);
        LogsResponse body = new LogsResponse(transactions);

        broadcast(String.format("User %s checked account number: %d", req.name, req.accountNumber));
        return new Message(header, body);
    }

    /**
     * get the account object from bank
     * Error checking for the following:
     *  1. invalid account number
     *  2. invalid name or password
     *  3. correct currency type
     *  4. sufficient balance
     * if no erros, withdraw given amount from account, create log and create UpdatedBalanceResponse (with new balance) to send to client
     *
     */
    public Message processWithdraw(WithdrawRequest req) {
        Account account = bank.getAccount(req.accountNumber);
        if (account == null) {
            return getError("Account does not exist", new UpdatedBalanceResponse());
        }
        if (!account.validate(req.name, req.password)) {
            return getError("name or password is incorrect", new UpdatedBalanceResponse());
        }
        if (Currency.fromId(req.currency) != account.getCurrency()) {
            return getError("The Currency type you have selected is not the same as your account",
                    new UpdatedBalanceResponse());
        }
        if (req.amount > account.getBalance()) {
            return getError("Your account does not have enough money",
                    new UpdatedBalanceResponse(account.getBalance()));
        }
        double balance = account.withdraw(req.amount);
        account.addLog(formatLog(String.format("$%.2f withdrawed from your account", req.amount)));
        MessageHeader header = new MessageHeader(Status.OK, "");
        UpdatedBalanceResponse body = new UpdatedBalanceResponse(balance);

        broadcast(
                String.format("User %s withdrew %.2f from account number: %d", req.name, req.amount, req.accountNumber));
        return new Message(header, body);
    }

    /**
     * get the account object from bank
     * Error checking for the following:
     *  1. invalid account number
     *  2. invalid name or password
     *  3. correct currency type
     * if no erros, deposit given amount to account, create log and create UpdatedBalanceResponse (with new balance) to send to client
     *
     */
    public Message processDeposit(DepositRequest req) {
        Account account = bank.getAccount(req.accountNumber);
        if (account == null) {
            return getError("Account does not exist", new UpdatedBalanceResponse());
        }
        if (!account.validate(req.name, req.password)) {
            return getError("name or password is incorrect", new UpdatedBalanceResponse());
        }
        if (Currency.fromId(req.currency) != account.getCurrency()) {
            return getError("The Currency type you have selected is not the same as your account",
                    new UpdatedBalanceResponse());
        }
        double balance = account.deposit(req.amount);
        account.addLog(formatLog(String.format("$%.2f %s deposited to your account", req.amount, Currency.fromId(req.currency))));
        MessageHeader header = new MessageHeader(Status.OK, "");
        UpdatedBalanceResponse body = new UpdatedBalanceResponse(balance);

        broadcast(
                String.format("User %s deposited %.2f to account number: %d", req.name, req.amount, req.accountNumber));
        return new Message(header, body);
    }


    /**
     * get the account object from bank
     * Error checking for the following:
     *  1. invalid account number
     *  2. invalid name or password
     *  3. correct currency type
     * if no erros, remove account from bank and create CloseAccountResponse (with account number of closed account) to send to client
     *
     */
    public Message processCloseAccount(CloseAccountRequest req) {
        Account account = bank.getAccount(req.accountNumber);
        if (account == null) {
            return getError("Account does not exist", new CloseAccountResponse());
        }
        if (!account.validate(req.name, req.password)) {
            return getError("name or password is incorrect", new CloseAccountResponse());
        }
        bank.closeAccount(req.accountNumber);
        MessageHeader header = new MessageHeader(Status.OK, "");
        CloseAccountResponse body = new CloseAccountResponse(req.accountNumber);

        broadcast(String.format("User %s closed account number: %d", req.name, req.accountNumber));
        return new Message(header, body);
    }

    /**
     * get the account object from bank
     * Error checking for the following:
     *  1. invalid account number
     *  2. invalid name or password
     * if no erros, get balance of account, and create UpdatedBalanceResponse (with new balance) to send to client
     *
     */
    public Message processCheckBalance(CheckBalanceRequest req) {
        Account account = bank.getAccount(req.accountNumber);
        if (account == null) {
            return getError("Account does not exist", new UpdatedBalanceResponse());
        }
        if (!account.validate(req.name, req.password)) {
            return getError("name or password is incorrect", new UpdatedBalanceResponse());
        }
        double balance = account.getBalance();
        Currency curr = account.getCurrency();
        MessageHeader header = new MessageHeader(Status.OK, "");
        UpdatedBalanceResponse body = new UpdatedBalanceResponse(balance);

        broadcast(String.format("User %s checked balance for account number %d", req.name, req.accountNumber));
        return new Message(header, body);
    }

    /**
     * get the account object from bank
     * Error checking for the following:
     *  1. invalid account number of sender
     *  2. invalid name or password of sender
     *  3. correct currency type
     *  4. sufficient balance in sender
     *  5. invalid receiver account number
     * if no erros, withdraw from sender and deposit to receiver, create logs in boths accounts
     * and create UpdatedBalanceResponse (with new balance) to send to client
     *
     */
    public Message processTransfer(TransferRequest req) {
        Account account = bank.getAccount(req.accountNumber);
        if (account == null) {
            return getError("Account does not exist", new UpdatedBalanceResponse());
        }
        if (!account.validate(req.name, req.password)) {
            return getError("name or password is incorrect", new UpdatedBalanceResponse());
        }

        Account receiver = bank.getAccount(req.receiver);
        if (receiver == null) {
            return getError("Receiver account does not exist", new UpdatedBalanceResponse());
        }

        if (Currency.fromId(req.currency) != account.getCurrency()) {
            return getError("The Currency type you have selected is not the same as your account",
                    new UpdatedBalanceResponse());
        }
        if (req.amount > account.getBalance()) {
            return getError("Your account does not have enough money", new UpdatedBalanceResponse());
        }
        double balance = account.withdraw(req.amount);
        receiver.deposit(req.amount);
        account.addLog(
                formatLog(String.format("$%.2f transferred from your account to account number %d (%s).", req.amount,
                        req.receiver, receiver.getName())));
        receiver.addLog(
                formatLog(String.format("$%.2f transferred from account number %d (%s) to your account.", req.amount,
                        req.accountNumber, account.getName())));
        MessageHeader header = new MessageHeader(Status.OK, "");
        UpdatedBalanceResponse body = new UpdatedBalanceResponse(balance);

        broadcast(String.format("User %s transferred %d%s to account number: %d", req.name, req.amount,
                Currency.fromId(req.currency), req.receiver));

        return new Message(header, body);
    }

    /**
     * add client identifier and the monitoring duration information to the subscribers list
     * return acknowledgement response to client
     */
    public Message processMonitor(MonitorRequest req) {
        String key = req.clientIp + "," + Integer.toString(req.clientPort);
        subscribers.put(key, Instant.now().plusSeconds(60 * req.monitorDuration));
        System.out.printf("User at ip: %s, port: %d subscribed to monitor for %d minutes...\n", req.clientIp,
                req.clientPort,
                req.monitorDuration);

        MessageHeader header = new MessageHeader(Status.OK, "");
        MonitorResponse body = new MonitorResponse("Successfully subscribed to monitor server...");
        Message returnMsg = new Message(header, body);
        return returnMsg;
    }

    /**
     * Used for monitoring:
     * remove any subscribers whose monitring duration has expired
     * for each client in the subscribers list, 
     * send a MonitorResponse with the relavant String information about event to client
     */
    private void broadcast(String info) {
        removeExpiredSubscribers();
        MessageHeader header = new MessageHeader(Status.OK, "");
        MonitorResponse body = new MonitorResponse(info);
        Message msg = new Message(header, body);
        subscribers.forEach((key, instant) -> {
            System.out.println("ip-port: " + key);
            String[] ip_port = key.split(",");
            String ip = ip_port[0];
            int port = Integer.parseInt(ip_port[1]);
            try {
                skeleton.sendMessage(msg, InetAddress.getByName(ip), port);
            } catch (Exception e) {
                System.out.println(e);
            }
        });
    }

    /**
     * Used for monitoring:
     * remove client from subscribers list if their monitoring duration expired
     */
    private void removeExpiredSubscribers() {
        subscribers.entrySet().removeIf(x -> x.getValue().isBefore(Instant.now()));
    }

    /**
     * 
     * @param error the error message
     * @param body empty repsonse body of the relavent response class
     * @return A Message with BAD_REQUEST status, error message and empty response body
     */
    private Message getError(String error, Marshallable body) {
        MessageHeader header = new MessageHeader(Status.BAD_REQUEST, error);
        return new Message(header, body);
    }

}
