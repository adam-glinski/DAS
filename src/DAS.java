import java.net.DatagramSocket;
import java.net.SocketException;

public class DAS {
    public static void main(String[] args) {
        if(args.length != 2) {
            System.err.println("missing params! usage: java DAS <port> <number>.");
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

        try(DatagramSocket socket = new DatagramSocket(port)) {
            Master.work(socket, number);
        } catch(SocketException e) {
            Slave.work(port, number);
        }
    }
}
