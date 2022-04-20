package com.iutmontreuil.idiomem.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "words")
public class WordTable implements Parcelable{


    @PrimaryKey(autoGenerate = true)
    int idWord;

    @ColumnInfo(name = "wordFr")
    String wordFr;

    @ColumnInfo(name = "translation")
    String translation;

    @ColumnInfo(name = "foreignLang")
    String foreignLang;

    public WordTable(){

    }

    protected WordTable(Parcel in) {
        idWord = in.readInt();
        wordFr = in.readString();
        translation = in.readString();
        foreignLang = in.readString();
    }

    public static final Parcelable.Creator<WordTable> CREATOR = new Parcelable.Creator<WordTable>() {
        @Override
        public WordTable createFromParcel(Parcel in) {
            return new WordTable(in);
        }

        @Override
        public WordTable[] newArray(int size) {
            return new WordTable[size];
        }
    };

    public int getIdWord() {
        return idWord;
    }

    public void setIdWord(int idWord) {
        this.idWord = idWord;
    }

    public String getWordFr() {
        return wordFr;
    }

    public void setWordFr(String wordFr) {
        this.wordFr = wordFr;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

   public String getForeignLang() {
        return foreignLang;
    }

    public void setForeignLang(String foreignLang) {
        this.foreignLang = foreignLang;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idWord);
        dest.writeString(wordFr);
        dest.writeString(translation);
        dest.writeString(foreignLang);
    }
}