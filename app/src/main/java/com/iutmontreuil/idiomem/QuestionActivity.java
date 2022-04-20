package com.iutmontreuil.idiomem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionActivity extends AppCompatActivity {

    private TextView correctionTextView, questionTextView;
    private EditText answerEditText;
    private Button confirmButton, endButton;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        String wordFr = getIntent().getStringExtra("WORDFR");
        String translation = getIntent().getStringExtra("TRANSLATION");
        Boolean isFrench = getIntent().getBooleanExtra("MODE",true);

        questionTextView = findViewById(R.id.questionTextView);
        correctionTextView = findViewById(R.id.correctionTextView);
        answerEditText = findViewById(R.id.answerEditText);
        confirmButton = findViewById(R.id.confirmAnswerButton);
        endButton = findViewById(R.id.endQuestionButton);

        endButton.setVisibility(View.GONE);
        endButton.setEnabled(false);

        initQuestion(wordFr,translation, isFrench);

        confirmButton.setOnClickListener(v ->{
            correctionTextView.setVisibility(View.VISIBLE);
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
            endButton.setEnabled(true);
            endButton.setVisibility(View.VISIBLE);
        });

        endButton.setOnClickListener(v -> {
            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(endButton.getWindowToken(), 0);
            this.finish();
        });
    }

    public void initQuestion(String wordFr, String translation, Boolean mode){
        correctionTextView.setVisibility(View.INVISIBLE);
        if(mode){
            questionTextView.setText(String.format("Traduisez :\n%s", wordFr));
            correctionTextView.setText(String.format("Correction :\n%s", translation));
        }
        else{
            questionTextView.setText(String.format("Traduisez :\n%s", translation));
            correctionTextView.setText(String.format("Correction :\n%s", wordFr));
        }
    }
}
