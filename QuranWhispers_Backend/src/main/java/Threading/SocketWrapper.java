package Threading;

import java.io.*;
import java.net.Socket;

public class SocketWrapper {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public SocketWrapper(Socket s) throws IOException {
        this.socket = s;
       // this.socket.setSoTimeout(100000); // change this
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public String read() throws IOException {
        return reader.readLine();
    }

    public void write(String msg) {
        writer.println(msg);
    }

    public void close() throws IOException {
        socket.close();
    }

    public Socket getSocket() {
        return socket;
    }
}
