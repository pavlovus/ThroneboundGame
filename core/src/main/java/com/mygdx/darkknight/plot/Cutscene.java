package com.mygdx.darkknight.plot;

public class Cutscene {
    private String speaker;
    private String text;

    public Cutscene() {
        // обов'язковий для десеріалізації
    }

    public Cutscene(String speaker, String text) {
        this.speaker = speaker;
        this.text = text;
    }

    public String getSpeaker() { return speaker; }
    public void setSpeaker(String speaker) { this.speaker = speaker; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}

