package common.response;

import common.communication.Marshallable;

/*
  This class is used as the message body for a response to monitor request, or response to send to monitoring subscribers about an event.
  It requires the parameters: monitorResponse (either an acknowledgement of successful subscription or event information about an activity on the bank service)
*/
public class MonitorResponse implements Marshallable {
  public String monitorResponse;

  // ----------------------------Constructors-----------------------------
  public MonitorResponse() {
  }

  public MonitorResponse(String monitorResponse) {
    super();
    this.monitorResponse = monitorResponse;

  }

  // ----------------------------Interface Methods-----------------------------
  public String[] getAllAttributes() {
    String[] attributes = { "monitorResponse" };
    return attributes;
  };

  public Object getAttribute(String attr) {
    switch (attr) {
      case "monitorResponse":
        return this.monitorResponse;
      default:
        return null;
    }
  }

  public void setAttribute(String attr, Object val) {
    switch (attr) {
      case "monitorResponse":
        this.monitorResponse = (String) val;
        break;
      default:
        System.out.println("No such attribute found");
        break;
    }
  };

  @Override
  public String toString() {
    return "MonitorResponse(monitorResponse: " + monitorResponse + ")";
  }
}
