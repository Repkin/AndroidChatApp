package com.example.engineer2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    private FirebaseUser currentUser;
    private FirebaseAuth myAuth;
    private DatabaseReference RootRef;
    private String currentUserID, currentUserName;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private Button groupsButton, friendsButton, requestButton, chatButton, dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myAuth = FirebaseAuth.getInstance();
        currentUser = myAuth.getCurrentUser();
        currentUserID = myAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        InitializeFields();

        groupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new GroupsFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new FriendsFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new RequestsFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new ChatFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new DateListFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


    }


    @Override
    protected void onStart() {

        super.onStart();

        if (currentUser == null) {
            SendUserToLoginActivity();
        } else {
            //updateUserStatus("online");
            VerifyUserExistence();
        }
    }

    private void VerifyUserExistence() {
        String currentUserID = myAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if ((snapshot.child("name").exists())) {
                    currentUserName = snapshot.child("name").getValue().toString();
                    setTitle("Welcome " + snapshot.child("name").getValue().toString());
                } else {
                    SendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.main_logout) {
            myAuth.signOut();
            SendUserToLoginActivity();
        }
        if (item.getItemId() == R.id.main_create) {
            RequestNewGroup();
        }
        if (item.getItemId() == R.id.main_settings) {
            SendUserToSettingsActivity();

        }
        if (item.getItemId() == R.id.main_dates_menu)
        {
            RequestNewDate();
        }
        if (item.getItemId() == R.id.main_friends) {
            SendUserToFriendsActivity();
        }
        return true;
    }


    private void RequestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name: ");


        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("Group Name");
        groupNameField.setHintTextColor(getResources().getColor(R.color.white));
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(MainActivity.this, "Enter group name", Toast.LENGTH_SHORT).show();
                } else {
                    CreateNewGroup(groupName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void RequestNewDate()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.alert_date, null);
        builder.setTitle("When will You be free? ");
        CalendarView cal = new CalendarView(this);
        cal.setDate(System.currentTimeMillis(),false,true);
        final EditText dateField = (EditText) mView.findViewById(R.id.editText1) ;
        dateField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String working = s.toString();
                boolean isValid = true;
                if (working.length()==2 && before ==0) {
                    if (Integer.parseInt(working) < 1) {
                        isValid = false;
                    }
                    else {
                        working+="-";
                        dateField.setText(working);
                        dateField.setSelection(working.length());
                    }
                }
                else if (working.length()==5 && before ==0) {
                        working+=" from ";
                        dateField.setText(working);
                        dateField.setSelection(working.length());
                }
                else if (working.length()==13 && before == 0) {
                    working+=":";
                    dateField.setText(working);
                    dateField.setSelection(working.length());
                }
                else if (working.length() == 16 && before == 0) {
                    working+=" to ";
                    dateField.setText(working);
                    dateField.setSelection(working.length());
                }
                else if(working.length() == 22 && before == 0) {
                    working+=":";
                    dateField.setText(working);
                    dateField.setSelection(working.length());
                }
                else if (working.length() == 25 && before == 0) {

                       // working+=" "+ currentUserName;
                        dateField.setText(currentUserName + ": " + working);
                        dateField.setSelection(working.length());


                }
                if (!isValid)
                {
                    dateField.setError("Enter date like: DD.MM from hh:mm to hh:mm *your name*");
                }
                else
                {
                    dateField.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dateField.setTextColor(getResources().getColor(R.color.white));
        dateField.setHint("Start with day and month!");
        dateField.setHintTextColor(getResources().getColor(R.color.white));
        AlertDialog dialog = builder.create();
        builder.setView(mView);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String yourDate = dateField.getText().toString();

                if (TextUtils.isEmpty(yourDate))
                {
                    Toast.makeText(MainActivity.this, "Enter date", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateNewDate(yourDate);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void CreateNewGroup(String groupName) {
        RootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, groupName + " group is created successfully.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void CreateNewDate(String yourDate)
    {
        RootRef.child("Dates").child(yourDate).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "Succesfully added Your date", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void SendUserToGroupsActivity() {
        Intent groupsIntent = new Intent(MainActivity.this, GroupsActivity.class);
        startActivity(groupsIntent);
        finish();

    }

    private void SendUserToFriendsActivity() {
        Intent friendsIntent = new Intent(MainActivity.this, FriendsActivity.class);
        startActivity(friendsIntent);
    }

    private void SendUserToDateActivity()
    {
        Intent dateIntent = new Intent (MainActivity.this, DateActivity.class);
        startActivity(dateIntent);
    }

    private void InitializeFields() {
        groupsButton = (Button) findViewById(R.id.groupsButton);
        friendsButton = (Button) findViewById(R.id.friendsButton);
        requestButton = (Button) findViewById(R.id.requestButton);
        chatButton = (Button) findViewById(R.id.chatButton);
        dateButton = (Button) findViewById(R.id.dateButton);
    }
}