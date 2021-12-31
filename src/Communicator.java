import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Communicator implements Runnable{
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private JTextPane chatPane;
    private boolean disconnected = true;

    public Communicator(JTextPane pane, String address, int port) {
        chatPane = pane;
        connect(address, port);
        pane.setText("Connected\n");
    }
    public void send(String message){
        output.println(message);
        chatPane.setText(chatPane.getText() + "YOU: " + message + '\n');
    }
    private void connect(String address, int port){
        int counter = 0;
        try {
            while (disconnected) {
                try {
                    socket = new Socket(address, port);
                    output = new PrintWriter(socket.getOutputStream(), true);
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    chatPane.setText("Connected.");
                    disconnected = false;
                    break;
                } catch (SocketException e) {
                    chatPane.setText("Server offline [" + counter++ + "]");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
        }catch (IOException e){
            chatPane.setText("Error reconnecting.");
        }
    }
    @Override
    public void run() {
        while(true){
            if(input != null){
                try {
                    String receivedStr = input.readLine();
                    System.out.println("received message: " + receivedStr);
                    chatPane.setText(chatPane.getText() + receivedStr + '\n');
                }catch (SocketException e){
                    chatPane.setText("disconnected");
                    disconnected = true;
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
            if(disconnected) {
                connect(socket.getInetAddress().getHostAddress(), socket.getPort());
            }
        }
    }
}
