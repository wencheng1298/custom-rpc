package common.request;

import common.communication.Marshallable;
import bank.Currency;

/*
  This class is used as the message body for a transfer request.
  It requires the parameters: accountNumber, name, password, currency, amount and receiver.
    - receiver refers to the account number of the receiving party.
*/

public class TransferRequest implements Marshallable {
  public int accountNumber;
  public String name;
  public String password;
  public int currency;
  public double amount;
  public int receiver;

  // ----------------------------Constructors-----------------------------
  public TransferRequest() {
  }

  public TransferRequest(int accountNumber, String name, String password, Currency currency, double amount,
      int receiver) {
    super();
    this.accountNumber = accountNumber;
    this.name = name;
    this.password = password;
    this.currency = currency.getId();
    this.amount = amount;
    this.receiver = receiver;
  }

  // ----------------------------Interface Methods-----------------------------
  public String[] getAllAttributes() {
    String[] attributes = { "accountNumber", "name", "password", "currency", "amount", "receiver" };
    return attributes;
  };

  public Object getAttribute(String attr) {
    switch (attr) {
      case "accountNumber":
        return this.accountNumber;
      case "name":
        return this.name;
      case "password":
        return this.password;
      case "currency":
        return this.currency;
      case "amount":
        return this.amount;
      case "receiver":
        return this.receiver;
      default:
        return null;
    }
  }

  public void setAttribute(String attr, Object val) {
    switch (attr) {
      case "accountNumber":
        this.accountNumber = (int) val;
        break;
      case "name":
        this.name = (String) val;
        break;
      case "password":
        this.password = (String) val;
        break;
      case "currency":
        this.currency = (int) val;
        break;
      case "amount":
        this.amount = (double) val;
        break;
      case "receiver":
        this.receiver = (int) val;
        break;
      default:
        System.out.println("No such attribute found");
        break;
    }
  };

  @Override
  public String toString() {
    return "TransferRequest(accountNumber: " + accountNumber + ", name: " + name + ", password: " + password
        + ", Currency: " + Currency.fromId(currency) + ", amount: " + amount + ", receiver account number: " + receiver
        + ")";
  }
}
