import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.Key;

public class ChatWindow extends JFrame {
    private JTextPane pane = new JTextPane();
    private JScrollPane chatArea = new JScrollPane(pane);
    private JTextField input = new JTextField();
    private GridBagConstraints constraints = new GridBagConstraints();
    private JButton send = new JButton("SEND");
    private Font mainFont = new Font("Comic Sans", Font.BOLD, 20);
    private Communicator communicator;
    private String ip;
    private int port;
    private boolean wait = true;
    private int stage = 0;
    ChatWindow(String title){
        super(title);

        this.setSize(500, 800);
        this.setLayout(new GridBagLayout());
        input.setFont(mainFont);
        chatArea.setFont(mainFont);
        pane.setFont(mainFont);
        pane.setEditable(false);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 0.95;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        this.add(chatArea, constraints);
        constraints.gridwidth = 1;
        constraints.weightx = 0.7;
        constraints.weighty = 0.05;
        constraints.gridy = 2;
        this.add(input, constraints);
        constraints.weightx = 0.3;
        constraints.gridx = 2;
        this.add(send, constraints);
        this.setVisible(true);
        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    switch (stage) {
                        case 0: getIp(); stage++; break;
                        case 1: getPort(); stage++; break;
                        default: send(); break;
                    }
                }
            }
        });
        setup();
        send.addActionListener(action -> send());
    }
    private void setup(){
        //what server should i connect to?
        send.addActionListener(action -> getIp());
        pane.setText("WRITE IN THE SERVER IP: ");
        while (wait){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(var listener: send.getActionListeners())
            send.removeActionListener(listener);
        wait = true;
        send.addActionListener(action -> getPort());
        pane.setText("WRITE IN THE SERVER PORT: ");
        while (wait){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(var listener: send.getActionListeners())
            send.removeActionListener(listener);

        communicator = new Communicator(pane, ip, port);
        new Thread(communicator).start();
    }
    private void send(){
        if(!input.getText().equals("")) {
            communicator.send(input.getText());
            input.setText("");
        }
    }
    private void getIp(){
        ip = input.getText();
        input.setText("");
        System.out.println(ip);
        wait = false;
    }
    private void getPort(){
        while (true) {
            try {
                port = Integer.valueOf(input.getText());
                input.setText("");
                break;
            }catch (NumberFormatException e){
                pane.setText("PORT HAS TO BE A NUMBER");
            }
        }
        wait = false;
    }
}
