package common.response;

import common.communication.Marshallable;

/*
  This class is used as the response message body for a close account request.
  It requires the parameters: accountNumber
*/
public class CloseAccountResponse implements Marshallable {
    public int accountNumber;
   

    public CloseAccountResponse() {
      this.accountNumber = 0;
        
    }

    public CloseAccountResponse(int accountNumber) {
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
        return "CloseAccountResponse(" + accountNumber + ")";
    }

     
    
}
