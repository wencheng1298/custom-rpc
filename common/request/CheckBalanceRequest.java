package common.request;

import common.communication.Marshallable;

/*
  This class is used as the message body for a check balance request.
  It requires the parameters: accountNumber, name and password.
*/

public class CheckBalanceRequest implements Marshallable {
  public String name;
  public String password;
  public int accountNumber;

  // ----------------------------Constructors-----------------------------
  public CheckBalanceRequest() {
  }

  public CheckBalanceRequest(int accountNumber, String name, String password) {
    super();
    this.name = name;
    this.password = password;
    this.accountNumber = accountNumber;

  }

  // ----------------------------Interface Methods-----------------------------
  public String[] getAllAttributes() {
    String[] attributes = { "name", "password", "accountNumber" };
    return attributes;
  };

  public Object getAttribute(String attr) {
    switch (attr) {
      case "name":
        return this.name;
      case "password":
        return this.password;
      case "accountNumber":
        return this.accountNumber;
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
      case "accountNumber":
        this.accountNumber = (int) val;
        break;
      default:
        System.out.println("No such attribute found");
        break;
    }
  };

  @Override
  public String toString() {
    return "CheckBalanceRequest(name: " + name + ", password: " + password + ", accountNumber: " + accountNumber + ")";
  }
}
