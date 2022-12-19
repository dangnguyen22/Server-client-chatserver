

/**
 *  This program was taken as an example presented in
 *  Head First JAVA 2 ed  K Sierra and Bert Bates
 *  Some added modifications were done to allow its readability
 *  
 *  Assumes port 5000 for the connections
 *  Fall 2022
 */

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *  This client only posts data in the chat room but does not listen (retrieves)
 *  information
 * 
 */

public class DeafChatClient
	{ public  final boolean verbose = true;
    JTextField outgoing;
    PrintWriter writer;
    Socket sock;
    public static void main(String args[]) {
		System.out.println("Chat Service");
	    DeafChatClient client =  new DeafChatClient(); 
        client.go();
	    
		
	}
    public void go() {
        JFrame frame = new JFrame("Deaf Chat Client");
        JPanel mainPanel = new JPanel();
        outgoing = new JTextField(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        setUpNetworking();
        frame.setSize(400, 500);
        frame.setVisible(true);
        
    }
    
    private void setUpNetworking() {
        try {
            sock = new Socket("127.0.0.1", 5001);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("networking established");
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            try {
            	String message =  outgoing.getText();
            	System.out.println("Sending coded message => " + message);
                writer.println(message);
                writer.flush();
                
                
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }
   
   
}
