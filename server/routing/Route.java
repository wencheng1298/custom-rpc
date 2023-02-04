package server.routing;


import java.util.function.Function;
import common.communication.Marshallable;


/**
 * Route class defines the struture for the binding between a request body and its handler function that processes the request
 * The handler function is a function in BankController that takes in a requestBody (see requests folder)
 * and outputs a Message object (the response message)
 */
public class Route {
    public Marshallable reqBody;
    public Function<Marshallable, Marshallable> handler;

    public Route(Marshallable reqBody, Function<Marshallable, Marshallable> handler) {
        this.reqBody = reqBody;
        this.handler = handler;
      }  
}
