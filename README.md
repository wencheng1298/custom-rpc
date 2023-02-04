# Custom RPC

1. Run compile.bat to compile client and server code.

2. Fill in the required environment information in runClient.bat and runServer.bat.

   runClient.bat

   - CLIENT_HOST : ip address of client
   - CLIENT_PORT : port number of client
   - SERVER_HOST : ip address of server (client port is assumed to be 2222)
   - LOSE_MESSAGE : Simulation of message loss from client

     runServer.bat

   - AT_MOST_ONCE : Whether to use at-most-once invocation. (at-least-once invocation is default)
   - DEBUG : Whether to run in debug mode (Not really in use)
   - LOSE_MESSAGE : Simulation of message loss from server

3. Run client or server by running runClient.bat or runServer.bat
