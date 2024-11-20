import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Slave {
    private static boolean foundPort = false;
    public static final int MIN_PORT = 0x400;
    public static final int MAX_PORT = 0xFFFF;

    public Slave(int masterPort, int numberToSend) {
        int port = MIN_PORT;
        while (!foundPort) {
            try (DatagramSocket socket = new DatagramSocket(port)){
                foundPort = true;
                byte[] buffer = Integer.toString(numberToSend).getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("localhost"), masterPort);
                socket.send(packet);
            } catch (SocketException e) {
                System.out.println("Failed to open port: " + port);
                port = MIN_PORT + (int)(Math.random() * ((MAX_PORT - MIN_PORT) + 1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
