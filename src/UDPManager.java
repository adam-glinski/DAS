import java.io.IOException;
import java.net.*;

public class UDPManager implements AutoCloseable {
    public static final int MAX_BUFFER_SIZE = 10;
    private final DatagramSocket socket;
    private String destHost;
    private int destPort;
    private String lastRecvHost;
    private int lastRecvPort;
    public UDPManager(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }

    public void send(String msg) throws IOException {
        byte[] buffer = msg.getBytes();
        if (buffer.length > MAX_BUFFER_SIZE) throw new RuntimeException("Message too long");
        if (destPort == 0) throw new RuntimeException("Message too long");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                InetAddress.getByName(destHost), destPort);
        socket.send(packet);
    }

    public String receive() throws IOException {
        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        lastRecvHost = packet.getAddress().getHostAddress();
        lastRecvPort = packet.getPort();
        return new String(packet.getData(), 0, packet.getLength());
    }

    public void setBroadcast(boolean on) throws SocketException {
        socket.setBroadcast(on);
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void setDestHost(String destHost) {
        this.destHost = destHost;
    }

    public void setDestPort(int port) {
        this.destPort = port;
    }

    public String getRecvHost() {
        return lastRecvHost;
    }

    public int getRecvPort() {
        return lastRecvPort;
    }

    @Override
    public void close() {
        socket.close();
    }
}
