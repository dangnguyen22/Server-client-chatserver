import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/*
 * Fall 2022 COSC 20203
 * @author: Michael Nguyen
 * @credit: some codes are take from 
 *  Head First JAVA 2 ed  K Sierra and Bert Bates
 */
public class SimpleChatClient {
    public final boolean verbose = true;
    JTextArea incoming;
    JTextField outgoing;
    BufferedReader reader;
    PrintWriter writer;
    Socket sock;
    JTextField name = new JTextField();
    JTextField to = new JTextField("ALL");
    JCheckBox encrypted = new JCheckBox("Encrypted?");
    int offset = 5;
    String todayS, timeS, minS, secS;
    Calendar now;
    int year, month, day, hour, min, sec;
    VerySimpleChatServer v;
    String message1;
    String message;

    public SimpleChatClient(VerySimpleChatServer fromV) {
        v = fromV;
    }

    public SimpleChatClient() {

    }

    public void go() {
        JFrame frame = new JFrame("Simple Chat Client");

        JPanel uPanel = new JPanel();
        uPanel.setLayout(new GridLayout(2, 2, 10, 10));
        uPanel.add(new JLabel("Name", SwingConstants.RIGHT));
        uPanel.add(name);
        uPanel.add(new JLabel("To", SwingConstants.RIGHT));
        uPanel.add(to);

        JPanel mainPanel = new JPanel();
        incoming = new JTextArea(15, 50);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);
        incoming.setText("Client logged on " + processTime(2) + "\n");
        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        outgoing = new JTextField(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());
        mainPanel.add(qScroller);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);

        mainPanel.add(encrypted);
        encrypted.setSelected(false);
        encrypted.addActionListener(new SendCheckboxListener());

        frame.add(uPanel, BorderLayout.NORTH);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        setUpNetworking();

        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();

        frame.setSize(650, 500);
        frame.setVisible(true);

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
            case (3):
                return secS;
        }
        return null; // should not get here
    }

    public class SendCheckboxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JCheckBox jcb = (JCheckBox) e.getSource();
        }

    }

    private void setUpNetworking() {
        try {
            sock = new Socket("127.0.0.1", 5000);
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("networking established");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            try {
                String mes = outgoing.getText();

                if (encrypted.isSelected()) {
                    mes = EncryptionM(mes, offset);
                    if (name.getText().isEmpty()) {
                        message = "FROM " + "NoName" + " TO " + to.getText() + " ENCRYPTED TRUE MESS " + mes;

                    } else {
                        message = "FROM " + name.getText() + " TO " + to.getText() + " ENCRYPTED TRUE MESS " + mes;
                    }

                } else {
                    if (name.getText().isEmpty()) {
                        message = "FROM " + "NoName" + " TO " + to.getText() + " ENCRYPTED FALSE MESS " + mes;

                    } else {
                        message = "FROM " + name.getText() + " TO " + to.getText() + " ENCRYPTED FALSE MESS " + mes;
                    }
                }
                if (verbose)
                    System.out.println("Sending coded message => " + message);
                writer.println(message);
                writer.flush();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }

        public String EncryptionM(String message, int offset) {
            byte b[] = message.getBytes(StandardCharsets.UTF_8);
            for (int i = 0; i < b.length; i++) {
                b[i] = (byte) (b[i] + offset);
            }
            String s = new String(b, StandardCharsets.UTF_8);
            // s = "Encryptedmess: " + s;
            return s;
        }
    }

    public static void main(String[] args) {
        new SimpleChatClient().go();
    }

    class IncomingReader implements Runnable {
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    // System.out.println("client read " + message);
                    incoming.append(message + "\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
    