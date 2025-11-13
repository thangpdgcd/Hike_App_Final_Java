package com.example.hikeapplication.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.hikeapplication.ConnectDb;
import com.example.hikeapplication.Hike.Hike;
import com.example.hikeapplication.Hike.HikeAdapter;
import com.example.hikeapplication.MainActivity;
import com.example.hikeapplication.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    List<Hike> hikeList = new ArrayList<>();
    HikeAdapter hikeAdapter;
    ConnectDb db;
    Button buttonDeleteAll;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewHike);

        db = new ConnectDb(getActivity());
        displayHike();
        hikeAdapter = new HikeAdapter(getActivity(), getContext(), hikeList);
        recyclerView.setAdapter(hikeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        buttonDeleteAll = view.findViewById(R.id.buttonDeleteAll);
        buttonDeleteAll.setOnClickListener(v -> confirm());

        // 🔹 Ẩn nút back và đặt tiêu đề khi ở Home
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            activity.getSupportActionBar().setTitle("Hike Application");
        }

        return view;
    }

    void confirm() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Delete all hike?");
        builder.setMessage("Are you sure? You want to delete all hike?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            ConnectDb db = new ConnectDb(getActivity());
            db.deleteAllHike();
            Intent intent = new Intent(getContext(), MainActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {});
        builder.create().show();
    }

    public void displayHike() {
        hikeList.clear();
        hikeList = db.getHike();
        if (hikeList.size() == 0) {
            Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
        }
    }
}
