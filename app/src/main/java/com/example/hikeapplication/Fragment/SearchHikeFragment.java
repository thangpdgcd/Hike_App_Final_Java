package com.example.hikeapplication.Fragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hikeapplication.ConnectDb;
import com.example.hikeapplication.Hike.Hike;
import com.example.hikeapplication.Hike.HikeAdapter;
import com.example.hikeapplication.R;

import java.util.ArrayList;
import java.util.List;

public class SearchHikeFragment extends Fragment {

    private RecyclerView recyclerView;
    List<Hike> hikeList = new ArrayList<>();
    HikeAdapter hikeAdapter;
    ConnectDb db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_hike, container, false);
        recyclerView = view.findViewById(R.id.searchRecyclerView);
        db = new ConnectDb(getActivity());

        Button searchButton = view.findViewById(R.id.search_btn);
        searchButton.setOnClickListener(v -> {
            EditText inputSearch = view.findViewById(R.id.inputSearch);
            TextView result = view.findViewById(R.id.result);
            result.setText(R.string.result);
            displayHike();
            hikeAdapter = new HikeAdapter(getActivity(), getContext(), hikeList);
            hikeAdapter.getFilter().filter(inputSearch.getText());
            recyclerView.setAdapter(hikeAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        });

        // 🔹 Hiển thị nút back và đặt tiêu đề khi vào Search
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle("Search hike");
        }

        return view;
    }

    public void displayHike() {
        hikeList.clear();
        hikeList = db.getHike();
        if (hikeList.size() == 0) {
            Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
        }
    }
}
