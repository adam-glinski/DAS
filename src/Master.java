import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Master {
    public static final int BUFFER_SIZE = 10;
    public static final String BROADCAST_ADDRESS = "192.168.1.255";
    private static final List<Integer> receivedNums = new ArrayList<>();

    static void getBroadcastingAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
                networkInterface.getInterfaceAddresses().stream()
                        .filter(interfaceAddress -> interfaceAddress.getBroadcast() != null) // Ensure it has a broadcast address
                        .forEach(interfaceAddress -> {
                            InetAddress broadcast = interfaceAddress.getBroadcast();
                            System.out.println("Broadcast Address: " + broadcast.getHostAddress());
                        });
            }
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void work(DatagramSocket socket, int startNumber) {
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
                        broadcastToLan(socket, (int)average);
                        break;
                    case -1:
                        System.out.println(recvNum);
                        broadcastToLan(socket, recvNum);
                        socket.close();
                        return;
                    default:
                        receivedNums.add(recvNum);
                        System.out.println(recvNum);
                        break;
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static <T> void broadcastToLan(DatagramSocket socket, T value) {
        try {
            socket.setBroadcast(true);
            byte[] bytes = String.valueOf(value).getBytes();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
                    InetAddress.getByName(BROADCAST_ADDRESS), socket.getLocalPort());
            socket.send(packet);
            socket.setBroadcast(false);
        } catch (SocketException e) {
            System.err.println("Failed to set broadcast mode!");
            System.out.println(e.getMessage());
        } catch (UnknownHostException e) {
            System.err.println("Failed to find the subnet!");
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.err.println("Failed to send broadcast packet");
        }

    }
}
