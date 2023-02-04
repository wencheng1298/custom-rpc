package server.routing;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.nio.ByteBuffer;

import common.constants.Opcode;
import common.constants.Status;
import common.communication.Message;
import common.communication.MessageHeader;
import common.communication.Communications;
import common.communication.Marshallable;

public class Router {
  /**
   * Keeps a mapping between an Opcode (defining the type of request)
   * and a Route object (that keeps the request body, as well as teh handler function for the different kinds of requests)
   */
  private Map<Opcode, Route> routes = new HashMap<>();

  /**
   * Cache is a map of the request identifier to a response message
   * used for at-most-once semantics where the response is sent from the cache if teh request is repeated.
   */
  private Map<String, Message> cache = new HashMap<>();

  Communications comm = new Communications();
  private boolean atMostOnce;

 
  public Router(boolean atMostOnce) {
    this.atMostOnce = atMostOnce;
  }

  /**
   * 
   * @param operation The operation code defining the type of service required
   * @param handler handler function in BankController to process the request
   * @param reqBody The request body class of the request
   * create a Route with the handler function and bind it to the Opcode. added to routes mapping
   */
  public <ReqBody, ResBody> Router bind(Opcode operation, Function<ReqBody, ResBody> handler, Marshallable reqBody) {
    routes.put(operation, new Route(
        reqBody,
        (req) -> ((Function<Marshallable, Marshallable>) handler).apply(req)));

    return this;
  }


  /**
   * 
   * @param buf the bytebuffer of the incoming request
   * @param header the message header of the incoming request
   * @return Message object (the response message)
   * get the route based on the Opcode in the request header
   * unmarshall the request body and route process to the respective handler function
   */
  private Message routeUncached(ByteBuffer buf, MessageHeader header) {
    try {
      Route route = routes.get(Opcode.fromCode(header.opCode));
      if (route == null) {
        System.out.println("no route found");
      }
  
      Marshallable body = comm.unmarshal(buf, (Marshallable) route.reqBody);
      return (Message) route.handler.apply(body);

    } catch (Exception e) {
      System.out.println("oops some exception");
      e.printStackTrace();
    }
    return new Message();
  }

  /**
   * 
   * @param buf the bytebuffer containing the request
   * @param ip client IP
   * @param port client Port
   * @return Message object (the response message)
   * 
   * check if the request is in cache and if at-most-once semantic is used.
   * If yes return repsonse from cache. Else, process the request
   */
  public Message route(ByteBuffer buf, String ip, int port) {
    MessageHeader header = (MessageHeader) comm.unmarshal(buf, new MessageHeader());
    String key = messageIdentifier(header.messageId, ip, port);
    if (cache.containsKey(key) && atMostOnce) {
      return cache.get(key);
    } else {
      Message resp = routeUncached(buf, header);
      if (atMostOnce) {
        cache.put(key, resp);
      }
      return resp;
    }
  
  }

  /**
   * Structure of the message identifier to store in cache
   * Combines the request ID, client IP and client Port
   */
  private String messageIdentifier(int reqId, String ip, int port) {
    return String.format("%d-%s-%d", reqId, ip, port);
  }

}
