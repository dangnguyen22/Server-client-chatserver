import java.io.PrintWriter;
import java.net.Socket;

/*
 * Fall 2022 COSC 20203
 * @author: Michael Nguyen
 * @credit: some codes are take from 
 *  Head First JAVA 2 ed  K Sierra and Bert Bates
 */
public class SockAndWriter {
    private Socket s;
    private PrintWriter pw;
    private String name;

    public SockAndWriter(String name, Socket socket, PrintWriter writer) {
        this.name = name;
        this.s = socket;
        this.pw = writer;
    }

    public Socket getS() {
        return s;
    }

    public PrintWriter getPw() {
        return pw;
    }

    public String getName() {
        return name;
    }

}