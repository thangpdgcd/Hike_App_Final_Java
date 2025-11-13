package com.example.hikeapplication.Observation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hikeapplication.ConnectDb;
import com.example.hikeapplication.Hike.EditHikeActivity;
import com.example.hikeapplication.MainActivity;
import com.example.hikeapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.MyViewHolder> implements Filterable {
    private final Context context;
    private final Activity activity;
    public List<Observation> observations;
    private final List<Observation> observationSearch;

    public ObservationAdapter(Activity activity, Context context, List<Observation> observations) {
        this.activity = activity;
        this.context = context;
        this.observations = observations;
        this.observationSearch = observations;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameObservation;
        Button buttonDelete, buttonMore;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameObservation = itemView.findViewById(R.id.nameObservation);
            mainLayout = itemView.findViewById(R.id.mainLayoutObservations);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonMore = itemView.findViewById(R.id.buttonEdit);
        }
    }


    @NonNull
    @Override
    public ObservationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_observations, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ObservationAdapter.MyViewHolder holder, int position) {
        Observation observation = observations.get(position);
        holder.nameObservation.setText(String.valueOf(observation.getObservation()));


        holder.buttonDelete.setOnClickListener(v -> {
            confirm(observation.getId(), observation.getObservation());
        });

        holder.buttonMore.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditObservationActivity.class);
            intent.putExtra("observation_id", String.valueOf(observation.getId()));
            intent.putExtra("name", observation.getObservation());
            intent.putExtra("time", observation.getTime());
            intent.putExtra("comment", observation.getComment());
            activity.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return observations.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String stringSearch = charSequence.toString();

                if (stringSearch.isEmpty()) {
                    observations = observationSearch;
                } else {
                    List<Observation> list = new ArrayList<>();
                    for (Observation observation : observationSearch) {
                        if (observation.getObservation().toLowerCase().contains(stringSearch.toLowerCase())) {
                            list.add(observation);
                        }
                    }
                    observations = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = observations;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                observations = (List<Observation>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    void confirm(int id, String name) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle("Delete observation " + name + " ?");
        builder.setMessage("Are you sure? You want to delete observation?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @SuppressLint("UnsafeIntentLaunch")
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ConnectDb db = new ConnectDb(activity);
                db.deleteObservation(String.valueOf(id));
                Intent intent = activity.getIntent();
                activity.finish();
                activity.startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
}
