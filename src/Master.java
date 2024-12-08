import java.io.IOException;
import java.net.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Master {
    public static final int BUFFER_SIZE = 10;
    public static String broadcastAddress;
    public static boolean sentBroadcast = false;
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
                    // System.out.println("Broadcast Address: " + broadcast.getHostAddress());
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
                String recvString = manager.receive();
                if (recvString.equals("OK")) continue; // Skip any confirmations (master don't need them)
                int recvNum = Integer.parseInt(recvString);
                manager.setDestHost(manager.getRecvHost());
                manager.setDestPort(manager.getRecvPort());
                if(!sentBroadcast) manager.send("OK"); // Confirm that we got the number, except when we've sent the broadcast
                switch(recvNum){
                    case 0:
                        int average = (int)(receivedNums.stream()
                                .filter(n -> n != 0).mapToInt(Integer::intValue).average().orElse(0.0));
                        System.out.println(average);
                        sentBroadcast = true;
                        broadcastToLan(manager, average);
                        break;
                    case -1:
                        System.out.println(recvNum);
                        broadcastToLan(manager, recvNum);
                        sentBroadcast = true;
                        manager.close();
                        System.exit(0);
                    default:
                        if (!sentBroadcast) {
                            receivedNums.add(recvNum);
                            System.out.println(recvNum);
                        }
                        sentBroadcast = false;
                        manager.setDestPort(manager.getRecvPort());
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
