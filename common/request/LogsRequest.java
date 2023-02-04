package common.request;

import common.communication.Marshallable;

/*
  This class is used as the message body for a log request.
  It requires the parameters: accountNumber, name, password and count.
    - Count refers to the number of past transactions that we would like to retrieve.
*/

public class LogsRequest implements Marshallable {
  public int accountNumber;
  public String name;
  public String password;
  public int count;

  // ----------------------------Constructors-----------------------------
  public LogsRequest() {
  }

  public LogsRequest(int accountNumber, String name, String password, int count) {
    super();
    this.accountNumber = accountNumber;
    this.name = name;
    this.password = password;
    this.count = count;
  }

  // ----------------------------Interface Methods-----------------------------
  public String[] getAllAttributes() {
    String[] attributes = { "accountNumber", "name", "password", "count" };
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
      case "count":
        return this.count;

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
      case "count":
        this.count = (int) val;
        break;

      default:
        System.out.println("No such attribute found");
        break;
    }
  };

  @Override
  public String toString() {
    return "LogsRequest(accountNumber: " + accountNumber + ", name: " + name + ", password: " + password + ", Count: "
        + count + ")";
  }
}
