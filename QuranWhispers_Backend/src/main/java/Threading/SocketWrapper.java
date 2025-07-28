package Threading;

import java.io.*;
import java.net.Socket;

public class SocketWrapper {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public SocketWrapper(Socket s) throws IOException {
        this.socket = s;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        //oos = new ObjectOutputStream(socket.getOutputStream());
        //ois = new ObjectInputStream(socket.getInputStream());
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
    public PrintWriter getWriter() {
        return writer;
    }

    public Socket getSocket() {
        return socket;
    }
}
