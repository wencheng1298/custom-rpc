package bank;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import common.communication.Marshallable;

/**
 * Account class defines the parameters associated with each account creted, as well as the methods available for it
 */
public class Account implements Marshallable {
  private final String className = "account";

  private int accountNo;
  private String name;
  private String password;
  private double balance;
  private Currency currency; // eg. USD, SGD

  /**
   * keeps a list of all transactions that involve this account. 
   * i.e. withdrawals, deposits, and transfers to and from
   * 
   * format of log string is:
   * Timestamp: String description of transaction
   */
  private ArrayList<String> transactions = new ArrayList<String>();


  public Account(int accountNo, String name, String password, Currency currency, double balance) {
    this.accountNo = accountNo;
    this.name = name;
    this.balance = balance;
    this.currency = currency;
    this.password = password;
  }

  // ----------------------------Interface Methods-----------------------------
  public String[] getAllAttributes() {
    String[] attributes = { "accountNo", "name", "balance" };
    return attributes;
  };

  public Object getAttribute(String attr) {
    switch (attr) {
      case "accountNo":
        return this.accountNo;
      case "name":
        return this.name;
      case "balance":
        return this.balance;
      default:
        return null;
    }
  }

  public String getClassName() {
    return this.className;
  };

  public void setAttribute(String attr, Object val) {
    switch (attr) {
      case "accountNo":
        this.accountNo = (int) val;
        break;
      case "name":
        this.name = (String) val;
        break;
      case "balance":
        this.balance = (Double) val;
        break;

      default:
        System.out.println("No such attribute found");
        break;
    }
  };

  // ----------------------------Class Methods-----------------------------
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{ \n");
    sb.append("\t accountNo: " + accountNo + "\n");
    sb.append("\t name: " + name + "\n");
    sb.append("\t name: " + password + "\n");
    sb.append("\t Currency Type: " + currency + "\n");
    sb.append("\t balance: $" + balance + "\n");
    sb.append("}");
    return sb.toString();
  }

  

  public int getAccountNo() {
    return accountNo;
  }
  public String getName() {
    return name;
  }
  public double getBalance() {
    return balance;
  }
  public Currency getCurrency() {
    return this.currency;
  }

  public ArrayList<String> getTransactions() {
    return transactions;
  }
  public void setAccountNo(int num) {
    this.accountNo = num;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  // remove amount form balance if there is sufficient balance
  public double withdraw(double amount) {
    if (amount <= this.balance) {
      this.balance -= amount;
      return this.balance;
  
    } else {
      return -1;
    }
  }

  // add a string Log to list of transaction logs
  public void addLog(String transaction) {
    transactions.add(transaction);
  }

  // add amount to account balance
  public double deposit(double amount) {
    this.balance += amount;
    return this.balance;
  }

  // validate name and password
  public Boolean validate(String name, String password) {

    if (!name.equals(this.name)) {
      return false;
    } else if (!password.equals(this.password)) {
      return false;
    } else {
      return true;
    }
  }

 


}
