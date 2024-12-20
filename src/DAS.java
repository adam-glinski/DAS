import java.net.DatagramSocket;
import java.net.SocketException;

public class DAS {
    public static final int EXPECTED_NO_ARGS = 2;
    public static void main(String[] args) {
        if(args.length < EXPECTED_NO_ARGS) {
            System.err.println("missing params! usage: java DAS <port> <number>");
            return;
        }
        int port;
        int number;
        try {
            port = Integer.parseInt(args[0]);
            number = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("At least one of the params isn't a number.");
            return;
        }

        try(UDPManager manager = new UDPManager(port)) {
            Master.work(manager, number);
        } catch(SocketException e) {
            Slave.work(port, number);
        }
    }
}
