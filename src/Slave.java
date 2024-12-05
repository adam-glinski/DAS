import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Slave {
    private static boolean foundPort = false;
    public static final int MIN_PORT = 0x400;
    public static final int MAX_PORT = 0xFFFF;

    public static void work(int destPort, int numberToSend) {
        int port = MIN_PORT;
        while (!foundPort) {
            try (UDPManager manager = new UDPManager(port)) {
                foundPort = true;
                manager.setDestHost("localhost");
                manager.setDestPort(destPort);
                manager.send(Integer.toString(numberToSend));
            } catch (SocketException e) {
                System.out.println("Failed to open port " + port + ". Looking for a new one...");
                port = MIN_PORT + (int)(Math.random() * ((MAX_PORT - MIN_PORT) + 1));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
