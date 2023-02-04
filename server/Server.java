package server;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Map;

import common.request.*;
import server.routing.Router;
import common.constants.Opcode;


/**
 * Main Class to start the server
 */
public class Server {

  /**
   * @param args System arguments include AT_MOST_ONCE and LOSE_MESSAGE, both boolean variables (1 for true and 0 otherwise)
   * @throws SocketException
   * Initializes different server components, i.e. ServerSkeleton (for communication), BankController(process the requests) 
   * and Router (routes the incoming requests and maintains cache)
   */
  public static void main(String[] args) throws SocketException {
    Map<String, String> env = System.getenv();
    boolean atMostOnce = Integer.parseInt(env.getOrDefault("AT_MOST_ONCE", "0")) != 0;
    boolean loseMessage = Integer.parseInt(env.getOrDefault("LOSE_MESSAGE", "0")) != 0;

    String serverIp = "localhost";
    int serverPort = 2222;

    DatagramSocket serverSocket = new DatagramSocket(new InetSocketAddress(serverIp, serverPort));

    ServerSkeleton skeleton = new ServerSkeleton(serverSocket, loseMessage);

    BankController controller = new BankController(skeleton);

    // bind the different operations (services) to its respective handler functions in BankController
    Router r = new Router(atMostOnce)
        .bind(Opcode.NEW_ACCOUNT, controller::processOpenAccount, new NewAccountRequest() {
        })
        .bind(Opcode.CLOSE_ACCOUNT, controller::processCloseAccount, new CloseAccountRequest() {
        })
        .bind(Opcode.CHECK_LOGS, controller::processCheckLogs, new LogsRequest() {
        })
        .bind(Opcode.WITHDRAW, controller::processWithdraw, new WithdrawRequest() {
        })
        .bind(Opcode.DEPOSIT, controller::processDeposit, new DepositRequest() {
        })
        .bind(Opcode.TRANSFER, controller::processTransfer, new TransferRequest() {
        })
        .bind(Opcode.MONITOR, controller::processMonitor, new MonitorRequest() {
        })
        .bind(Opcode.CHECK_BALANCE, controller::processCheckBalance, new CheckBalanceRequest() {});
    

    System.out.println("Starting server...");

    // listen for incoming messages from clients
    skeleton.receiveThenSend(r);

  }
}
