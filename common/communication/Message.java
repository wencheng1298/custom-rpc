package common.communication;

/*
    Message class is the object that is used for sending between client and server.
*/
public class Message implements Marshallable {
    public MessageHeader header;
    public Marshallable body;

    // ----------------------------Constructors-----------------------------
    public Message() {
    }

    public Message(MessageHeader header, Marshallable body) {
        this.header = header;
        this.body = body;
    }

    // ----------------------------Interface Methods-----------------------------
    public String[] getAllAttributes() {
        String[] attributes = { "header", "body" };
        return attributes;
    };

    public Object getAttribute(String attr) {
        switch (attr) {
            case "header":
                return this.header;
            case "body":
                return this.body;
            default:
                return null;
        }
    }

    public void setAttribute(String attr, Object obj) {
        switch (attr) {
            case "header":
                this.header = (MessageHeader) obj;
                break;
            case "body":
                this.body = (Marshallable) obj;
                break;
            default:
                System.out.println("No such attribute found");
                break;
        }
    };

    // ----------------------------Class Methods-----------------------------
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n{ \n");
        sb.append("\t header: " + this.header.toString() + "\n");
        sb.append("\t body: " + this.body.toString() + "\n");
        sb.append("}\n");
        return sb.toString();
    }
}
