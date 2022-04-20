package com.iutmontreuil.idiomem.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WordQueryDao {

    @Query("SELECT * FROM words")
    LiveData<List<WordTable>> fetchAllWords();


    @Query("SELECT * FROM words")
    List<WordTable> getAllWordsList();

    @Insert
    Long insertTask(WordTable wordTable);

    @Query("SELECT idWord, wordFr, translation, foreignLang FROM words WHERE idWord=:idWord")
    LiveData<WordTable> getWord(int idWord);

    @Query("DELETE FROM words WHERE idWord=:idWord")
    void deleteSpecific(int idWord);

    @Query("DELETE FROM words WHERE 1=1")
    void deleteAll();

    @Update
    void updateWords(WordTable wordTable);
}
