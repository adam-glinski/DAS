import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

public class DAS {
    public static void main(String[] args) {
        if(args.length != 2) {
            System.err.println("missing params! usage: java DAS <port> <number>.");
            return;
        }
        int port = -1;
        int number = -1;

        try {
            port = Integer.parseInt(args[0]);
            number = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("At least one of the params isn't a number.");
        }

        try(DatagramSocket socket = new DatagramSocket(port)) {
            new Master(socket, number);
        } catch(SocketException e) {
            new Slave(port, number);
        }
    }
}
