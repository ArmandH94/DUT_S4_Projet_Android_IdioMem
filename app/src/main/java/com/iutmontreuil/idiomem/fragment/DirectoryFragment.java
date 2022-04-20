package com.iutmontreuil.idiomem.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iutmontreuil.idiomem.adapter.ListAdapter;
import com.iutmontreuil.idiomem.adapter.SpinnerAdapter;
import com.iutmontreuil.idiomem.QuestionActivity;
import com.iutmontreuil.idiomem.R;
import com.iutmontreuil.idiomem.model.LanguageItem;
import com.iutmontreuil.idiomem.model.WordActions;
import com.iutmontreuil.idiomem.model.WordTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.widget.GridLayout.HORIZONTAL;


public class DirectoryFragment extends Fragment implements ListAdapter.OnCardListener, AdapterView.OnItemSelectedListener {

    private WordActions wordActions;
    private RecyclerView recyclerView;
    static ArrayList<WordTable> wordTableArrayList;
    private ArrayList<WordTable> wordTableArrayListTemp; //Depends on currentForeignLang
    private ArrayList<LanguageItem> languageItems;
    private ListAdapter listAdapter;
    private Button frenchListButton, foreignListButton;
    private Spinner languageSpinner;
    private String currentForeignLang;
    private Boolean isFrench;
    private final String MSG = "choisir une langue".toUpperCase();
    private InputMethodManager imm;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_directory, container, false);

        imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(container.getWindowToken(), 0);

        isFrench = true;
        currentForeignLang = "";
        wordActions = new WordActions(this.getContext());

        recyclerView = root.findViewById(R.id.recycler_view);
        frenchListButton = root.findViewById(R.id.frenchListButton);
        foreignListButton = root.findViewById(R.id.foreignListButton);
        languageSpinner = root.findViewById(R.id.languageSpinner);

        foreignListButton.setText(currentForeignLang);
        frenchListButton.setTextColor(Color.rgb(245, 212, 49));

        wordTableArrayList = new ArrayList<>();
        wordTableArrayListTemp = new ArrayList<>();

        //WordCard RecyclerView
        DividerItemDecoration itemDecor = new DividerItemDecoration(Objects.requireNonNull(recyclerView.getContext()), HORIZONTAL);
        recyclerView.addItemDecoration(itemDecor);
        listAdapter = new ListAdapter(this.getContext(), wordTableArrayListTemp, this.getActivity(), this, "Français", currentForeignLang);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        //Create spinner items
        initLanguageList();

        //Spinner settings
        languageSpinner.setOnItemSelectedListener(this);
        SpinnerAdapter dataAdapter = new SpinnerAdapter(getContext(),languageItems);
        languageSpinner.setAdapter(dataAdapter);

        frenchListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listAdapter.setLanguage("Français");
                recyclerView.setAdapter(listAdapter);
                frenchListButton.setTextColor(Color.rgb(245, 212, 49));
                foreignListButton.setTextColor(Color.rgb(255,255,255));
                isFrench = true;
            }
        });

        foreignListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currentForeignLang.isEmpty()){
                    listAdapter.setLanguage(currentForeignLang);
                    recyclerView.setAdapter(listAdapter);
                    foreignListButton.setTextColor(Color.rgb(245, 212, 49));
                    frenchListButton.setTextColor(Color.rgb(255,255,255));
                    isFrench = false;
                }
            }
        });

        return root;
    }

    /*
        Populates all words in wordList and in wordListTemp
     */
    private void loadWords() {
        wordActions.getLiveWords().observe(getViewLifecycleOwner() , new Observer<List<WordTable>>() {
            @Override
            public void onChanged(List<WordTable> wordTables) {
                wordTableArrayList.clear();
                wordTableArrayListTemp.clear();
                Log.e("length wordTables", wordTables.size()+"");
                for(WordTable word : wordTables) {
                    WordTable wordData = new WordTable();
                    wordData.setIdWord(word.getIdWord());
                    wordData.setWordFr(word.getWordFr());
                    wordData.setTranslation(word.getTranslation());
                    wordData.setForeignLang(word.getForeignLang());

                    wordTableArrayList.add(0,wordData);
                    if(!currentForeignLang.equals("")){
                        if (word.getForeignLang().equals(currentForeignLang)){
                            wordTableArrayListTemp.add(0,wordData);
                            listAdapter.notifyItemChanged(0);
                        }
                    }
                }
                Log.e("length wordTable", "onChanged: "+wordTableArrayList.size());
                Log.e("length wordTableTemp", "onChanged: "+wordTableArrayListTemp.size());
            }
        });
    }

    /*
        Launch Question Activity on CardClick
     */
    @Override
    public void onCardClick(int position) {
        Intent questionActivity = new Intent(this.getContext(), QuestionActivity.class);
        questionActivity.putExtra("MODE",isFrench);
        questionActivity.putExtra("WORDFR",wordTableArrayListTemp.get(position).getWordFr());
        questionActivity.putExtra("TRANSLATION",wordTableArrayListTemp.get(position).getTranslation());
        Objects.requireNonNull(this.getActivity()).startActivity(questionActivity);
    }

    /*
        Spinner item action on selected : Set text button, change language, reupdate wordList
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LanguageItem clickedItem = (LanguageItem) parent.getItemAtPosition(position);
        String language = clickedItem.getLanguageName();
        if(!language.equals(MSG)){
            this.currentForeignLang = language;
            foreignListButton.setText(language);
            foreignListButton.setVisibility(View.VISIBLE);
            frenchListButton.setVisibility(View.VISIBLE);
        }
        else {
            this.currentForeignLang = "";
            foreignListButton.setText("");
            foreignListButton.setVisibility(View.GONE);
            frenchListButton.setVisibility(View.GONE);
        }

        wordTableArrayList.clear();
        wordTableArrayListTemp.clear();
        listAdapter.setForeignLang(currentForeignLang);
        recyclerView.setAdapter(listAdapter);
        loadWords();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void initLanguageList(){
        languageItems = new ArrayList<>();
        languageItems.add(new LanguageItem(MSG.toUpperCase(),R.drawable.ic_spinner_down));
        languageItems.add(new LanguageItem("anglais".toUpperCase(),R.mipmap.ic_flag_en));
        languageItems.add(new LanguageItem("roumain".toUpperCase(),R.mipmap.ic_flag_ro));
    }

    public static ArrayList<WordTable> getWordTableArrayList() {
        return wordTableArrayList;
    }


}
