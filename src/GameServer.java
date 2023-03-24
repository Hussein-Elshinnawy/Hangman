import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {
    private static final int port = 9090;

    // private static ArrayList<Player> players = new ArrayList<>();

    private static ArrayList<String> words = new ArrayList<>();

    private static ArrayList<Character> playerGuesses = new ArrayList<>();

    private static ArrayList<ClientHandler> clients = new ArrayList<>();

    private static ExecutorService pool = Executors.newFixedThreadPool(4);

    private static String word;

    private static ArrayList<ClientHandler> teamA = new ArrayList<>();

    private static ArrayList<ClientHandler> teamB = new ArrayList<>();

    private static boolean first = true;

    private static ArrayList<Character> temp = new ArrayList<>();

    private static int numberOfAttempts;

    private static int minNumTeams;

    private static int maxNumTeams;

    public static boolean flag;

    // private static ArrayList<Character> playerguess = new ArrayList<>();

    
    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(port);

        Scanner scanner = new Scanner(new File("hangmanwords.txt"));
        while (scanner.hasNext()) {
            words.add(scanner.nextLine());
        }
        scanner.close();


        try {

            BufferedReader buff = new BufferedReader(new FileReader("gameConfiguration.txt"));
            String line;

            while ((line = buff.readLine()) != null) {
                String[] split = line.split(" ");

                numberOfAttempts = Integer.parseInt(split[0]);

                minNumTeams = Integer.parseInt(split[1]);

                maxNumTeams = Integer.parseInt(split[2]);
            }

        } catch (IOException e) {
            System.out.println("Error reading file " + "gameConfiguration.txt");
            e.printStackTrace();
        }

        // try {
        // File myObj = new File("gameConfiguration.txt");
        // if (myObj.createNewFile()) {
        // System.out.println("history created: " + myObj.getName());

        // } else {
        // System.out.println("File already exists.");
        // }
        // } catch (IOException e) {
        // System.out.println("An error occurred.");
        // e.printStackTrace();
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
        boolean wrongPassword = false;
        boolean wrongpUsername = false;
        String[] arr = usernameNpassword.split(" ");// 0-user 1-pass

        try {
            BufferedReader br = new BufferedReader(new FileReader("config.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                String[] split = line.split(" ");
                if (split[0].equals(arr[0]) && split[1].equals(arr[1])) {
                    usernameFound = true;
                    passwordFound = true;
                    break;
                } else if (split[0].equals(arr[0]) && !split[1].equals(arr[1])) {
                    wrongPassword = true;
                } else if (!split[0].equals(arr[0]) && split[1].equals(arr[1])) {
                    wrongpUsername = true;
                }
            }
            br.close();
            if (usernameFound && passwordFound) {
                return "ok";
            }

            if (wrongpUsername) {
                return "404";
            }
            if (wrongPassword) {
                return "401";
            }

        } catch (IOException e) {
            System.out.println("Error reading file " + "config.txt");
            e.printStackTrace();
        }
        return "no such user";

    }

    public static boolean register(String nameNusernameNpassword) {
        String[] arr = nameNusernameNpassword.split(" ");// 0-
        FileWriter myWriter;
        try {
            FileWriter fw = new FileWriter("config.txt", true); // true - enables appending mode
            fw.write(arr[1]);
            fw.write(" ");
            fw.write(arr[2]);
            fw.write("\r\n");
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // myWriter.write(arr[0]);
        // myWriter.write("\r\n");

        System.out.println("Successfully wrote to the file.");

        return true;

    }

    public static String generateWord() {
        Random random = new Random();
        word = words.get(random.nextInt(words.size()));
        return word;
    }

    public static String firstState() {

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

    public static String printWordState(Character Guesses) {// b
        // ArrayList<Character> temp = new ArrayList<>();// b------
        flag=false;
        for (int i = 0; i < word.length(); i++) {// border
            if (Character.toLowerCase(Guesses) == Character.toLowerCase(word.charAt(i))) {//
                // System.out.println("character "+ word.charAt(i));
                temp.set(i, word.charAt(i));
                flag=true;
                // System.out.println(temp.toString());
                // System.out.print(word.charAt(i));
            }
            // else {
            // temp.set(i, '-');
            // // System.out.print("-");
            // }
        }
        StringBuilder builder = new StringBuilder(word.length());
        for (Character ch : temp) {
            builder.append(ch);
        }
        System.out.println(builder.toString());
        return builder.toString();
        // return temp;

    }
    public static void resetTemp(){
        temp.clear();
    }

    public static void setPlayerTeam(char team, ClientHandler c) {
        if (team == 'a') {
            teamA.add(c);
            System.out.println(c.toString() + " has joined team a");
        } else if (team == 'b') {
            teamB.add(c);
            System.out.println(c.toString() + " has joined team b");
        }
    }

    public static String evenTeam() {
        if (teamA.size() > teamB.size() && teamA.size() == 2) {
            return "no space in team a your are team b";
        } else if (teamA.size() < teamB.size() && teamB.size() == 2) {
            return "no space in team b your are team a";
        }
        return "you are free choose your team";
    }

    public static boolean firstTime() {
        if (first) {
            first = false;
            return true;
        }
        return false;
    }

    public static boolean allTeamReady() {
        if (teamA.size() == 2 && teamB.size() == 2) {
            return true;
        }
        return false;
    }

    public static void resetPlayerGuess() {
        playerGuesses = null;
    }

    public static boolean isAttempts(int attemptsLeft) {
        if(attemptsLeft>0)
        {
            return true;
        }
        return false;
    }

    public static int getNumberOfAttempts() {
        return numberOfAttempts;
    }

    public static boolean charFound(){
        return flag;

    }

    public static boolean checkWin(String guess){
        if(guess.equals(word))
        {
           return true;
        }
        return false;
    }

  



}