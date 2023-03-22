import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    // private PrintWriter out1;
    private static ArrayList<ClientHandler> clients;//static was not written before
    private static boolean logged = false;


    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler> clients) throws IOException {
        this.client = clientSocket;
        this.clients = clients;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
        // out1 = new PrintWriter(client.getOutputStream(), true);
        this.logged = false;
    }

    @Override
    public void run() {

        try {
            while (true) {
                String request = in.readLine();
                


                if ((request.equals("login") || request.equals("1")) && logged == false) {
                    out.println("--login--");
                    out.println("please enter username and password");
                    String usernameNpassword = in.readLine();
                    String response = GameServer.login(usernameNpassword);
                    if (response.equals("ok")) {
                        logged = true;
                        // out.println("success");
                    } else if (response.equals("401")) {
                        out.println("401 error");
                    } else if (response.equals("404")) {
                        out.println("404 error");
                    } else {
                        out.println("no such user");
                    }

                } else if (((request.equals("register") || request.equals("2")) && logged == false)) {
                    out.println("--register--");
                    out.println("please enter name, username and passord");
                    String nameNusernameNpassword = in.readLine();
                    if (GameServer.register(nameNusernameNpassword)) {
                        // out.println("success");
                        logged = true;
                    } else {
                        out.println("user alleardy exits");

                    }

                    // continue;

                }
                if (logged) {
                    out.println(("successfully logged in"));

                    out.println(("1.singleplayer 2.multiplayer"));
                    String playOp=in.readLine();
                    if(playOp.equals("1")||playOp.equals("singleplayer"))
                    {


                    }else if(playOp.equals("2")||playOp.equals("multiplayer")){
                        out.println("a.team b.team");
                        char team=(char) in.read();
                    
                        GameServer.setPlayerTeam(team, this);
                        String msg=GameServer.generateWord();
                        outToAll(msg);

                    }
                    
                    // out.println(GameServer.generateWord());
                    // out.println(GameServer.firstState());
                    // String str= in.readLine();
                    // ArrayList<Character> playerGuesses = new ArrayList<>();
                    // playerGuesses.add(str.charAt(0));
                    // //out.println(playerGuesses);
                    // out.println(GameServer.printWordState(playerGuesses));

                    // // GameServer.printWordState(playerGuesses);
                    // // continue;

                } else {
                    out.println("Please login or register");
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
            // out1.close();
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