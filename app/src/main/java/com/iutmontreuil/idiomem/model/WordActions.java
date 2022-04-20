package com.iutmontreuil.idiomem.model;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class WordActions {

    private String DB_NAME = "words";


    private AppDatabase wordDB;

    public WordActions(Context context) {
        wordDB = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
    }

    public LiveData<List<WordTable>> getLiveWords() {
        return wordDB.queryDao().fetchAllWords();
    }



    public List<WordTable> getAllWords() throws ExecutionException, InterruptedException {
        return new getAllAsyncTask(wordDB).execute().get();
    }

    private static class getAllAsyncTask extends android.os.AsyncTask<Void, Void, List<WordTable>> {

        private AppDatabase wordDB;

        getAllAsyncTask(AppDatabase dao) {
            wordDB = dao;
        }

        @Override
        protected List<WordTable> doInBackground(Void... voids) {
            return wordDB.queryDao().getAllWordsList();
        }
    }


    public void insertTask(String wordFr, String translation, String foreignLang) {

        WordTable word = new WordTable();
        word.setWordFr(wordFr);
        word.setTranslation(translation);
        word.setForeignLang(foreignLang);
        insert(word);
    }

    public void insert(final WordTable word) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                wordDB.queryDao().insertTask(word);
                return null;
            }
        }.execute();
    }

    public void delete(final int idWord) {
        final LiveData<WordTable> word = getWord(idWord);
        if (word != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    wordDB.queryDao().deleteSpecific(idWord);
                    return null;
                }
            }.execute();
        }
    }

    public void update(final WordTable wordData) {
        final LiveData<WordTable> word = getWord(wordData.getIdWord());
        if (word != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    wordDB.queryDao().updateWords(wordData);
                    return null;
                }
            }.execute();
        }
    }

    public void deleteAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                wordDB.queryDao().deleteAll();
                return null;
            }
        }.execute();
    }

    public LiveData<WordTable> getWord(int idWord) {
        return wordDB.queryDao().getWord(idWord);
    }


}
