public class Player {
    public String name;
    
    public String username;

    public String password;

    public char team;

   
    
    public void setName(String name) {
        this.name = name;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
     
    public Player() {
    }

    public Player(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    
}
