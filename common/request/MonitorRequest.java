package common.request;

import common.communication.Marshallable;

/*
  This class is used as the message body for a monitor request.
  It requires the parameters: monitorDuration, ip and port of the client.
*/

public class MonitorRequest implements Marshallable {
  public int monitorDuration;
  public String clientIp;
  public int clientPort;

  // ----------------------------Constructors-----------------------------
  public MonitorRequest() {
  }

  public MonitorRequest(int monitorDuration, String ip, int port) {
    super();
    this.monitorDuration = monitorDuration;
    this.clientIp = ip;
    this.clientPort = port;
  }

  // ----------------------------Interface Methods-----------------------------
  public String[] getAllAttributes() {
    String[] attributes = { "monitorDuration", "clientIp", "clientPort" };
    return attributes;
  };

  public Object getAttribute(String attr) {
    switch (attr) {
      case "monitorDuration":
        return this.monitorDuration;
      case "clientIp":
        return this.clientIp;
      case "clientPort":
        return this.clientPort;
      default:
        return null;
    }
  }

  public void setAttribute(String attr, Object val) {
    switch (attr) {
      case "monitorDuration":
        this.monitorDuration = (int) val;
        break;
      case "clientIp":
        this.clientIp = (String) val;
        break;
      case "clientPort":
        this.clientPort = (int) val;
        break;
      default:
        System.out.println("No such attribute found");
        break;
    }
  };

  @Override
  public String toString() {
    return "MonitorRequest(monitorDuration: " + monitorDuration + ", clientIp: " + clientIp + ", clientPort: "
        + clientPort + ")";
  }
}
