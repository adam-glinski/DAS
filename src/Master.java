import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Master {
    public static final int BUFFER_SIZE = 10;
    public static String broadcastAddress;
    private static final List<Integer> receivedNums = new ArrayList<>();

    static public void setBroadcastingAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp() || networkInterface.isVirtual()) continue;
                for(InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = address.getBroadcast();
                    if(broadcast == null) continue;
                    broadcastAddress = broadcast.getHostAddress();
                    System.out.println("Broadcast Address: " + broadcast.getHostAddress());
                    return;
                }
            }
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void work(UDPManager manager, int startNumber) {
        setBroadcastingAddress();
        receivedNums.add(startNumber);
        while(true) {
            try {
                int recvNum = Integer.parseInt(manager.receive());
                manager.setDestHost(manager.getRecvHost());
                switch(recvNum){
                    case 0://TODO: Co w przypadku gdy lista jeste pusta?
                        double average = receivedNums.stream()
                                .filter(n -> n != 0).mapToInt(Integer::intValue).average().orElse(0.0);
                        broadcastToLan(manager, (int)average);
                        break;
                    case -1:
                        System.out.println(recvNum);
                        broadcastToLan(manager, recvNum);
                        manager.close();
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

    private static <T> void broadcastToLan(UDPManager manager, T value) {
        try {
            manager.setBroadcast(true);
            manager.setDestHost(broadcastAddress);
            manager.setDestPort(manager.getSocket().getLocalPort()); // port master-a
            manager.send(String.valueOf(value));
            manager.setBroadcast(false);
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
