package at.technikum.apps.mtcg.template;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Card {

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

    public Card() {

    }

    public Card(String id, String name, Float damage, boolean monsterType, String elementType) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.monsterBoolean = monsterType;
        this.elementType = elementType;
    }

    public Card(String cardData) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Card singleCard = objectMapper.readValue(cardData, Card.class);
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
