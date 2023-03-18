import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9090);
        
        ServerConnection serverConn= new ServerConnection(socket);

        //BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        new Thread(serverConn).start();

        while (true) {
            System.out.print(">");
            String command = keyboard.readLine();

            if(command.equals("quit")) break;
            out.println(command);

            // String serverResponse = input.readLine();
            // System.out.println("server says:"+serverResponse);
            //JOptionPane.showMessageDialog(null, serverResponse);
        }
        socket.close();
        System.exit(0);
    }
}
