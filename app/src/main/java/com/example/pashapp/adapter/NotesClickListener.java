package com.example.pashapp.adapter;

import androidx.cardview.widget.CardView;

import com.example.pashapp.modules.Notes;

public interface NotesClickListener {

    void onClick (Notes notes);
    void onLongClick (Notes notes, CardView cardView);
}
