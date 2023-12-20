package at.technikum.apps.mtcg.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    @JsonProperty("Username")
    private String Username;

    @JsonProperty("Password")
    private String Password;

    public User() {
    }

    public User(String username, String password) {
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
