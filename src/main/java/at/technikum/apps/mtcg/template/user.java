package at.technikum.apps.mtcg.template;

import com.fasterxml.jackson.annotation.JsonProperty;

public class user {
    @JsonProperty("Username")
    private String Username;

    @JsonProperty("Password")
    private String Password;

    public user() {
    }

    public user(String username, String password) {
        this.Username = username;
        this.Password = password;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

}
