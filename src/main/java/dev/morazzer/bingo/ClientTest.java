package dev.morazzer.bingo;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClientTest {

    /**
     * Small test client for simple connection testing
     * @param args
     */
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("bsn.morazzer.dev", 1807);
            while (true) {

                System.out.print("Awaiting message...\r");
                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int read = inputStream.read(buffer);
                if (read == -1) {
                    System.out.println("Connection closed");
                    break;
                }
                String message = new String(buffer, 0, read);
                System.out.println("Message: " + message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
