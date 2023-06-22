package com.example.pashapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pashapp.modules.Notes;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotesTakerActivity extends AppCompatActivity {

    private EditText editText_title, editText_notes;
    private ImageView imageView_save;
    private Notes notes;
    private boolean isOldNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_taker);

        editText_title = findViewById(R.id.editText_title);
        editText_notes = findViewById(R.id.editText_notes);

        imageView_save = findViewById(R.id.imageView_save);

        notes = new Notes();
        try {
            notes = (Notes) getIntent().getSerializableExtra("old_notes");
            editText_title.setText(notes.getTitle());
            editText_notes.setText(notes.getNotes());
            isOldNote = true;
        } catch (Exception e){
            e.printStackTrace();
        }

        imageView_save.setOnClickListener(v -> {
            String title = editText_title.getText().toString();
            String description = editText_notes.getText().toString();

            if(description.isEmpty()){
                Toast.makeText(NotesTakerActivity.this, "Please enter descriptiption", Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm::ss");
            Date date = new Date();

            if(!isOldNote) {
                notes = new Notes();
            }
            notes.setTitle(title);
            notes.setNotes(description);
            notes.setDate(format.format(date));

            Intent intent = new Intent();
            intent.putExtra("note", notes);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });

    }
}