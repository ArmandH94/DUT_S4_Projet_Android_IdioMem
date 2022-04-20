package com.iutmontreuil.idiomem.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iutmontreuil.idiomem.EditActivity;
import com.iutmontreuil.idiomem.R;
import com.iutmontreuil.idiomem.model.WordActions;
import com.iutmontreuil.idiomem.model.WordTable;
import com.iutmontreuil.idiomem.fragment.DirectoryFragment;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.CustomViewHolder> {

    private List<WordTable> wordTableList;
    private Context mContext;
    private Activity act;
    private OnCardListener onCardListener;
    private String language;
    private String foreignLang;
    private WordActions wordActions;

    public ListAdapter(Context context, ArrayList<WordTable> wordTables, Activity activity, OnCardListener onCardListener, String language, String foreignLang) {
        this.wordTableList = wordTables;
        this.mContext = context;
        this.act = activity;
        this.onCardListener = onCardListener;
        this.language = language;
        this.foreignLang = foreignLang;
        wordActions = new WordActions(context);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view, onCardListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, final int position) {
        final WordTable get = wordTableList.get(position);

        if (this.language.equals("Français")) {
            holder.text_holder.setText(get.getWordFr());
        } else {
            holder.text_holder.setText(get.getTranslation());
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = get.getIdWord();
                wordActions.delete(id);
                wordTableList.remove(position);
                DirectoryFragment.getWordTableArrayList().remove(get);
                notifyDataSetChanged();
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditActivity = new Intent(mContext, EditActivity.class);
                EditActivity.putExtra("WORDTABLE", get);
                EditActivity.putExtra("POSITION", position);
                EditActivity.putExtra("WORDFR", get.getWordFr());
                EditActivity.putExtra("TRANSLATION", get.getTranslation());
                EditActivity.putExtra("FOREIGNLANG",get.getForeignLang());
                int FRAGMENT_REQUEST_CODE_EDIT = 42;
                act.startActivityForResult(EditActivity, FRAGMENT_REQUEST_CODE_EDIT);
                notifyItemChanged(position);
            }
        });
    }



    @Override
    public int getItemCount() {
        return (null != wordTableList ? wordTableList.size() : 0);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setLanguage(String language){
        this.language = language;
    }

    public void setForeignLang(String foreignLang) {
        this.foreignLang = foreignLang;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView text_holder;
        ImageButton delete, edit;
        OnCardListener onCardListener;

        public CustomViewHolder(View itemView, OnCardListener onCardListener) {
            super(itemView);
            this.onCardListener = onCardListener;
            text_holder = (TextView) itemView.findViewById(R.id.text_holder);
            delete = (ImageButton) itemView.findViewById(R.id.delete);
            edit = (ImageButton) itemView.findViewById(R.id.edit);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCardListener.onCardClick(getAdapterPosition());
        }
    }
    public interface OnCardListener{

        void onCardClick(int position);
    }
}
