package common.response;

import common.communication.Marshallable;
/*
  This class is used as the message body for a response to check transaction history request.
  It requires the parameters: logs (String combining the logs information to return to client)
*/
public class LogsResponse implements Marshallable {
  public String logs;

  public LogsResponse() {
    this.logs = "";
  }

  public LogsResponse(String logs) {
    this.logs = logs;
  }

  // ----------------------------Interface Methods-----------------------------
  public String[] getAllAttributes() {
    String[] attributes = { "logs" };
    return attributes;
  };

  public Object getAttribute(String attr) {
    switch (attr) {
      case "logs":
        return this.logs;

      default:
        return null;
    }
  }

  public void setAttribute(String attr, Object val) {
    switch (attr) {
      case "logs":
        this.logs = (String) val;
        break;

      default:
        System.out.println("No such attribute found");
        break;
    }
  };

  @Override
  public String toString() {
    return "LogsResponse(logs: " + logs + ")";
  }

}
