package com.pvplan.UIComponents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pvplan.R;
import com.pvplan.database.ProjectModel;

import java.util.ArrayList;

public class ProjectsListRecyclerViewAdapter extends RecyclerView.Adapter<ProjectsListRecyclerViewAdapter.MyViewHolder> {
    Context ctx;
    ArrayList<ProjectModel> pm;
    public ProjectsListRecyclerViewAdapter(Context context, ArrayList<ProjectModel> pm){
        this.ctx = context;
        this.pm = pm;
    }

    @NonNull
    @Override
    public ProjectsListRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new ProjectsListRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectsListRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.pow.setText(pm.get(position).getPower() + "kW");
        holder.nam.setText(pm.get(position).getName());
        holder.idx.setText(Integer.toString(pm.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return pm.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView pow;
        TextView nam;
        TextView idx;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            pow = itemView.findViewById(R.id.text_power_icon);
            nam = itemView.findViewById(R.id.text_created);
            idx = itemView.findViewById(R.id.textLocation);
        }
    }
}

