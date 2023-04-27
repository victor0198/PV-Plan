package com.pvplan.UIComponents;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pvplan.MainActivity;
import com.pvplan.ProjectConfigurationActivity;
import com.pvplan.R;
import com.pvplan.database.ProjectModel;

import java.util.ArrayList;

public class ProjectsListRecyclerViewAdapter extends RecyclerView.Adapter<ProjectsListRecyclerViewAdapter.MyViewHolder> {
    Context ctx;
    ArrayList<ProjectModel> pm;
    MainActivity mainActivity;
    public ProjectsListRecyclerViewAdapter(Context context, ArrayList<ProjectModel> pm, MainActivity mainActivity){
        this.ctx = context;
        this.pm = pm;
        this.mainActivity= mainActivity;
    }

    @NonNull
    @Override
    public ProjectsListRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new ProjectsListRecyclerViewAdapter.MyViewHolder(view, ctx, mainActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectsListRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.pow.setText(pm.get(pm.size()-position-1).getPower() + "kW");
        holder.nam.setText(pm.get(pm.size()-position-1).getName());
        Log.d("in recycler", "showing Lat:" + pm.get(pm.size()-position-1).getLatitude()+".");
        Log.d("in recycler", "showing Lat:" + pm.get(pm.size()-position-1).getLatitude()+".");
        Log.d("->>>",pm.get(pm.size()-position-1).getLatitude().toString());
        if (!pm.get(pm.size()-position-1).getLatitude().equals("-181") && !pm.get(pm.size()-position-1).getLongitude().equals("-181")){
            holder.idx.setText("Location: " +
                    pm.get(pm.size()-position-1).getLatitude() + ", " +
                    pm.get(pm.size()-position-1).getLongitude());
        }
        holder.theId = pm.get(pm.size()-position-1).getId();
    }

    @Override
    public int getItemCount() {
        return pm.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        Context ctx;
        TextView pow;
        TextView nam;
        TextView idx;
        Integer theId;
        MainActivity mainActivity;

        public MyViewHolder(@NonNull View itemView, Context context, MainActivity mainActivity) {
            super(itemView);
            ctx = context;
            pow = itemView.findViewById(R.id.text_power_icon);
            nam = itemView.findViewById(R.id.text_name);
            idx = itemView.findViewById(R.id.textLocation);
            this.mainActivity = mainActivity;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(ctx, ProjectConfigurationActivity.class);
                    intent.putExtra(Intent.EXTRA_INDEX, theId.toString());
                    // start the Intent by the main activity, and register it for result
                    mainActivity.runProjectLauncher(intent);
                }
            });
        }
    }
}

