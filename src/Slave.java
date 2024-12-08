
import java.io.IOException;
import java.net.SocketException;

public class Slave {
    private static boolean foundPort = false;
    public static final int MIN_PORT = 0x400;
    public static final int MAX_PORT = 0xFFFF;

    public static void work(int destPort, int numberToSend) {
        int port = MIN_PORT;
        int maxRetry = 5;
        while (!foundPort) {
            try (UDPManager manager = new UDPManager(port)) {
                foundPort = true;
                for(int retryCounter = 0; retryCounter < maxRetry; retryCounter++) {
                    manager.setDestHost("localhost");
                    manager.setDestPort(destPort);
                    manager.send(Integer.toString(numberToSend));
                    manager.getSocket().setSoTimeout(2000);

                    String resp = manager.receive();
                    if (resp.equals("OK")) break;
                }
                } catch (SocketException e) {
                System.out.println("Failed to open port " + port + ". Looking for a new one...");
                port = MIN_PORT + (int)(Math.random() * ((MAX_PORT - MIN_PORT) + 1));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
