package common.request;

import common.communication.Marshallable;
import bank.Currency;

/*
  This class is used as the message body for a withdraw request.
  It requires the parameters: accountNumber, name, password, currency, amount.
*/

public class WithdrawRequest implements Marshallable {
  public int accountNumber;
  public String name;
  public String password;
  public int currency;
  public double amount;

  // ----------------------------Constructors-----------------------------
  public WithdrawRequest() {
  }

  public WithdrawRequest(int accountNumber, String name, String password, Currency currency, double amount) {
    super();
    this.accountNumber = accountNumber;
    this.name = name;
    this.password = password;
    this.currency = currency.getId();
    this.amount = amount;
  }

  // ----------------------------Interface Methods-----------------------------
  public String[] getAllAttributes() {
    String[] attributes = { "accountNumber", "name", "password", "currency", "amount" };
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
      default:
        System.out.println("No such attribute found");
        break;
    }
  };

  @Override
  public String toString() {
    return "WithdrawRequest(accountNumber: " + accountNumber + ", name: " + name + ", password: " + password
        + ", Currency: " + Currency.fromId(currency) + ", amount: " + amount + ")";
  }
}
