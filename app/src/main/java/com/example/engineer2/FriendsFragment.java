package com.example.engineer2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsFragment extends Fragment {

    private View FriendsView;
    private RecyclerView myFriendsList;

    private DatabaseReference FriendsRef, UserRef;
    private FirebaseAuth myAuth;
    private String currentUserID;


    public FriendsFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FriendsView = inflater.inflate(R.layout.fragment_friends, container, false);

        myFriendsList = (RecyclerView) FriendsView.findViewById(R.id.friends_list);
        myFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));


        myAuth = FirebaseAuth.getInstance();
        currentUserID = myAuth.getCurrentUser().getUid();

        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return FriendsView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(FriendsRef, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, FriendsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsViewHolder holder, int position, @NonNull Contacts model)
            {
                String userIDs = getRef(position).getKey();
                UserRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.hasChild("image"))
                        {
                            String userImage = snapshot.child("image").getValue().toString();
                            String userName1 = snapshot.child("name").getValue().toString();
                            String userStatus1 = snapshot.child("status").getValue().toString();

                            holder.userName.setText(userName1);
                            holder.userStatus.setText(userStatus1);
                            Picasso.get().load(userImage).placeholder(R.drawable.blank_profile).into(holder.profileImage);
                        }
                        else
                        {
                            String userName1 = snapshot.child("name").getValue().toString();
                            String userStatus1 = snapshot.child("status").getValue().toString();

                            holder.userName.setText(userName1);
                            holder.userStatus.setText(userStatus1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
                });
            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_friends_layout, parent, false);
                FriendsViewHolder viewHolder = new FriendsViewHolder(view);
                return viewHolder;

            }
        };

        myFriendsList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView profileImage;


        public FriendsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_profile_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);


        }
    }
}