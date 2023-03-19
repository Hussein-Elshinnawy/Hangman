import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

//import javax.swing.JOptionPane;

public class Client {
    

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9090);

        ServerConnection serverConn = new ServerConnection(socket);

        // BufferedReader input = new BufferedReader(new
        // InputStreamReader(socket.getInputStream()));
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        
        new Thread(serverConn).start();

        System.out.println("welcome to hangman please choose 1.login 2.register");
        

        while (true) {
            System.out.println(">");
            String command = keyboard.readLine();
          
            if (command.equals("-"))
                break;
            out.println(command);

            // String serverResponse = input.readLine();
            // System.out.println("server says:"+serverResponse);
            // JOptionPane.showMessageDialog(null, serverResponse);
        }
        socket.close();
        System.exit(0);
    }

 
}
