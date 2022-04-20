package com.iutmontreuil.idiomem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iutmontreuil.idiomem.model.WordActions;
import com.iutmontreuil.idiomem.model.WordTable;
import com.iutmontreuil.idiomem.fragment.DirectoryFragment;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {

    public static final String BUNDLE_EDIT = "UPDATEWORDTABLE";

    private EditText wordFr, translation;
    private Button edit;
    private int position;

    private WordActions wordActions;
    private WordTable wordTable;
    private InputMethodManager imm;
    private ArrayList<WordTable> wordTableList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        wordFr = (EditText) findViewById(R.id.wordFr);
        translation = (EditText) findViewById(R.id.translation);
        edit = (Button)findViewById(R.id.edit);

        wordTableList = DirectoryFragment.getWordTableArrayList();
        wordActions = new WordActions(getApplicationContext());
        wordTable = getIntent().getParcelableExtra("WORDTABLE");
        position = getIntent().getIntExtra("POSITION", 0);
        editWord(wordTable);
    }

    public void editWord(final WordTable get) {

        wordFr.setText(getIntent().getStringExtra("WORDFR"));
        translation.setText(getIntent().getStringExtra("TRANSLATION"));

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldWordEdit = get.getWordFr();
                String oldTranslation = get.getTranslation();

                String wordFrEdit = wordFr.getText().toString();
                String translationEdit = translation.getText().toString();
                String foreignLangEdit = getIntent().getStringExtra("FOREIGNLANG");

                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);

                if (!wordExist(wordFrEdit, translationEdit, foreignLangEdit)) {
                    get.setWordFr(wordFr.getText().toString().trim().replaceAll(" ",""));
                    get.setTranslation(translation.getText().toString().trim().replaceAll(" ",""));
                    if(!wordExist(get.getWordFr(),get.getTranslation(),get.getForeignLang())){
                        Toast.makeText(getApplicationContext(),"Sauvegardé",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        get.setWordFr(oldWordEdit);
                        get.setTranslation(oldTranslation);
                        Toast.makeText(getApplicationContext(),"La traduction existe déjà. Aucun changement fait",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"La traduction existe déjà.",Toast.LENGTH_SHORT).show();
                }

                try {

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(BUNDLE_EDIT, get);
                    resultIntent.putExtra("POSITION", position);
                    setResult(RESULT_OK ,resultIntent);
                }catch (Exception e) {
                    setResult(RESULT_CANCELED, new Intent());
                    Toast.makeText(getApplicationContext(), String.valueOf("Erreur EDIT"), Toast.LENGTH_SHORT).show();
                }

                finish();
            }
        });
    }

    public boolean wordExist(String wordFr, String translation, String foreignLang){
        for (WordTable word : wordTableList){
            if(word.getWordFr().equals(wordFr) && word.getTranslation().equals(translation) && word.getForeignLang().equals(foreignLang)){
                return true;
            }
        }
        return false;
    }

}
