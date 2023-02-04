package client;

import java.net.DatagramSocket;
import java.net.DatagramSocketImplFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.net.DatagramPacket;

import common.communication.Message;
import common.communication.Communications;
import common.communication.Marshallable;

/*
  The proxy class handles all the communication for the client. Users interact with UI provided by the client class,
  the client collects the necessary information and puts them into a message before passing to the proxy class for marshalling
  and sending to the server.
*/
public class BankClientProxy {
  int serverPort;
  String serverIp;

  DatagramSocket socket;
  private static int requestId;
  private boolean loseMessage = false;
  private boolean dontSendMessage = false;
  private int timeout = 5; // in seconds

  // ----------------------------Constructors-----------------------------
  public BankClientProxy() {
  }

  public BankClientProxy(DatagramSocket clientSocket, String serverIp, int serverPort, boolean loseMessage) {
    try {
      this.socket = clientSocket;
      this.serverIp = serverIp;
      this.serverPort = serverPort;
      this.loseMessage = loseMessage;
      requestId = 0;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*
   * This method marshals the message into its corresponding byte[] and sends to
   * the server through UDP format.
   * It then awaits for a reply from the server. The respObj parameter represents
   * the expected object structure
   * to be received from server.
   */
  public Message sendThenReceive(Message message, Marshallable respObj) throws IOException {
    Communications comm = new Communications();
    byte[] byteMessage = comm.marshal(message);

    InetSocketAddress serverAdd = new InetSocketAddress(serverIp, serverPort);

    DatagramPacket datagramPacket = new DatagramPacket(byteMessage,
        byteMessage.length,
        serverAdd);

    do {
      try {
        this.send(datagramPacket);
        System.out.println("Waiting for response...");

        Message respMsg = this.recvMessage(respObj, this.timeout);
        return respMsg;

      } catch (SocketTimeoutException e) {
        System.out.println("Timeout! Sending message again...");
      } catch (Exception e) {
        e.printStackTrace();
      }
    } while (true);
  }

  /*
   * Handles the sending of datagram to the server.
   * Alternates between sending and not sending datagram if loseMessage is enabled
   * Used by sendThenReceive()
   */
  private void send(DatagramPacket datagram) throws IOException {
    if (loseMessage) {
      if (!dontSendMessage) {
        System.out.println("Message sent...");
        socket.send(datagram);
      } else {
        System.out.println("Message lost!!!");
      }
      this.dontSendMessage = !dontSendMessage;

    } else {
      System.out.println("Message sent...");
      socket.send(datagram);
    }
    return;
  }

  /*
   * Handles the receiving of datagram from the server.
   * It takes in the expected object to be unmarshaled into as well as
   * timeouDuration.
   * Upon timeout, a socketException is thrown.
   * Used by sendThenReceive()
   */
  public Message recvMessage(Marshallable respObj, int timeoutDuration) throws SocketException, IOException {
    Communications comm = new Communications();

    byte[] reply = new byte[1024];
    DatagramPacket packet = new DatagramPacket(reply, reply.length);

    socket.setSoTimeout(timeoutDuration * 1000);
    socket.receive(packet);

    ByteBuffer buf = ByteBuffer.wrap(packet.getData());

    Message responseMsg = (Message) comm.unmarshal(buf, respObj);
    buf.clear();
    return responseMsg;
  }
}
