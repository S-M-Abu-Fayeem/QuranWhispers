package Threading;

import java.io.*;
import java.net.Socket;

public class SocketWrapper {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public SocketWrapper(Socket s) throws IOException {
        this.socket = s;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    }

    public String read() throws IOException {
        return reader.readLine();
    }

    public void write(String msg) {
        writer.println(msg); // println() automatically adds newline + flushes
    }

    public void close() throws IOException {
        socket.close();
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public Socket getSocket() {
        return socket;
    }
}
