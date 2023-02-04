package common.communication;

import common.constants.Opcode;
import common.constants.Status;

/*
    Message header contains attributes that provides some context information about the request or response.
    Clients and server use this information to determine what should be done upon receiving a message.
*/
public class MessageHeader implements Marshallable {
  public int status;
  public String message;
  public int opCode;
  public int messageId;
  private static int idCount = 0;

  // ----------------------------Constructors-----------------------------
  public MessageHeader() {
  };

  public MessageHeader(Status status, String msg, Opcode opCode) {
    this.messageId = ++idCount;
    this.status = status.getStatusCode();
    this.opCode = opCode.getId();
    this.message = msg;
  }

  public MessageHeader(Status status, String msg) {
    this.messageId = ++idCount;
    this.status = status.getStatusCode();
    // responses dont need a opcode
    this.opCode = 0;
    this.message = msg;
  }

  // ----------------------------Interface Methods-----------------------------
  public String[] getAllAttributes() {
    String[] attributes = { "status", "opCode", "message", "messageId" };
    return attributes;
  };

  public Object getAttribute(String attr) {
    switch (attr) {
      case "status":
        return this.status;
      case "message":
        return this.message;
      case "opCode":
        return this.opCode;
      case "messageId":
        return this.messageId;
      default:
        return null;
    }
  }

  public void setAttribute(String attr, Object val) {
    switch (attr) {
      case "status":
        this.status = (int) val;
        break;
      case "message":
        this.message = (String) val;
        break;
      case "opCode":
        this.opCode = (int) val;
        break;
      case "messageId":
        this.messageId = (int) val;
        break;
      default:
        System.out.println("No such attribute found");
        break;
    }
  }

  // ----------------------------Class Methods-----------------------------
  @Override
  public String toString() {
    return "MessageHeader(status: " + status + ", opCode: " + opCode + ", message: " + message + ", messageId: "
        + messageId + ")";
  }
}
