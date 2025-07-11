package com.example.myapplication.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.GameHistoryManager;
import com.example.myapplication.R;

import java.util.List;

public class GameHistoryAdapter extends RecyclerView.Adapter<GameHistoryAdapter.ViewHolder> {
    private final List<GameHistoryManager.GameEntry> historyList;

    public GameHistoryAdapter(List<GameHistoryManager.GameEntry> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameHistoryManager.GameEntry entry = historyList.get(position);
        holder.tvResult.setText(entry.result);
        holder.tvMode.setText(entry.mode);
        holder.tvDate.setText(entry.date);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvResult, tvMode, tvDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvResult = itemView.findViewById(R.id.tv_result);
            tvMode = itemView.findViewById(R.id.tv_mode);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
} 