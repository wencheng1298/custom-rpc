package common.response;

import common.communication.Marshallable;

/*
  This class is used as the message body for a response to withdraw, deposit, transfer and check balance requests.
  It requires the parameters: balance (the udpated balance of the requesting client)
*/
public class UpdatedBalanceResponse implements Marshallable {
    public double balance;
   

    public UpdatedBalanceResponse() {
      this.balance = 0;
        
    }

    public UpdatedBalanceResponse(double balance) {
        this.balance = balance;
    
    }

  

    // ----------------------------Interface Methods-----------------------------
  public String[] getAllAttributes() {
    String[] attributes = { "balance" };
    return attributes;
  };

  public Object getAttribute(String attr) {
    switch (attr) {
      case "balance":
        return this.balance;
      
      default:
        return null;
    }
  }

  public void setAttribute(String attr, Object val) {
    switch (attr) {
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
        return "UpdatedBalanceResponse(" + balance + ")";
    }

     
    
}
