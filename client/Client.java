package client;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import java.util.Scanner;
import java.time.LocalDateTime;

import java.util.Map;

import common.constants.Status;
import common.constants.Opcode;

import common.communication.Message;
import common.communication.MessageHeader;
import common.request.*;
import common.response.*;

import bank.Currency;

/**
 * This is the main entry to the Client program.
 * 
 */
public class Client {

  private static int PASSWORD_LENGTH = 7;
  private static Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) throws SocketException, UnknownHostException {

    // initialise environment variables and bankProxy to use for communication with server
    Map<String, String> env = System.getenv();
    String clientIp = env.getOrDefault("CLIENT_HOST", "localhost");
    int clientPort = Integer.parseInt(env.getOrDefault("CLIENT_PORT", "12740"));

    System.out.println(clientIp);
    System.out.println(clientPort);

    String serverIp = env.getOrDefault("SERVER_HOST", "localhost");
    int serverPort = 2222;

    DatagramSocket clientSocket = new DatagramSocket(new InetSocketAddress(clientIp, clientPort));

    boolean loseMessage = Integer.parseInt(env.getOrDefault("LOSE_MESSAGE", "0")) != 0;
    BankClientProxy bankProxy = new BankClientProxy(clientSocket, serverIp, serverPort, loseMessage);
    MessageHeader recvHeader = new MessageHeader();

    System.out.println("Starting Bank Client...");
    final int QUITCODE = 9;

    /**
     * get User inputs for the different services and create respective Request Messages
     * get and display the responses from the server and display it accordingly
     */
    while (true) {
      print_menu();
      int opCode = Integer.parseInt(scanner.nextLine());
      if (opCode != QUITCODE) {
        Opcode operation = Opcode.fromCode(opCode);
        switch (operation) {
          case NEW_ACCOUNT:

            String name = getName();
            String password = getPassword();
            Currency curr = getCurrency();
            System.out.print("Initial Balance:");
            double balance = Double.parseDouble(scanner.nextLine());

            MessageHeader header = new MessageHeader(Status.OK, "", Opcode.NEW_ACCOUNT);
            NewAccountRequest req = new NewAccountRequest(name, password, curr, balance);

            Message message = new Message(header, req);

            NewAccountResponse recvBody = new NewAccountResponse();

            Message res = new Message(recvHeader, recvBody);
            try {
              Message resp = bankProxy.sendThenReceive(message, res);
              if (Status.fromCode(resp.header.status) == Status.OK) {
                NewAccountResponse resObj = (NewAccountResponse) resp.body;
                printResponse(String.format("Successfully created a new bank account. Your Account number is %d\n",
                    resObj.accountNumber));
              } else {
                System.out.printf("Failed to create Account. %s\n", resp.header.message);
              }
            } catch (Exception e) {
              System.out.println(e);
            }
            break;

          case CLOSE_ACCOUNT:
            int accountNumber = getAccountNumber();
            name = getName();
            password = getPassword();
            header = new MessageHeader(Status.OK, "", Opcode.CLOSE_ACCOUNT);
            CloseAccountRequest closeReq = new CloseAccountRequest(accountNumber, name, password);
            message = new Message(header, closeReq);
            CloseAccountResponse recvBodyClose = new CloseAccountResponse();

            res = new Message(recvHeader, recvBodyClose);
            try {
              Message resp = bankProxy.sendThenReceive(message, res);
              if (Status.fromCode(resp.header.status) == Status.OK) {
                CloseAccountResponse resObj = (CloseAccountResponse) resp.body;
                printResponse(String.format("Successfully closed your account with Account number %d.\n",
                    resObj.accountNumber));

              } else {
                System.out.printf("Failed to close Account. %s\n", resp.header.message);
              }
            } catch (Exception e) {
              System.out.println(e);
            }

            break;
          case WITHDRAW:
            accountNumber = getAccountNumber();
            name = getName();
            password = getPassword();
            curr = getCurrency();
            double amount = getAmount();
            header = new MessageHeader(Status.OK, "", Opcode.WITHDRAW);
            WithdrawRequest withdrawReq = new WithdrawRequest(accountNumber, name, password, curr, amount);
            message = new Message(header, withdrawReq);
            // Create resp obj
            UpdatedBalanceResponse recvBodyWithdraw = new UpdatedBalanceResponse();

            res = new Message(recvHeader, recvBodyWithdraw);
            try {
              Message resp = bankProxy.sendThenReceive(message, res);
              if (Status.fromCode(resp.header.status) == Status.OK) {
                UpdatedBalanceResponse resObj = (UpdatedBalanceResponse) resp.body;
                printResponse(
                    String.format("Successfully withdrawed $%.2f from you account. Your new balance is $%.2f.\n",
                        amount, resObj.balance));
               
              } else {
                System.out.printf("Failed to withdraw. %s\n", resp.header.message);
              }
            } catch (Exception e) {
              System.out.println(e);
            }

            break;
          case DEPOSIT:
            accountNumber = getAccountNumber();
            name = getName();
            password = getPassword();
            curr = getCurrency();
            amount = getAmount();
            header = new MessageHeader(Status.OK, "", Opcode.DEPOSIT);
            DepositRequest depositReq = new DepositRequest(accountNumber, name, password, curr, amount);
            message = new Message(header, depositReq);
            UpdatedBalanceResponse recvBodyDeposit = new UpdatedBalanceResponse();

            res = new Message(recvHeader, recvBodyDeposit);
            try {
              Message resp = bankProxy.sendThenReceive(message, res);
              if (Status.fromCode(resp.header.status) == Status.OK) {
                UpdatedBalanceResponse resObj = (UpdatedBalanceResponse) resp.body;
                printResponse(String.format("Successfully deposited $%.2f to you account. Your new balance is $%.2f.\n",
                    amount, resObj.balance));

              } else {
                System.out.printf("Failed to Deposit. %s\n", resp.header.message);
              }
            } catch (Exception e) {
              System.out.println(e);
            }

            break;
          case TRANSFER:
            accountNumber = getAccountNumber();
            name = getName();
            password = getPassword();
            System.out.print("Account Number of receiver:");
            int receiver = Integer.parseInt(scanner.nextLine());
            curr = getCurrency();
            amount = getAmount();
            header = new MessageHeader(Status.OK, "", Opcode.TRANSFER);
            TransferRequest transferReq = new TransferRequest(accountNumber, name, password, curr, amount, receiver);
            message = new Message(header, transferReq);

            UpdatedBalanceResponse recvBodytransfer = new UpdatedBalanceResponse();

            res = new Message(recvHeader, recvBodytransfer);
            try {
              Message resp = bankProxy.sendThenReceive(message, res);
              if (Status.fromCode(resp.header.status) == Status.OK) {
                UpdatedBalanceResponse resObj = (UpdatedBalanceResponse) resp.body;
                printResponse(String.format(
                    "Successfully transfered $%.2f from you account to account number %d. Your new balance is $%.2f.\n",
                    amount, receiver, resObj.balance));
              } else {
                System.out.printf("Failed to transfer. %s\n", resp.header.message);
              }
            } catch (Exception e) {
              System.out.println(e);
            }

            break;

          case CHECK_BALANCE:

            accountNumber = getAccountNumber();
            name = getName();
            password = getPassword();
            header = new MessageHeader(Status.OK, "", Opcode.CHECK_BALANCE);
            CheckBalanceRequest checkbalreq = new CheckBalanceRequest(accountNumber, name, password);
            message = new Message(header, checkbalreq);
            UpdatedBalanceResponse recvBodycheck = new UpdatedBalanceResponse();

            res = new Message(recvHeader, recvBodycheck);
            try {
              Message resp = bankProxy.sendThenReceive(message, res);
              if (Status.fromCode(resp.header.status) == Status.OK) {
                UpdatedBalanceResponse resObj = (UpdatedBalanceResponse) resp.body;
                printResponse(String.format(
                    "Your balance is $%.2f.\n", resObj.balance));
              } else {
                printResponse(String.format("Failed to get balance. %s\n", resp.header.message));
              }
            } catch (Exception e) {
              System.out.println(e);
            }

            break;

          case CHECK_LOGS:
            accountNumber = getAccountNumber();
            name = getName();
            password = getPassword();
            System.out.print("Number of transactions you would like to see: ");
            int count = Integer.parseInt(scanner.nextLine());

            header = new MessageHeader(Status.OK, "", Opcode.CHECK_LOGS);
            LogsRequest reqLogs = new LogsRequest(accountNumber, name, password, count);
            System.out.println(header);
            message = new Message(header, reqLogs);

            // Create resp obj
            LogsResponse recvBodyLogs = new LogsResponse();

            res = new Message(recvHeader, recvBodyLogs);
            try {
              Message resp = bankProxy.sendThenReceive(message, res);
              if (Status.fromCode(resp.header.status) == Status.OK) {
                LogsResponse resObj = (LogsResponse) resp.body;
                String[] transactions = resObj.logs.split(";");

                StringBuilder sb = new StringBuilder();
                sb.append(String.format("\tHere are the past %d transactions on your account:\n\n", count));

                System.out.printf("Here are the past %d transactions done:\n", count);
                for (var i = 0; i < transactions.length; i++) {
                  sb.append(String.format("\t%d. %s\n", i + 1, transactions[i]));
                  
                }
                printResponse(sb.toString());

              } else {
                System.out.printf("Failed to get Logs. %s\n", resp.header.message);
              }
            } catch (Exception e) {
              System.out.println(e);
            }
            break;
          case MONITOR:
            System.out.print("Enter the number of minutes you want to monitor the server: ");
            int monitorDuration = Integer.parseInt(scanner.nextLine());

            header = new MessageHeader(Status.OK, "", Opcode.MONITOR);
            MonitorRequest monitorReq = new MonitorRequest(monitorDuration, clientIp, clientPort);
            message = new Message(header, monitorReq);

            MonitorResponse monitorRes = new MonitorResponse();
            res = new Message(recvHeader, monitorRes);

            try {
              Message resp = bankProxy.sendThenReceive(message, res);
              // 1st message to subscribe to monitoring service
              if (Status.fromCode(resp.header.status) == Status.OK) {
                MonitorResponse resObj = (MonitorResponse) resp.body;
                System.out.println(resObj.monitorResponse);

                LocalDateTime timenow = LocalDateTime.now();
                // 2nd message onwards is to receive monitoring updates
                while (timenow.isBefore(timenow.plusMinutes(monitorDuration))) {
                  Message updates = new Message(new MessageHeader(Status.OK, "", Opcode.MONITOR),
                      new MonitorResponse());
                  MonitorResponse monitorResponse = (MonitorResponse) bankProxy.recvMessage(updates,
                      monitorDuration * 60).body;
                  System.out.println(monitorResponse.monitorResponse);
                }

              } else {
                System.out.println(resp.header.message);
              }
            } catch (SocketTimeoutException e) {
              System.out.println("Monitoring ended...");
            } catch (Exception e) {
              System.out.println(e);
            }
            break;
        }
      } else {
        bankProxy.socket.close();
        break;
      }
    }
    scanner.close();
  }

  public static String getName() {
    System.out.print("Name:");
    return scanner.nextLine();
  }

  public static String getPassword() {
    System.out.printf("Your password (%d characters): ", PASSWORD_LENGTH);
    String password = scanner.nextLine();
    if (password.length() != PASSWORD_LENGTH) {
      System.out.printf("Password must be exactly %d characters!\n", PASSWORD_LENGTH);
      return getPassword();
    }
    return password;
  }

  public static int getAccountNumber() {
    System.out.print("Account Number:");
    return Integer.parseInt(scanner.nextLine());
  }

  public static Currency getCurrency() {
    System.out.println("Currency Type:");
    System.out.println("1. SGD");
    System.out.println("2. USD");
    System.out.print("Enter the currency accordingly: ");
    int currencyCode = Integer.parseInt(scanner.nextLine());
    return Currency.fromId(currencyCode);
  }

  public static double getAmount() {
    System.out.print("Amount:");
    return Double.parseDouble(scanner.nextLine());
  }

  public static void print_menu() {
    System.out.println();
    System.out.println("Input your choice:");
    System.out.println("1. Open New Account");
    System.out.println("2. Close Existing Account");
    System.out.println("3. Withdraw money");
    System.out.println("4. Deposit money");
    System.out.println("5. Transfer money");
    System.out.println("6. Check Account Balance");
    System.out.println("7. Check Transaction History");
    System.out.println("8. Monitor Updates to All Accounts");
    System.out.println("9. Exit");
    System.out.println("Enter a command to proceed:");
  }

  public static void printResponse(String response) {
    System.out.println("===================RESPONSE FROM SERVER===================\n\n");
    System.out.println(response + "\n");
    System.out.println("==========================================================\n\n");
  }
}
