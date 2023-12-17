package at.technikum.apps.mtcg.template;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserData {
    @JsonProperty("Name")
    private String name;

    @JsonProperty("Bio")
    private String bio;

    @JsonProperty("Image")
    private String image;

    public UserData() {
    }

    public UserData(String name, String bio, String image) {
        this.name = name;
        this.bio = bio;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getImage() { return image; }


}
