package at.technikum.apps.mtcg.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TradingDeal {
    @JsonProperty("Id")
    private String id;
    @JsonProperty("CardToTrade")
    private String cardToTrade;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("MinimumDamage")
    private Float minimumDamage;
    @JsonIgnore
    private String creator;

    public TradingDeal() {

    }

    public TradingDeal(String tradeId, String cardId, String type, Float minDamage, String creator) {
        this.id = tradeId;
        this.cardToTrade = cardId;
        this.type = type;
        this.minimumDamage = minDamage;
        this.creator = creator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCardToTrade() {
        return cardToTrade;
    }

    public void setCardToTrade(String cardToTrade) {
        this.cardToTrade = cardToTrade;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float getMinimumDamage() {
        return minimumDamage;
    }

    public void setMinimumDamage(Float minimumDamage) {
        this.minimumDamage = minimumDamage;
    }
    public void setCreator(String name) {
        this.creator = name;
    }
}
