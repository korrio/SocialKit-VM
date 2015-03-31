package co.aquario.socialkit.event;

/**
 * Created by Mac on 3/3/15.
 */
public class RegisterEvent {
    private String name;
    private String username;
    private String password;
    private String email;
    private String gender;

    public RegisterEvent(String name, String username, String password, String email, String gender) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }
}
