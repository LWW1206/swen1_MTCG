package at.technikum.apps.mtcg.template;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class card {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Damage")
    private Float damage;

    @JsonIgnore
    private String elementType;

    @JsonIgnore
    private Boolean monsterBoolean;

    public card() {

    }

    public card(String cardData) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            card singleCard = objectMapper.readValue(cardData, card.class);
            this.id = singleCard.getId();
            this.name = singleCard.getName();
            this.damage = singleCard.getDamage();
            isMonster(this.name);
            setElementType(this.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public Float getDamage() {
        return damage;
    }

    public void isMonster(String name) {
        if(name.contains("Spell"))
            this.monsterBoolean = false;
        else {
            this.monsterBoolean = true;
        }
    }
    public void setElementType(String name) {
        if(name.contains("Water"))
            this.elementType = "Water";
        else if(name.contains("Fire"))
            this.elementType = "Fire";
        else
            this.elementType = "Regular";
    }

    public boolean getMonsterType() {
        return monsterBoolean;
    }

    public String getElementType() {
        return elementType;
    }
}
