package com.iutmontreuil.idiomem.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.iutmontreuil.idiomem.adapter.SpinnerAdapter;
import com.iutmontreuil.idiomem.QuizActivity;
import com.iutmontreuil.idiomem.R;
import com.iutmontreuil.idiomem.model.LanguageItem;
import com.iutmontreuil.idiomem.model.WordTable;

import java.util.ArrayList;
import java.util.Objects;

public class QuizFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Button buttonQuizQuick;
    private Button buttonQuizPerso;
    private Spinner languageSpinner;
    private ArrayList<LanguageItem> languageItems;
    private String foreignLang;
    private TextView textQuizDownChoise;
    private TextView textViewQuizChoiseNqQuestion;
    private EditText editTextNbQuestion;
    private Button confirmAnswerButton;
    private InputMethodManager imm;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_quiz, container, false);

        imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(container.getWindowToken(), 0);

        buttonQuizQuick = root.findViewById(R.id.buttonQuizQuick);
        buttonQuizPerso = root.findViewById(R.id.buttonQuizPerso);
        languageSpinner = root.findViewById(R.id.languageSpinner);
        textViewQuizChoiseNqQuestion = root.findViewById(R.id.text_Quiz_Choise_Nb_Question);
        editTextNbQuestion = root.findViewById(R.id.NbQuestionEditText);
        confirmAnswerButton = root.findViewById(R.id.confirmAnswerButton);
        textQuizDownChoise = root.findViewById(R.id.text_Quiz_Down_Choise);

        textViewQuizChoiseNqQuestion.setVisibility(View.INVISIBLE);
        editTextNbQuestion.setVisibility(View.INVISIBLE);
        confirmAnswerButton.setVisibility(View.INVISIBLE);

        //Create spinner items
        initLanguageList();

        //Spinner settings
        languageSpinner.setOnItemSelectedListener(this);
        SpinnerAdapter dataAdapter = new SpinnerAdapter(getContext(),languageItems);
        languageSpinner.setAdapter(dataAdapter);

        ArrayList<WordTable> wordTableArrayList = DirectoryFragment.getWordTableArrayList();
        ArrayList<WordTable> wordTableArrayListTemp = new ArrayList<>();

        editTextNbQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmAnswerButton.setEnabled(s.toString().length() !=0 &&
                        !editTextNbQuestion.getText().toString().isEmpty() &&
                        Integer.parseInt(String.valueOf(editTextNbQuestion.getText())) != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonQuizQuick.setOnClickListener(v -> {
            wordTableArrayListTemp.clear();
            setList(foreignLang, wordTableArrayList, wordTableArrayListTemp);
            if(wordTableArrayListTemp.size()!=0){
                Intent QuizActivity = new Intent(getContext(), QuizActivity.class);
                QuizActivity.putExtra("LISTWORD", wordTableArrayListTemp);
                Objects.requireNonNull(getActivity()).startActivity(QuizActivity);
            }
            else{
                Toast.makeText(this.getContext(),"Aucun mot pour le quiz", Toast.LENGTH_SHORT).show();
            }
        });

        buttonQuizPerso.setOnClickListener(v -> {
            buttonQuizPerso.setVisibility(View.INVISIBLE);
            textQuizDownChoise.setVisibility(View.INVISIBLE);
            textViewQuizChoiseNqQuestion.setVisibility(View.VISIBLE);
            editTextNbQuestion.setVisibility(View.VISIBLE);
            confirmAnswerButton.setVisibility(View.VISIBLE);
            confirmAnswerButton.setEnabled(false);
        });

        confirmAnswerButton.setOnClickListener(v -> {
            imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(container.getWindowToken(), 0);
            wordTableArrayListTemp.clear();
            setList(foreignLang, wordTableArrayList, wordTableArrayListTemp);
            if(wordTableArrayListTemp.size()!=0){
                Intent QuizActivity = new Intent(getContext(), QuizActivity.class);
                QuizActivity.putExtra("LISTWORD", wordTableArrayListTemp);
                QuizActivity.putExtra("NBQUESTION", Integer.parseInt(String.valueOf(editTextNbQuestion.getText())));
                Objects.requireNonNull(getActivity()).startActivity(QuizActivity);
            }
            else{
                Toast.makeText(this.getContext(),"Aucun mot pour le quiz", Toast.LENGTH_SHORT).show();
            }
            editTextNbQuestion.getText().clear();
        });

        return root;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LanguageItem clickedItem = (LanguageItem) parent.getItemAtPosition(position);
        this.foreignLang =  clickedItem.getLanguageName();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setList(String foreignLang, ArrayList<WordTable> wordTableArrayList, ArrayList<WordTable> wordTableArrayListTemp) {

        for (WordTable word: wordTableArrayList) {
            if (word.getForeignLang().equals(foreignLang)) {
                wordTableArrayListTemp.add(word);
            }
        }

    }

    private void initLanguageList(){
        languageItems = new ArrayList<>();
        languageItems.add(new LanguageItem("anglais".toUpperCase(),R.mipmap.ic_flag_en));
        languageItems.add(new LanguageItem("roumain".toUpperCase(),R.mipmap.ic_flag_ro));
    }
}
