package com.example.GoogleCalendar;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private List<Task> taskList;
    private List<Task> filteredTaskList;

    public TaskAdapter() {
        taskList = new ArrayList<>();
        taskList.add(new Task("Imana Foods", "Order Pad", "Download Pending", R.color.overdueIncomplete));
        taskList.add(new Task("Imana Foods", "Stock Count", "Complete", R.color.complete_dark));
        taskList.add(new Task("No Principal", "Save Location", "Ready For Capture", R.color.currentFuture));
        taskList.add(new Task("No Principal", "Pack Shelves", "Ready For Capture", R.color.overdueIncomplete));
        taskList.add(new Task("Noodle King", "Order Pad", "Download Pending", R.color.overdueIncomplete));
        taskList.add(new Task("Noodle King", "Stock Count", "Complete", R.color.complete_dark));
        taskList.add(new Task("Prima Pasta", "Order Pad", "Complete", R.color.complete_dark));
        taskList.add(new Task("Prima Pasta", "Stock Count", "Complete", R.color.complete_dark));
        taskList.add(new Task("Imana Foods", "Price Survey", "Ready For Capture", R.color.currentFuture));
        taskList.add(new Task("Prima Pasta", "Pack Shelves", "Ready For Capture", R.color.overdueIncomplete));
        filteredTaskList = taskList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        CardView cardView = (CardView) layoutInflater.inflate(R.layout.task_card, parent, false);
        return new TaskViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Task task = filteredTaskList.get(position);
        if (task != null) {
            TaskViewHolder taskViewHolder = (TaskViewHolder) viewHolder;
            taskViewHolder.bind(task);
        }
    }

    @Override
    public int getItemCount() {
        return (filteredTaskList != null) ? filteredTaskList.size() : 0;
    }

    /**
     * Caching of the children views for a RecipeStep item
     */
    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView taskImage;
        final TextView taskName;
        final TextView taskPrincipal;
        final TextView taskStatus;
        final CardView cardView;

        public TaskViewHolder(@NonNull CardView cardView) {
            super(cardView);
            taskImage = cardView.findViewById(R.id.task_image);
            taskName = cardView.findViewById(R.id.task_name);
            taskPrincipal = cardView.findViewById(R.id.task_principal);
            taskStatus = cardView.findViewById(R.id.task_status);
            this.cardView = cardView;
            cardView.setOnClickListener(this);
        }

        void bind(Task task) {
            taskName.setText(task.getTaskName());
            taskPrincipal.setText(task.getTaskPrincipal());
            taskStatus.setText(task.getTaskStatus());
            switch (task.getTaskName()) {
                case "Order Pad":
                    taskImage.setImageResource(R.drawable.ic_op);
                    break;
                case "Stock Count":
                    taskImage.setImageResource(R.drawable.ic_sc);
                    break;
                case "Save Location":
                    taskImage.setImageResource(R.drawable.ic_sl);
                    break;
                default:
                    taskImage.setImageResource(R.drawable.ic_tm);
                    break;
            }
            cardView.setCardBackgroundColor(cardView.getResources().getColor(task.getTaskColour()));
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            // listItemClickListener.onListItemClick(clickedPosition);
        }
    }


    public void setTaskData(List<Task> taskList) {
        this.filteredTaskList = taskList;
        notifyDataSetChanged();
    }

    public void filterTasks(String principal) {
        if (principal.equals("All Principals")) {
            filteredTaskList = taskList;
        } else {
            filteredTaskList = taskList.stream()
                    .filter(task -> matchesPrincipal(task, principal))
                    .collect(Collectors.toList());
        }
        notifyDataSetChanged();
    }

    private boolean matchesPrincipal(Task task, String principal) {
        return task.getTaskPrincipal().equals(principal);
    }


}
