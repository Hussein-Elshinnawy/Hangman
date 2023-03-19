import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<ClientHandler> clients;
    private static boolean logged=false;

    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler> clients) throws IOException {
        this.client = clientSocket;
        this.clients = clients;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
        this.logged=false;
    }

    @Override
    public void run() {

        try {
            while (true) {
                String request = in.readLine();
                
                if ((request.equals("login") || request.equals("1"))&& logged ==false) {
                    out.println("--login--");
                    out.println("please enter username and password");
                    String usernameNpassword=in.readLine();
                    // out.println("please enter password");
                    // String password= in.readLine();
                    //System.out.println(usernameNpassword);
                    String response=GameServer.login(usernameNpassword);
                    if(response.equals("ok")){
                        logged=true;
                        out.println("success");
                    }else if(response.equals("401")){
                        out.println("401 error");
                    }else if(response.equals("404")){
                        out.println("404 error");
                    }

                } else if(((request.equals("register") || request.equals("2"))&& logged ==false)){
                    out.println("--register--");
                    out.println("please enter name, username and passord");
                    String nameNusernameNpassword=in.readLine();
                    if(GameServer.register(nameNusernameNpassword)){
                        out.println("success");
                        logged=true;
                    }else{
                        out.println("user alleardy exits");
                    }
                
                    continue;
                
                }else if(logged==true){
                    System.out.println("successfully logged in");

                }else{
                    out.println("error in login or register");
                }
                // if (request.contains("name")) {
                // out.println(GameServer.getRandomName());
                // } else if (request.startsWith("say")) {
                // int firstSpace = request.indexOf(" ");
                // if(firstSpace != -1){

                // outToAll(request.substring(firstSpace+1));
                // }
                // } else {
                // out.println("type name");
                // }
            }
        } catch (IOException e) {
            System.err.println("IO exception in client handler");
            System.err.println(e.getStackTrace());
        } finally {
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private void outToAll(String msg) {
        for (ClientHandler aClient : clients) {
            aClient.out.println(msg);
        }
    }

}