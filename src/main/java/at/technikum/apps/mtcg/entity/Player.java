package at.technikum.apps.mtcg.entity;

import java.util.List;

public class Player {

    private final String name;
    private Float hp;

    public Player(String name) {
        this.name = name;
        this.hp = (float)100;
    }

    public String getName() {
        return this.name;
    }

    public Float getHp() {
        return this.hp;
    }

    public void deductHp(Float dmgTaken) {
        this.hp = this.hp - dmgTaken;
    }

    public boolean isDead() {
        if(this.hp <= 0)
            return true;
        return false;
    }
}
