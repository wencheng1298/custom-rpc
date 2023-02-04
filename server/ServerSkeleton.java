package server;

import java.net.DatagramSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Map;

import common.communication.Message;
import common.communication.Communications;
import server.routing.Router;

/*
  The skeleton class handles all the communication for the server. It is first initialised with a socket and also whether messages
  will be lost when sending messages to the server.
*/
public class ServerSkeleton {
  private DatagramSocket serverSocket;
  private byte[] buffer = new byte[1024];
  private Communications comm = new Communications();
  private boolean loseMessage = false;
  private boolean dontSendMessage = false;

  public ServerSkeleton(DatagramSocket serverSocket, boolean loseMessage) {
    this.serverSocket = serverSocket;
    this.loseMessage = loseMessage;
  }

  /*
   * The router class provides information about how different messages
   * received should be processed.
   * This method receives packets from the client and firstly unmarshals the
   * message header.
   * Messages are then processed using router and then marshaled and results are
   * sent back to the client.
   */
  public void receiveThenSend(Router r) {
    Map<String, String> env = System.getenv();
    boolean debug = Integer.parseInt(env.getOrDefault("DEBUG", "0")) != 0;
    this.loseMessage = Integer.parseInt(env.getOrDefault("LOSE_MESSAGE", "0")) != 0;

    while (true) {
      try {
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(datagramPacket);
        if (debug) {
          System.out.println("Received something!");
        }
        InetAddress clientAddress = datagramPacket.getAddress();

        int clientPort = datagramPacket.getPort();

        byte[] byteMessage = datagramPacket.getData();

        ByteBuffer buf = ByteBuffer.wrap(byteMessage);
        Message message = r.route(buf, clientAddress.getHostAddress(), clientPort);
        buf.clear();
        sendMessage(message, clientAddress, clientPort);
      } catch (Exception e) {
        e.printStackTrace();
        break;
      }
    }
  }

  /*
   * This method first marshals the message and using the client's address and
   * port number, it sends them to the receiving client.
   */
  public void sendMessage(Message message, InetAddress clientAddress, int clientPort) throws IOException {
    byte[] res = comm.marshal(message);

    DatagramPacket datagramPacket = new DatagramPacket(res, res.length, clientAddress,
        clientPort);

    if (loseMessage) {
      if (!dontSendMessage) {
        serverSocket.send(datagramPacket);
        // System.out.println("Message sent!!!");

      } else {
        System.out.println("Message lost!!!");
      }
      dontSendMessage = !dontSendMessage;
    } else {
      serverSocket.send(datagramPacket);
    }
    return;
  }

}
