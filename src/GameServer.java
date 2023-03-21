import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {
    private static final int port = 9090;

    private static ArrayList<Player> players = new ArrayList<>();

    private static ArrayList<String> words = new ArrayList<>();

    private static ArrayList<Character> playerGuesses = new ArrayList<>();

    private static ArrayList<ClientHandler> clients = new ArrayList<>();

    private static ExecutorService pool = Executors.newFixedThreadPool(4);

    private static String word;

    private static ArrayList<ClientHandler> teamA = new ArrayList<>();

    private static ArrayList<ClientHandler> teamB = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(port);

        Scanner scanner = new Scanner(new File("hangmanwords.txt"));
        while (scanner.hasNext()) {
            words.add(scanner.nextLine());
        }

        // try {
        //     File myObj = new File("gameConfiguration.txt");
        //     if (myObj.createNewFile()) {
        //         System.out.println("history created: " + myObj.getName());

        //     } else {
        //         System.out.println("File already exists.");
        //     }
        // } catch (IOException e) {
        //     System.out.println("An error occurred.");
        //     e.printStackTrace();
        // }

        while (true) {
            System.out.println("server is waiting for client");
            Socket client = listener.accept();
            System.out.println("client is connected");
            ClientHandler clientThread = new ClientHandler(client, clients);
            clients.add(clientThread);
            pool.execute(clientThread);
        }

        // PrintWriter out = new PrintWriter(client.getOutputStream(), true);// to send
        // to client
        // BufferedReader in = new BufferedReader(new
        // InputStreamReader(client.getInputStream()));// to read from client

        // System.out.println("server is closing");
        // listener.close();
        // client.close();
    }

    public static String login(String usernameNpassword) {
        boolean usernameFound = false;
        boolean passwordFound = false;
        String[] arr = usernameNpassword.split(" ");// 0-user 1-pass
        for (Player aPlayer : players) {
            if (aPlayer.username.equals(arr[0]) && aPlayer.password.equals(arr[1])) {
                usernameFound = true;
                passwordFound = true;
            }
        }
        for (Player aPlayer : players) {
            if (aPlayer.username.equals(arr[0])) {
                usernameFound = true;
            }
        }
        for (Player aPlayer : players) {
            if (aPlayer.password.equals(arr[1])) {
                passwordFound = true;
            }
        }
        if (usernameFound && passwordFound) {
            return "ok";
        }

        if (!usernameFound) {
            return "404";
        }
        if (!passwordFound) {
            return "401";
        }
        return null;

    }

    public static boolean register(String nameNusernameNpassword) {
        String[] arr = nameNusernameNpassword.split(" ");// 0-

        for (Player aPlayer : players) {

            if (aPlayer.username.equals(arr[1])) {//

                return false;
            }
        }
        Player p = new Player(arr[0], arr[1], arr[2]);
        players.add(p);

        try {
            File myObj = new File("user" + players.size() + "config.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
                FileWriter myWriter = new FileWriter(myObj.getName());
                // myWriter.write(arr[0]);
                // myWriter.write("\r\n");
                myWriter.write(arr[1]);
                myWriter.write("\r\n");
                myWriter.write(arr[2]);
                myWriter.write("\r\n");
                myWriter.close();
                System.out.println("Successfully wrote to the file.");

            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            File myObj = new File("user" + players.size() + "history.txt");
            if (myObj.createNewFile()) {
                System.out.println("history created: " + myObj.getName());

            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return true;

    }

    public static String generateWord() {
        Random random = new Random();
        word = words.get(random.nextInt(words.size()));
        return word;
    }

    public static String firstState() {
        ArrayList<Character> temp = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            temp.add('-');
            // System.out.print("*");
        }
        StringBuilder builder = new StringBuilder(word.length());
        for (Character ch : temp) {
            builder.append(ch);
        }
        return builder.toString();
    }

    public static String printWordState(ArrayList<Character> playerGuesses) {// b
        ArrayList<Character> temp = new ArrayList<>();// b------
        for (int i = 0; i < word.length(); i++) {// border
            if (playerGuesses.contains(word.charAt(i))) {//
                temp.add(word.charAt(i));
                // System.out.print(word.charAt(i));
            } else {
                temp.add('-');
                // System.out.print("-");
            }
            System.out.print("");
        }
        StringBuilder builder = new StringBuilder(word.length());
        for (Character ch : temp) {
            builder.append(ch);
        }
        System.out.println(builder.toString());
        return builder.toString();

    }
    public static void setPlayerTeam(char team, ClientHandler c){
        if(team=='a'){
            teamA.add(c);
            System.out.println(c.toString()+" has joined team a");
        }else if(team=='b'){
            teamB.add(c);
            System.out.println(c.toString()+" has joined team b");
        }
        
    }

}