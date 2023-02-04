package common.request;

import common.communication.Marshallable;
import bank.Currency;

/*
  This class is used as the message body for a new account request.
  It requires the parameters: name, password, currency and balance.
*/

public class NewAccountRequest implements Marshallable {
  public String name;
  public String password;
  public int currency;
  public double balance;

  // ----------------------------Constructors-----------------------------
  public NewAccountRequest() {
  }

  public NewAccountRequest(String name, String password, Currency currency, double balance) {
    super();
    this.name = name;
    this.password = password;
    this.currency = currency.getId();
    this.balance = balance;
  }

  // ----------------------------Interface Methods-----------------------------
  public String[] getAllAttributes() {
    String[] attributes = { "name", "password", "currency", "balance" };
    return attributes;
  };

  public Object getAttribute(String attr) {
    switch (attr) {
      case "name":
        return this.name;
      case "password":
        return this.password;
      case "currency":
        return this.currency;
      case "balance":
        return this.balance;
      default:
        return null;
    }
  }

  public void setAttribute(String attr, Object val) {
    switch (attr) {
      case "name":
        this.name = (String) val;
        break;
      case "password":
        this.password = (String) val;
        break;
      case "currency":
        this.currency = (int) val;
        break;
      case "balance":
        this.balance = (double) val;
        break;
      default:
        System.out.println("No such attribute found");
        break;
    }
  };

  @Override
  public String toString() {
    return "NewAccountRequest(name: " + name + ", password: " + password + ", Currency: " + Currency.fromId(currency)
        + ", balance: " + balance + ")";
  }
}
