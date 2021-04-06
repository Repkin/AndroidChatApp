package com.example.engineer2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DateActivity extends AppCompatActivity {

    private CalendarView calendar;
    private Button SelectDateButton;
    private EditText insertDate;
    private FirebaseAuth myAuth;
    private DatabaseReference RootRef, UsersReference, dateKeyReference, dateReference;

    private String currentUserId, currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
        CalendarView cal = new CalendarView(this);
        cal.setDate(System.currentTimeMillis(),false,true);
        myAuth = FirebaseAuth.getInstance();
        currentUserId = myAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");


        InitializeFields();
        GetUserInfo();

        SelectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String dateTime = insertDate.getText().toString();

                if (TextUtils.isEmpty(dateTime))
                {
                    Toast.makeText(DateActivity.this, "Please insert date, when you have free time", Toast.LENGTH_SHORT).show();
                }
                else
                {
                   // SaveDateToDatabase(dateTime);
                    insertDate.setText("");
                }

            }
        });

    }

    private void InitializeFields() {
        calendar = (CalendarView) findViewById(R.id.calendar_view);
        SelectDateButton = (Button) findViewById(R.id.button_set_date);
        insertDate = (EditText) findViewById(R.id.insert_date_field);
    }

    private void GetUserInfo() {
        UsersReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUserName = snapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
}

    private void SaveDateToDatabase()
    {
    //    RootRef.child("Dates").child(dateTime)
       //         .addOnCompleteListener()


    }
}