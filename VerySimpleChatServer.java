import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import javax.print.SimpleDoc;
/*
 * Fall 2022 COSC 20203
 * @author: Michael Nguyen
 * @credit: some codes are take from /**
 *  Head First JAVA 2 ed  K Sierra and Bert Bates
 */
import java.awt.*;

public class VerySimpleChatServer extends Frame {
    Hashtable<String, SockAndWriter> toHT;
    ArrayList clientOutputStreams;
    Label timeLabel = new Label("Date and Time ", Label.RIGHT);
    TextField timeField = new TextField("");
    Panel displayTime = new Panel(new GridLayout(1, 2));
    int year, month, day, hour, min, sec;
    String todayS, timeS, minS, secS;
    Calendar now;
    public TextArea result = new TextArea(40, 40);
    String to;
    SimpleChatClient s;
    Socket sock;
    String from;
    String content;
    boolean encrypted;
    String mes;

    public VerySimpleChatServer() {
        setLayout(new BorderLayout());
        setSize(600, 800);
        displayTime.add(timeLabel);
        displayTime.add(timeField);
        add(displayTime, BorderLayout.NORTH);
        add(result, BorderLayout.CENTER);
        setBackground(Color.orange);
        setVisible(true);

        s = new SimpleChatClient(this);
    }

    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;

        public ClientHandler(Socket clientSOcket) {
            try {
                sock = clientSOcket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("read " + message);
                    to = analyzeMess(message);

                    System.out.println("FROM:" + from);
                    if (to.equals("ALL")) {
                        tellEveryone(message);
                    } else {
                        tellSomeone(from, to, message);
                    }

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public String analyzeMess(String message) throws IOException {
            String element = "1234";
            // String to = "";
            PrintWriter writer = new PrintWriter(sock.getOutputStream());
            StringTokenizer st = new StringTokenizer(message);
            while (st.hasMoreElements()) {
                element = st.nextToken();

                if (element.equals("FROM")) {
                    from = st.nextToken();
                    SockAndWriter sw = new SockAndWriter(from, sock, writer);
                    toHT.put(from, sw);
                    System.out.println(toHT);
                    // result.append("FROM " + from + " ");
                }
                try {
                    if (element.equals("TO")) {
                        to = st.nextToken();
                        // result.append("TO " + to + " ");
                    }
                } catch (Exception e) {
                    to = "NULL";
                }

                if (element.equals("ENCRYPTED")) {
                    element = st.nextToken();
                    if (element.equals("TRUE")) {
                        encrypted = true;
                    } else if (element.equals("FALSE")) {
                        encrypted = false;
                    }
                }
                try {
                    if (element.equals("MESS")) {
                        mes = st.nextToken();
                    }
                } catch (Exception e) {
                    mes = "NULL";
                }

            }

            return to;
        }
    }

    public static void main(String[] args) {
        new VerySimpleChatServer().go();
    }

    public void go() {
        clientOutputStreams = new ArrayList();
        toHT = new Hashtable<>();
        try {

            ServerSocket serverSock = new ServerSocket(5000);
            while (true) {
                result.append("Server started on " + processTime(2) + "\n");
                Socket clientSocket = serverSock.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                System.out.println("got a connection");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String EncryptionM(String message, int offset) {
        byte b[] = message.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) (b[i] + offset);
        }
        String s = new String(b, StandardCharsets.UTF_8);
        // s = "Encryptedmess: " + s;
        return s;
    }

    public void tellEveryone(String message) {
        Iterator it = clientOutputStreams.iterator();
        result.append(message + " broadcasted on " + processTime(2) + "\n");
        if (encrypted == true) {
            mes = EncryptionM(mes, s.offset * -1);
            message = "FROM " + from + " TO " + to + " ENCRYPTED TRUE MESS " + mes;
        }

        while (it.hasNext()) {
            try {

                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void tellSomeone(String from, String to, String message) {

        try {

            result.append(message + " narrowcasted on " + processTime(2) + "\n");
            if (encrypted == true) {
                mes = EncryptionM(mes, s.offset * -1);
                message = "FROM " + from + " TO " + to + " ENCRYPTED TRUE MESS " + mes;
            }
            Socket sock = toHT.get(to).getS();
            String name = toHT.get(to).getName();
            PrintWriter writer = toHT.get(to).getPw();
            PrintWriter writer1 = toHT.get(from).getPw();
            System.out.println("Message to be sent to " + name);
            writer1.println(message);
            writer1.flush();
            writer.println(message);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String processTime(int option) {
        now = Calendar.getInstance();
        year = now.get(Calendar.YEAR);
        month = now.get(Calendar.MONTH) + 1;
        day = now.get(Calendar.DAY_OF_MONTH);
        hour = now.get(Calendar.HOUR);
        min = now.get(Calendar.MINUTE);
        sec = now.get(Calendar.SECOND);
        if (min < 10)
            minS = "0" + min;
        else
            minS = "" + min;
        if (sec < 10)
            secS = "0" + sec;
        else
            secS = "" + sec;
        todayS = month + " / " + day + " / " + year;
        timeS = hour + " : " + minS + " : " + secS;
        switch (option) {
            case (0):
                return todayS;
            case (1):
                return timeS;
            case (2):
                return todayS + " @ " + timeS;
        }
        return null; // should not get here
    }

}