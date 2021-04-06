package com.example.engineer2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class DateListFragment extends Fragment {

    private View dateListFragmentView;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listOfDates = new ArrayList<>();
    private DatabaseReference DatesRef;


    public DateListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       dateListFragmentView = inflater.inflate(R.layout.fragment_date_list, container, false);
       DatesRef = FirebaseDatabase.getInstance().getReference().child("Dates");


       InitializeFields();

       RetrieveAndDisplayDates();

       return dateListFragmentView;
    }

    private void InitializeFields()
    {
        listView = (ListView) dateListFragmentView.findViewById(R.id.dates_list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.row_dates, listOfDates);
        listView.setAdapter(arrayAdapter);
    }

    private void RetrieveAndDisplayDates()
    {
        DatesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Set<String> set = new HashSet<>();
                Iterator iterator = snapshot.getChildren().iterator();
                while (iterator.hasNext())
                {
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }

                listOfDates.clear();
                listOfDates.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

