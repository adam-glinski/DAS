import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Master {
    public static final int BUFFER_SIZE = 10;
    private static final List<Integer> receivedNums = new ArrayList<>();
    public static final String BROADCAST_ADDRESS = "192.168.1.255";

    public Master(DatagramSocket socket, int startNumber) {
        receivedNums.add(startNumber);
        while(true) {
            try {
                byte[] buf = new byte[BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buf, BUFFER_SIZE);
                socket.receive(packet);

                int recvNum = Integer.parseInt(new String(packet.getData(), 0, packet.getLength()));
                switch(recvNum){
                    case 0://TODO: Co w przypadku gdy lista jeste pusta?
                        double average = receivedNums.stream()
                                .filter(n -> n != 0).mapToInt(Integer::intValue).average().orElse(0.0);
                        System.out.println(average);
                        sendToLan(socket, (int)average); //TODO: Should we send int or double
                        break;
                    case -1:
                        System.out.println("Got -1!");
                        return;
                    default:
                        receivedNums.add(recvNum);
                        System.out.println(recvNum);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static <T> void sendToLan(DatagramSocket socket, T value) {
        try {
            socket.setBroadcast(true);
            byte[] bytes = String.valueOf(value).getBytes();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
                    InetAddress.getByName(BROADCAST_ADDRESS), socket.getLocalPort());
            socket.send(packet);
            socket.setBroadcast(false);
        } catch (SocketException e) {
            System.err.println("Failed to set broadcast mode!");
            e.printStackTrace();
        } catch (UnknownHostException e) {
            System.err.println("Failed to find the subnet!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Failed to send broadcast packet");
        }

    }
}
