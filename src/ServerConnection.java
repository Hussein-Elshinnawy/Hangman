import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection implements Runnable {
    private Socket server;
    private BufferedReader in;
    private String serverResponse;

    public ServerConnection(Socket s) throws IOException {
        server = s;
        in = new BufferedReader(new InputStreamReader(server.getInputStream()));
    }

    @Override
    public void run() {

        // String serverResponse;
        try {
            while (true) {
                serverResponse = in.readLine();
                if(serverResponse== null) break;
                if(serverResponse.equals("successfully logged in")){
                    System.out.println("HANGMAN GAME OMG");
                }
                

                System.out.println("server :" + serverResponse);
                serverResponse=null;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        // finally {
        //     try {
        //         in.close();
        //     } catch (IOException e) {
        //         // TODO Auto-generated catch block
        //         e.printStackTrace();
        //     }
        // }

    }

}