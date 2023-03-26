import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    // private PrintWriter out1;
    private static ArrayList<ClientHandler> clients;// static was not written before
    private static boolean logged = false;
    private static int attemptsleft;
    private static int attemptsleftA;
    private static int attemptsleftB;
    private int winsSingleScore = 0;
    private int lossSingleScore = 0;
    private static int winsMultiScore = 0;
    private static int lossMultiScore = 0;
    private static String usernameNpassword = "";

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
                FileWriter writer = new FileWriter("history.txt", true);
                // attemptsleft = GameServer.getNumberOfAttempts();
                // out.println("number of theads");
                // out.println(clients.size());
                String request = in.readLine();

                if ((request.equals("login") || request.equals("1")) && logged == false) {
                    out.println("--login--");
                    out.println("please enter username and password");
                    usernameNpassword = in.readLine();
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
                    logged = false;
                    out.println(("successfully logged in"));

                    out.println(("1.singleplayer 2.multiplayer"));
                    String playOp = in.readLine();
                    if (playOp.equals("1") || playOp.equals("singleplayer")) {
                        Boolean playAgain = false;
                        do {
                            attemptsleft = GameServer.getNumberOfAttempts();
                            String wordGen = GameServer.generateWord();
                            String msg = wordGen + "\n" + GameServer.firstState();
                            out.println(msg);
                            GameServer.resetPlayerGuess();

                            out.println("please enter a guess");
                            while (GameServer.isAttempts(attemptsleft)) {
                                String baba = in.readLine();
                                // out.print(baba.isBlank());
                                if (!baba.equals("logout")) {
                                    // out.close();
                                    // in.close();

                                    // ArrayList<Character> playerGuesses = new ArrayList<>();
                                    Character playerGuesses = baba.charAt(0);
                                    // out.println(playerGuesses);
                                    String guess = GameServer.printWordState(playerGuesses);
                                    out.println(guess);
                                    if (!GameServer.charFound()) {
                                        attemptsleft--;
                                        out.println("number of attemptsleft = " + attemptsleft);
                                    }
                                    if (GameServer.checkWin(guess)) {

                                        break;
                                    }

                                    guess = null;

                                } else {
                                    // logged = false;
                                    break;
                                }
                            }
                            if (attemptsleft == 0) {
                                try {
                                    // FileWriter writer = new FileWriter("history.txt");
                                    FileReader reader = new FileReader("history.txt");
                                    BufferedReader bufferedReader = new BufferedReader(reader);
                                    lossSingleScore = 0;
                                    updateUserScore( lossSingleScore);

                                    bufferedReader.close();
                                    writer.close();

                                } catch (IOException e) {

                                    e.printStackTrace();
                                }
                                out.println("GAMEOVER YOU LOST \nyour are out of attempts the word was " + wordGen);
                            } else {

                              
                                winsSingleScore = 5;
                                updateUserScore( winsSingleScore);


                                out.println("YOU WON CONGRATURLATIONS");
                            }
                            GameServer.resetTemp();

                            out.println("do you want to play again y/n");
                            String yn = in.readLine();
                            if (yn.equals("y")) {
                                playAgain = true;
                                // winsSingleScore=0;
                            } else {
                                winsSingleScore = 0;
                                lossSingleScore = 0;
                                playAgain = false;
                            }
                        } while (playAgain);

                        // try {
                        //     writer = new FileWriter("history.txt", true);
                        //     FileReader reader = new FileReader("history.txt");
                        //     BufferedReader bufferedReader = new BufferedReader(reader);
                        //     if (bufferedReader.readLine() != null) {

                        //         writer.write("\r\n");
                        //     }

                        //     bufferedReader.close();
                        //     writer.close();

                        // } catch (IOException e) {

                        //     e.printStackTrace();
                        // }

                        //////////////////////////////////////////////////////////////////////////////////////////////////
                    } else if (playOp.equals("2") || playOp.equals("multiplayer")) {
                        
                        // if(once){
                        // once=false;

                        // out.print("1v1 or 2v2");
                        // String vs=in.readLine();
                        // if(vs.equals("1")){
                        // numberOfTeams=2;
                        // }else{
                        // numberOfTeams=4;
                        // }
                        // }

                        out.println("a.team b.team");

                        char team = (char) in.read();
                        if (GameServer.evenTeam() == "you are free choose your team") {
                            GameServer.setPlayerTeam(team, this);
                        } else if (GameServer.evenTeam() == "no space in team a your are team b") {

                            out.println("no space in team a your are team b");
                            GameServer.setPlayerTeam('b', this);
                        } else if (GameServer.evenTeam() == "no space in team b your are team a") {

                            out.print("no space in team b your are team a");
                            GameServer.setPlayerTeam('a', this);
                        }
                        out.println("before ="+usernameNpassword);
                        out.println("waiting for other players to join");
                        // out.println(GameServer.allTeamReady());

                        // String msg = GameServer.generateWord() + "\n" + GameServer.firstState();
                        // out.println(msg);

                        if (GameServer.once()) {
                            String temp = GameServer.generateWord();
                            String dashes = GameServer.firstState();
                            GameServer.setWord(temp);
                        }
                        GameServer.setNames(usernameNpassword);
                        while (true) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            if (GameServer.count == 4 /* || GameServer.count==2 */) {
                                break;
                            }
                        }
                        
                        String word = GameServer.getWord();
                        out.println("after ="+usernameNpassword);
                        out.println(word);
                        out.println(GameServer.dashes);
                        if (GameServer.whichTeam(this).equals("teamA")) {
                            out.println(GameServer.whichTeam(this));
                            attemptsleftA = GameServer.numberOfAttemptsA;

                            out.println("please enter a guess");
                            System.out.println("--> ");
                            String baba = in.readLine();/// bug
                            while (GameServer.isAttempts(attemptsleftA)) {

                                while (true) {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    if (GameServer.turn(GameServer.whichTeam(this))) {
                                        break;
                                    }
                                }
                                
                                System.out.println("--> ");
                                baba = in.readLine();
                                if (!baba.equals("logout")) {

                                    Character playerGuesses = baba.charAt(0);

                                    String guess = GameServer.teamAprintWordState(playerGuesses);
                                    out.println(guess);
                                    if (!GameServer.charFoundA()) {
                                        attemptsleftA--;
                                        out.println("number of attemptsleft = " + attemptsleftA);
                                    }
                                    if (GameServer.checkWinA(guess)) {

                                        break;
                                    }
                                    out.println(GameServer.order);
                                    GameServer.order = (++GameServer.order) % 4;
                                    out.println(GameServer.order);

                                    guess = null;

                                } else {
                                    // logged = false;
                                    break;
                                }
                                // GameServer.order=(++GameServer.order)%4;
                            }
                            if (attemptsleftA == 0) {

                                // writer.write(System.lineSeparator()); // add new line if there is existing
                                // content
                                // writer.write(Integer.toString(winsSingleScore));
                                // writer.write(" ");
                                // writer.write(Integer.toString(lossSingleScore));
                                // writer.write(" ");
                                // writer.write(Integer.toString(winsMultiScore));
                                // writer.write(" ");
                                // writer.write(Integer.toString(++lossMultiScore));
                                lossSingleScore = 0;
                                usernameNpassword=GameServer.getName();
                                ++GameServer.name;
                                GameServer.name=GameServer.name%4;
                                out.print(usernameNpassword);
                                updateUserScore( lossSingleScore);

                                out.println("GAMEOVER YOU LOST \nyour are out of attempts the word was " + word);
                            } else {

                                winsSingleScore = 5;
                                usernameNpassword=GameServer.getName();
                                ++GameServer.name;
                                GameServer.name=GameServer.name%4;
                                out.println(usernameNpassword);
                                updateUserScore( winsSingleScore);

                                // out.println("TEAM A WON CONGRATURLATIONS");
                                outToAll("TEAM A WON CONGRATURLATIONS");
                                //break;
                            }
                            break;

                        } else if (GameServer.whichTeam(this).equals("teamB")) {
                            out.println(GameServer.whichTeam(this));
                            attemptsleftB = GameServer.numberOfAttemptsB;

                            while (true) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                if (GameServer.turn(GameServer.whichTeam(this))) {
                                    break;
                                }
                            }

                            out.println("please enter a guess");
                            System.out.println("--> ");
                            String baba = in.readLine();
                            while (GameServer.isAttempts(attemptsleftB)) {

                                while (true) {
                                    try {
                                        Thread.sleep(5000);
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    if (GameServer.turn(GameServer.whichTeam(this))) {
                                        break;
                                    }
                                }
                                System.out.print("--> ");
                                baba = in.readLine();
                                if (!baba.equals("logout")) {

                                    Character playerGuesses = baba.charAt(0);

                                    String guess = GameServer.teamBprintWordState(playerGuesses);
                                    out.println(guess);
                                    if (!GameServer.charFoundB()) {
                                        attemptsleftB--;
                                        out.println("number of attemptsleft = " + attemptsleftB);
                                    }
                                    if (GameServer.checkWinA(guess)) {

                                        break;
                                    }
                                    out.println(GameServer.order);
                                    GameServer.order = (++GameServer.order) % 4;
                                    out.println(GameServer.order);

                                    guess = null;

                                } else {
                                    // logged = false;
                                    break;
                                }

                            }
                            if (attemptsleftB == 0) {

                                lossSingleScore = 0;
                                usernameNpassword=GameServer.getName();
                                ++GameServer.name;
                                GameServer.name=GameServer.name%4;
                                updateUserScore( lossSingleScore);
                                out.println("GAMEOVER YOU LOST \nyour are out of attempts the word was " + word);
                                
                            } else {
                                winsSingleScore = 5;
                                usernameNpassword=GameServer.getName();
                                ++GameServer.name;
                                GameServer.name=GameServer.name%4;
                                updateUserScore( winsSingleScore);
                                // out.println("TEAM B WON CONGRATURLATIONS");
                                outToAll("TEAM B WON CONGRATURLATIONS");
                                //break;
                            }
                            GameServer.resetTemp();
                            break;
                        }

                    }
                    // System.exit(0);
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

    public void outToAll(String msg) throws IOException {
        for (ClientHandler aClient : clients) {
            aClient.out.println(msg);
        }
        
    }

    // String line1;
    public void updateUserScore( int score) {

        String[] split = usernameNpassword.split(" ");
        String username = split[0];
        List<String> lines = new ArrayList<>();

        boolean found = false;
        try (BufferedReader reader = new BufferedReader(new FileReader("history.txt"))) {
            String line = reader.readLine();
            while (line != null) {
                String[] unp = line.split(" ");
                if (unp.length == 2 && unp[0].equals(username)) {
                    found = true;
                    lines.add(username + " " + score);
                } else {
                    lines.add(line);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!found) {
            lines.add(username + " " + score);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("history.txt"))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
