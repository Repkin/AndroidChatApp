package com.example.engineer2;

import android.content.Intent;
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


public class ChatFragment extends Fragment {

    private View PrivateChatsView;
    private RecyclerView PrivateChatsList;
    private String currentUserID;

    private DatabaseReference ChatsRef, UsersRef;
    private FirebaseAuth myAuth;


    public ChatFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        PrivateChatsView = inflater.inflate(R.layout.fragment_chat, container, false);

        PrivateChatsList = (RecyclerView) PrivateChatsView.findViewById(R.id.chat_list);
        PrivateChatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        myAuth = FirebaseAuth.getInstance();
        currentUserID = myAuth.getCurrentUser().getUid();
        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        return PrivateChatsView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options
                = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ChatsRef, Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts, ChatsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatsViewHolder holder, int position, @NonNull Contacts model)
            {
                final String usersIDs = getRef(position).getKey();
                final String[] chatImage = {"default_image"};

                UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.exists())
                        {
                            if (snapshot.hasChild("image"))
                            {
                                chatImage[0] = snapshot.child("image").getValue().toString();
                                Picasso.get().load(chatImage[0]).placeholder(R.drawable.blank_profile).into(holder.userImage);
                            }
                            final String chatName = snapshot.child("name").getValue().toString();
                            final String chatStatus = snapshot.child("status").getValue().toString();

                            holder.userName.setText(chatName);
                            holder.userStatus.setText("Last Seen: " + "\n" + "Date " + "Time ");

                            holder.itemView.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                    chatIntent.putExtra("visit_user_ID", usersIDs);
                                    chatIntent.putExtra("visit_user_name", chatName);
                                    chatIntent.putExtra("visit_image", chatImage[0]);
                                    startActivity(chatIntent);
                                }
                            });
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
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_friends_layout, parent, false);

                return new ChatsViewHolder(view);
            }
        };
        PrivateChatsList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class ChatsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView userImage;
        TextView userName, userStatus;


        public ChatsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userImage = itemView.findViewById(R.id.users_profile_image);
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_profile_status);

        }
    }
}