import java.awt.Color;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class NetClient extends JFrame implements KeyListener {
    final String serverIP = "127.0.0.1";
    final int serverPort;
    JTextArea textArea;
    JScrollPane scrollPane;
    BufferedReader in;
    PrintWriter out;

    NetClient() {
        super("Simple Chat Client");
        this.setSize(400, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.textArea = new JTextArea();
        this.textArea.setBackground(Color.BLACK);
        this.textArea.setForeground(Color.WHITE);
        this.textArea.setEditable(false);
        this.textArea.setMargin(new Insets(10, 10, 10, 10));
        this.scrollPane = new JScrollPane(this.textArea);
        this.add(this.scrollPane);
        this.textArea.addKeyListener(this);
        this.connect();
        serverPort = 1234;
    }

    void connect() {
        try {
            Socket socket = new Socket(serverIP, serverPort);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);

            // Поток для получения сообщений с сервера
            new Thread(this::run).start();
        } catch (IOException e) {
            this.textArea.setForeground(Color.RED);
            this.textArea.append("Server 127.0.0.1 port 1234 NOT AVAILABLE\n");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new NetClient().setVisible(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (c == '\n') {
            this.out.println();
        } else {
            this.out.print(c);
        }
        this.out.flush();
        addCharToTextArea(c);
    }

    void addCharToTextArea(char c) {
        this.textArea.append(String.valueOf(c));
        this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
    }

    private void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                this.textArea.append(line + "\n");
                this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
            }
        } catch (IOException e) {
            e.printStackTrace();
            addCharToTextArea('\n');
            this.textArea.append("CONNECTION ERROR\n");
        }
    }
}