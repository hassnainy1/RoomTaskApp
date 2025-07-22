package com.example.roomtaskapp.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtaskapp.R;
import com.example.roomtaskapp.model.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList = new ArrayList<>();
    private List<Task> fullTaskList = new ArrayList<>();
    private OnItemClickListener listener;
    private Context context;

    public TaskAdapter(Context context) {
        this.context = context;
    }

    public interface OnItemClickListener {
        void onEditClick(Task task);
        void onDeleteClick(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<Task> tasks) {
        this.taskList = tasks;
        this.fullTaskList = new ArrayList<>(tasks); // Keep full list for searching
        notifyDataSetChanged();
    }

    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            taskList = new ArrayList<>(fullTaskList);
        } else {
            List<Task> filtered = new ArrayList<>();
            String lowerQuery = query.toLowerCase().trim();
            for (Task task : fullTaskList) {
                if (task.getTitle().toLowerCase().contains(lowerQuery)) {
                    filtered.add(task);
                }
            }
            taskList = filtered;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView menuButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.task_title);
            description = itemView.findViewById(R.id.task_description);
            menuButton = itemView.findViewById(R.id.menu_button);
        }

        public void bind(Task task) {
            title.setText(task.getTitle());
            description.setText(task.getDescription());

            menuButton.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(context, v);
                popup.inflate(R.menu.task_popup_menu);
                popup.setOnMenuItemClickListener(item -> {
                    if (listener == null) return false;

                    if (item.getItemId() == R.id.menu_edit) {
                        listener.onEditClick(task);
                        return true;

                    } else if (item.getItemId() == R.id.menu_delete) {
                        new MaterialAlertDialogBuilder(context)
                                .setTitle("Delete Task")
                                .setMessage("Are you sure you want to delete this task?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    listener.onDeleteClick(task);
                                })
                                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                .show();
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }
    }
}
