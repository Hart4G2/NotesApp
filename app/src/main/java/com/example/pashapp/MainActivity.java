package com.example.pashapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.pashapp.adapter.NotesClickListener;
import com.example.pashapp.adapter.NotesListAdapter;
import com.example.pashapp.database.RoomDB;
import com.example.pashapp.modules.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    public static Receiver receiver;

    private RecyclerView recyclerView;
    private FloatingActionButton fab_add;
    private NotesListAdapter notesListAdapter;
    private RoomDB database;
    private List<Notes> notes = new ArrayList<>();
    private SearchView searchView_notes;
    private Notes selectedNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_home);
        fab_add = findViewById(R.id.fabb_add);
        searchView_notes = findViewById(R.id.searchView_notes);

        database = RoomDB.getInstance(this);
        notes = database.mainDAO().getAll();

        updateRecyclerView(notes);

        fab_add.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
            startActivityForResult(intent, 101);
        });

        searchView_notes.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        initReceiver();
    }

    private void initReceiver(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("7", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel Description");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        receiver = new Receiver();
        receiver.setRepeatingNotification(this);
    }

    private void filter(String newText) {
        List<Notes> filteredList = new ArrayList<>();

        for(Notes singleNote : notes){
            if (singleNote.getTitle().toLowerCase().contains(newText.toLowerCase()) ||
                    singleNote.getNotes().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(singleNote);
            }
        }

        notesListAdapter.filterList(filteredList);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101){
            if(resultCode == Activity.RESULT_OK){
                Notes newNotes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().insert(newNotes);
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
        }

        if(requestCode == 102){
            if(resultCode == Activity.RESULT_OK){
                Notes newNotes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().update(newNotes.getID(), newNotes.getTitle(), newNotes.getNotes());
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateRecyclerView(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        notesListAdapter = new NotesListAdapter(MainActivity.this, notes, notesClickListener);
        recyclerView.setAdapter(notesListAdapter);
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
            intent.putExtra("old_notes", notes);
            startActivityForResult(intent, 102);
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {

            selectedNote = new Notes();
            selectedNote = notes;
            showPopUp(cardView);
        }
    };

    private void showPopUp(CardView cardView) {

        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.pin:{
                database.mainDAO().pin(selectedNote.getID(), selectedNote.isPinned());
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
                return true;
            }
            case R.id.delete:{
                database.mainDAO().delete(selectedNote);
                notes.remove(selectedNote);
                notesListAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
            default:
                return false;
        }
    }
}