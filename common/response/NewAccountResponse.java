package common.response;

import common.communication.Marshallable;

/*
  This class is used as the message body for a response to create new account request.
  It requires the parameters: accountNumber
*/
public class NewAccountResponse implements Marshallable {
  public int accountNumber;

  public NewAccountResponse() {
    this.accountNumber = 0;

  }

  public NewAccountResponse(int accountNumber) {
    this.accountNumber = accountNumber;

  }

  // ----------------------------Interface Methods-----------------------------
  public String[] getAllAttributes() {
    String[] attributes = { "accountNumber" };
    return attributes;
  };

  public Object getAttribute(String attr) {
    switch (attr) {
      case "accountNumber":
        return this.accountNumber;

      default:
        return null;
    }
  }

  public void setAttribute(String attr, Object val) {
    switch (attr) {
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
    return "NewAccountResponse(accountNumber: " + accountNumber + ")";
  }

}
