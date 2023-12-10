package at.technikum.apps.template;

import com.fasterxml.jackson.annotation.JsonProperty;

public class user {
    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    public user() {
        // Default no-argument constructor
    }

    public user(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
