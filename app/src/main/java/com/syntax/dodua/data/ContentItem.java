package com.syntax.dodua.data;

import java.io.Serializable;

public class ContentItem implements Serializable {
    public final String id;
    public final String type;
    public final String category;
    public final String title;
    public final String arabic;
    public final String transliteration;
    public final String translation;
    public final String notes;
    public final String benefits;
    public final String source;
    public final int targetCount;

    public ContentItem(String id, String type, String category, String title, String arabic,
                       String transliteration, String translation, String notes, String benefits,
                       String source, int targetCount) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.title = title;
        this.arabic = arabic;
        this.transliteration = transliteration;
        this.translation = translation;
        this.notes = notes;
        this.benefits = benefits;
        this.source = source;
        this.targetCount = targetCount;
    }
}
