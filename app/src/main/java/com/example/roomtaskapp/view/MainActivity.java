package com.example.roomtaskapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtaskapp.R;
import com.example.roomtaskapp.controller.TaskAdapter;
import com.example.roomtaskapp.controller.TaskRepository;
import com.example.roomtaskapp.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private TaskRepository repository;
    private TaskAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupObservers();
        setupFabClick();
        setupAdapterListeners();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        fabAdd = findViewById(R.id.button_add_task);
        repository = new TaskRepository(getApplication());
        adapter = new TaskAdapter();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        repository.getAllTasks().observe(this, adapter::setTasks);
    }

    private void setupFabClick() {
        fabAdd.setOnClickListener(view -> showAddDialog());
    }

    private void setupAdapterListeners() {
        adapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {

            @Override
            public void onEditClick(Task task) {
                if (task != null) {
                    showEditDialog(task);
                } else {
                    Toast.makeText(MainActivity.this, "No task selected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteClick(Task task) {
                if (task != null) {
                    repository.delete(task);
                    Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No task selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void showAddDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText editTitle = dialogView.findViewById(R.id.edit_title);
        EditText editDescription = dialogView.findViewById(R.id.edit_description);

        new AlertDialog.Builder(this)
                .setTitle("Add Task")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = editTitle.getText().toString().trim();
                    String desc = editDescription.getText().toString().trim();

                    if (title.isEmpty()) {
                        Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Task task = new Task(title, desc);
                    repository.insert(task);
                    Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditDialog(Task task) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText editTitle = dialogView.findViewById(R.id.edit_title);
        EditText editDescription = dialogView.findViewById(R.id.edit_description);

        editTitle.setText(task.getTitle());
        editDescription.setText(task.getDescription());

        new AlertDialog.Builder(this)
                .setTitle("Edit Task")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String updatedTitle = editTitle.getText().toString().trim();
                    String updatedDesc = editDescription.getText().toString().trim();

                    if (updatedTitle.isEmpty()) {
                        Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    task.setTitle(updatedTitle);
                    task.setDescription(updatedDesc);
                    repository.update(task);
                    Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
