package models;

public class Courier {
    private String login;
    private String password;
    private String firstName;
    
    public Courier(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }
    
    // Геттеры
    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
}
