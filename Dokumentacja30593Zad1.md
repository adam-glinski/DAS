# Dokumentacja

```java
public void send(String msg) throws IOException {
        byte[] buffer = msg.getBytes();
        if (buffer.length > MAX_BUFFER_SIZE) throw new RuntimeException("Message too long");
        if (destPort == 0) throw new RuntimeException("Destport not set");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                InetAddress.getByName(destHost), destPort);
        socket.send(packet);
    }
```


