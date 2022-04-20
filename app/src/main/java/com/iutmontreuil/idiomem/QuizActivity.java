package com.iutmontreuil.idiomem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iutmontreuil.idiomem.model.WordTable;

import java.util.ArrayList;
import java.util.Collections;

public class QuizActivity extends AppCompatActivity {

    private TextView correctionTextView, questionTextView, textViewProgress;
    private EditText answerEditText;
    private Button confirmButton, nextButton;
    private int nbQuestions;
    private ArrayList<WordTable> wordTableArrayList;
    private String translation,wordFr;
    private Boolean isFrench;
    private ProgressBar progressBar;

    public QuizActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionTextView = findViewById(R.id.questionTextView);
        correctionTextView = findViewById(R.id.correctionTextView);
        answerEditText = findViewById(R.id.answerEditText);
        confirmButton = findViewById(R.id.confirmAnswerButton);
        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);
        textViewProgress = findViewById(R.id.textViewProgress);

        nextButton.setEnabled(false);

        wordTableArrayList = getIntent().getParcelableArrayListExtra("LISTWORD");
        assert wordTableArrayList != null;
        Collections.shuffle(wordTableArrayList);
        nbQuestions = getIntent().getIntExtra("NBQUESTION", 10);

        assert wordTableArrayList != null;
        if (wordTableArrayList.size() < nbQuestions) {
            nbQuestions = wordTableArrayList.size();
        }

        progressBar.setMax(nbQuestions);

        initQuestion();

        confirmButton.setOnClickListener(v ->{
            correctionTextView.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(confirmButton.getWindowToken(), 0);
            if(isFrench){
                if(answerEditText.getText().toString().toLowerCase().trim().equals(translation)){
                    Toast.makeText(this,"Correct !", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this,"Incorrect !", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                if(answerEditText.getText().toString().toLowerCase().trim().equals(wordFr)){
                    Toast.makeText(this,"Correct !", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this,"Incorrect !", Toast.LENGTH_SHORT).show();
                }
            }

            nextButton.setEnabled(true);

        });


        nextButton.setOnClickListener( v ->{

            if (nextButton.getText().equals("TERMINER")) {
                QuizActivity.this.finish();
            }

            nextButton.setEnabled(false);

            if (nbQuestions > 0) {
                questionTextView.setText("");
                answerEditText.setText("");
                correctionTextView.setText("");

                initQuestion();
            }

        });

    }

    public void initQuestion(){
        progressBar.incrementProgressBy(1);

        if (progressBar.getProgress() == 1) {
            textViewProgress.setText(String.valueOf(progressBar.getProgress()+"ère question"));
        }
        else {
            textViewProgress.setText(String.valueOf(progressBar.getProgress()+"ème question"));
        }

        nbQuestions--;
        wordFr = wordTableArrayList.get(nbQuestions).getWordFr();
        translation = wordTableArrayList.get(nbQuestions).getTranslation();
        int SelectLanguageTrad = (int)(Math.random()*2);
        isFrench = SelectLanguageTrad == 0;

        if (nbQuestions == 0) {
            nextButton.setText(R.string.endQuestion);
        }

        correctionTextView.setVisibility(View.INVISIBLE);
        if(isFrench){
            questionTextView.setText(String.format("Traduisez :\n%s", wordFr));
            correctionTextView.setText(String.format("Correction :\n%s", translation));
        }
        else{
            questionTextView.setText(String.format("Traduisez :\n%s", translation));
            correctionTextView.setText(String.format("Correction :\n%s", wordFr));
        }
    }
}
