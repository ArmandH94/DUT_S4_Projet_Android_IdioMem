package com.iutmontreuil.idiomem.model;

public class LanguageItem {
    private String languageName;
    private int flagImage;

    public LanguageItem(String languageName, int flagImage) {
        this.languageName = languageName;
        this.flagImage = flagImage;
    }

    public String getLanguageName() {
        return languageName;
    }

    public int getFlagImage() {
        return flagImage;
    }
}
