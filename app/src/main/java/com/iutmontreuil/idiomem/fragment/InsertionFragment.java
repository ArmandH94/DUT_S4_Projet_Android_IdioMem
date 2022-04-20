package com.iutmontreuil.idiomem.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.iutmontreuil.idiomem.adapter.SpinnerAdapter;
import com.iutmontreuil.idiomem.R;
import com.iutmontreuil.idiomem.model.LanguageItem;
import com.iutmontreuil.idiomem.model.PathUtil;
import com.iutmontreuil.idiomem.model.WordActions;
import com.iutmontreuil.idiomem.model.WordTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class InsertionFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final int PERMISSION_REQUEST_STORAGE = 1000;
    private static final int READ_REQUEST_CODE = 1000;

    private View view;
    private EditText wordSourceEditText,wordTradEditText;
    private TextView titleTrad;
    private Spinner languageSpinner;
    private Button insertWordButton, import_btn, export_btn;
    private String foreignLang;
    private ArrayList<LanguageItem> languageItems;
    private InputMethodManager imm;
    private ArrayList<WordTable> wordTableList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_insertion, container, false);

        imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(container.getWindowToken(), 0);

        wordTableList = DirectoryFragment.getWordTableArrayList();

        wordSourceEditText = view.findViewById(R.id.wordSourceEditText);
        wordTradEditText = view.findViewById(R.id.wordTradEditText);
        insertWordButton = view.findViewById(R.id.insertWordButton);
        languageSpinner = view.findViewById(R.id.insertLanguageSpinner);
        titleTrad = view.findViewById(R.id.text_Insertion_Down);
        import_btn = view.findViewById(R.id.import_btn);
        export_btn = view.findViewById(R.id.export_btn);

        //Create spinner items
        initLanguageList();

        //Spinner settings
        languageSpinner.setOnItemSelectedListener(this);
        SpinnerAdapter dataAdapter = new SpinnerAdapter(getContext(),languageItems);
        languageSpinner.setAdapter(dataAdapter);


        insertWordButton.setEnabled(false);

        //Check inputs != null to enable button
        wordSourceEditText.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            insertWordButton.setEnabled(s.toString().length() !=0 &&
                    !wordTradEditText.getText().toString().isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
        });
        wordTradEditText.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            insertWordButton.setEnabled(s.toString().length() !=0 &&
                    !wordSourceEditText.getText().toString().isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
        });

        insertWordButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(insertWordButton.getWindowToken(), 0);
            insertNewWord();
            wordSourceEditText.getText().clear();
            wordTradEditText.getText().clear();
        }
        });

        import_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importDatabase(v);
                wordSourceEditText.getText().clear();
                wordTradEditText.getText().clear();
            }
        });

        export_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDatabase(v);
                wordSourceEditText.getText().clear();
                wordTradEditText.getText().clear();
            }
        });

        return view;
    }

    private void initLanguageList() {
        languageItems = new ArrayList<>();
        languageItems.add(new LanguageItem("anglais".toUpperCase(),R.mipmap.ic_flag_en));
        languageItems.add(new LanguageItem("roumain".toUpperCase(),R.mipmap.ic_flag_ro));
    }

    private void insertNewWord() {
        String wordFr = wordSourceEditText.getText().toString().toLowerCase().trim().replaceAll(" ","");
        String translation = wordTradEditText.getText().toString().toLowerCase().trim().replaceAll(" ","");

        WordActions bd = new WordActions(this.getContext());

        if(!this.wordExist(wordFr,translation,foreignLang)){
            if(wordFr.length() > 0 && translation.length() > 0){
                bd.insertTask(wordFr, translation, this.foreignLang);
                Toast.makeText( this.getContext(), "Sauvegardé", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this.getContext(), "Champ(s) vide(s)", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this.getContext(),"La traduction existe déjà",Toast.LENGTH_SHORT).show();
        }

        Objects.requireNonNull(this.getActivity()).onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LanguageItem clickedItem = (LanguageItem) parent.getItemAtPosition(position);
        this.foreignLang = clickedItem.getLanguageName();
        titleTrad.setText(this.foreignLang);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean wordExist(String wordFr, String translation, String foreignLang){
        for (WordTable word : wordTableList){
            if(word.getWordFr().equals(wordFr) && word.getTranslation().equals(translation) && word.getForeignLang().equals(foreignLang)){
                return true;
            }
        }
        return false;
    }

    private void exportDatabase(View view) {
        //generate data.csv
        StringBuilder data = new StringBuilder();
        //add 3 columns to data.csv
        data.append("wordFr,translation,foreignLang");
        loadWordsIntoCSV(data);
        Log.e("Data content ", "exportDatabase: " + data.toString());
        try {
            //exporting
            Context context = Objects.requireNonNull(getActivity()).getApplicationContext();
            File fileLocation = new File(getActivity().getApplicationContext().getFilesDir(), "data.csv");
            Uri path = FileProvider.getUriForFile(context, "com.iutmontreuil.idiomem.fileprovider", fileLocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "data.csv");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Exportation de la base de données"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadWordsIntoCSV(StringBuilder data) {
        WordActions wordActions = new WordActions(getContext());
        try {
            for(WordTable word : wordActions.getAllWords()) {
                WordTable wordData = new WordTable();
                wordData.setIdWord(word.getIdWord());
                wordData.setWordFr(word.getWordFr());
                wordData.setTranslation(word.getTranslation());
                wordData.setForeignLang(word.getForeignLang());
                data.append("\n").append(wordData.getWordFr()).append(",").append(wordData.getTranslation()).append(",").append(wordData.getForeignLang());
            }
            FileOutputStream out = Objects.requireNonNull(getContext()).openFileOutput("data.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void importDatabase(View view) {
        performFileSearch();
    }

    //select file from storage
    private void performFileSearch(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    //affiche les résultats des permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_STORAGE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getContext(), "Permission accordée !", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getContext(), "Permission non accordée !", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == READ_REQUEST_CODE && resultCode == getActivity().RESULT_OK){
            if(data != null) {
                Uri uri = data.getData();
                String path = uri.getPath();
                try {
                    readCSV(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readCSV(Uri uri) throws IOException {
        try(InputStream inputStream = this.getContext().getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(inputStream)))){
            String line;
            WordActions wordActions = new WordActions(this.getContext());
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                Log.d("MyActivity", "Line: " + line);

                String[] tokens = line.split(",");
                if(!wordExist(tokens[0], tokens[1], tokens[2])){
                    wordActions.insertTask(tokens[0], tokens[1], tokens[2]);
                }
            }
            Toast.makeText(getContext(),"Importation réussie",Toast.LENGTH_SHORT).show();
        }
    }
}
