package com.example.roomtaskapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView; // ✅ Correct Import
import androidx.appcompat.widget.Toolbar;
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
    private Toolbar toolbar;
    private TextView headingText;

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
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        recyclerView = findViewById(R.id.recycler_view);
        fabAdd = findViewById(R.id.button_add_task);
        repository = new TaskRepository(getApplication());
        adapter = new TaskAdapter(this);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        repository.getAllTasks().observe(this, tasks -> adapter.setTasks(tasks));
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
                    showToast("No task selected");
                }
            }

            @Override
            public void onDeleteClick(Task task) {
                if (task != null) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Confirm Delete")
                            .setMessage("Are you sure you want to delete this task?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                repository.delete(task);
                                showToast("Task deleted");
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                } else {
                    showToast("No task selected");
                }
            }
        });
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText editTitle = dialogView.findViewById(R.id.edit_title);
        EditText editDescription = dialogView.findViewById(R.id.edit_description);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Task")
                .setView(dialogView)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                String title = editTitle.getText().toString().trim();
                String desc = editDescription.getText().toString().trim();

                if (title.isEmpty()) {
                    editTitle.setError("Title required");
                    return;
                }

                Task task = new Task(title, desc);
                repository.insert(task);
                showToast("Task added");
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void showEditDialog(Task task) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText editTitle = dialogView.findViewById(R.id.edit_title);
        EditText editDescription = dialogView.findViewById(R.id.edit_description);

        editTitle.setText(task.getTitle());
        editDescription.setText(task.getDescription());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Task")
                .setView(dialogView)
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                String updatedTitle = editTitle.getText().toString().trim();
                String updatedDesc = editDescription.getText().toString().trim();

                if (updatedTitle.isEmpty()) {
                    editTitle.setError("Title required");
                    return;
                }

                task.setTitle(updatedTitle);
                task.setDescription(updatedDesc);
                repository.update(task);
                showToast("Task updated");
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // Toolbar Search Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_popup_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search tasks...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        return true;
    }
}
