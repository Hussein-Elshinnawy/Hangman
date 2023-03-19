import java.io.BufferedReader;
import java.io.File;
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

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(port);


        Scanner scanner = new Scanner(new File("D:/2.University/year 4/term 2/New folder/Hangman/hangmanwords.txt"));
        while (scanner.hasNext()) {
            words.add(scanner.nextLine());
        }
        Random random = new Random();

        String word = words.get(random.nextInt(words.size()));
        System.out.println(word);



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
        return true;

    }

}