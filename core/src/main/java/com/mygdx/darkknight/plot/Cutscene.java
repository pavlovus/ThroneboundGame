package com.mygdx.darkknight.plot;

public class Cutscene {
    private String speaker;
    private String text;
    private String textUkr;

    public Cutscene() {
        // обов'язковий для десеріалізації
    }

    public Cutscene(String speaker, String text, String textUkr) {
        this.speaker = speaker;
        this.text = text;
        this.textUkr = textUkr;
    }

    public String getSpeaker() { return speaker; }
    public void setSpeaker(String speaker) { this.speaker = speaker; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getUkrText() { return textUkr; }
    public void setUkrText(String text) { this.textUkr = text; }
}

