package com.ihm.shifumeredahibba;

import java.util.HashMap;
import java.util.Map;


public enum Action {
    PAPER(1),
    ROCK(2),
    SCISSOR(3),
    REPLAY(4),
    DISCONNECT(5);

    private int value;
    private static Map map = new HashMap();

    Action(int value) {
        this.value = value;
    }

    static {
        for (Action pageType : Action.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static Action valueOf(int pageType) {
        return (Action) map.get(pageType);
    }

    public int getValue() {
        return value;
    }

    public String getStringValue(){
        switch (this){
            case ROCK: return "Rock";
            case PAPER: return "Paper";
            case SCISSOR: return "Scissor";
        }
        return null;
    }

}
