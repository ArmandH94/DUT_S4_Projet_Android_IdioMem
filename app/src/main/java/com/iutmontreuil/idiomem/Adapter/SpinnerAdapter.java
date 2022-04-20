package com.iutmontreuil.idiomem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iutmontreuil.idiomem.R;
import com.iutmontreuil.idiomem.model.LanguageItem;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter {

    public SpinnerAdapter(Context context, ArrayList<LanguageItem> strings){
        super(context,0,strings);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.language_spinner_row, parent, false
            );
        }

        ImageView imageViewFlag = convertView.findViewById(R.id.flag_imageView);
        TextView textViewName = convertView.findViewById(R.id.languageSelectTextView);

        LanguageItem currentItem = (LanguageItem) getItem(position);

        if (currentItem != null) {
            imageViewFlag.setImageResource(currentItem.getFlagImage());
            textViewName.setText(currentItem.getLanguageName());
        }

        return convertView;
    }
}
