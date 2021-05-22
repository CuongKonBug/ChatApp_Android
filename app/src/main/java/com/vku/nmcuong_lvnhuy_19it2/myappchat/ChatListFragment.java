package com.vku.nmcuong_lvnhuy_19it2.myappchat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vku.nmcuong_lvnhuy_19it2.myappchat.Adapter.AdapterChat;
import com.vku.nmcuong_lvnhuy_19it2.myappchat.Adapter.AdapterChatList;
import com.vku.nmcuong_lvnhuy_19it2.myappchat.Model.ModelChat;
import com.vku.nmcuong_lvnhuy_19it2.myappchat.Model.ModelChatList;
import com.vku.nmcuong_lvnhuy_19it2.myappchat.Model.ModelUser;

import java.util.ArrayList;
import java.util.List;


public class ChatListFragment extends Fragment {
FirebaseAuth firebaseAuth;
RecyclerView chatlistRv;
List<ModelChatList> chatlistList;
List<ModelUser> userList;
DatabaseReference reference;
FirebaseUser currentUser;
AdapterChatList adapterChatList;


    public ChatListFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat_list, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        chatlistRv = view.findViewById(R.id.chatlistRv);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        chatlistList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             chatlistList.clear();
             for (DataSnapshot ds:snapshot.getChildren()) {
                 ModelChatList chatList = ds.getValue(ModelChatList.class);
                 chatlistList.add(chatList);
             }
             loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    private void loadChats() {
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    ModelUser user = ds.getValue(ModelUser.class);
                    for (ModelChatList chatList: chatlistList) {
                        if (user.getUid() != null && user.getUid().equals(chatList.getId())){
                            userList.add(user);
                            break;
                        }
                    }
                    //adapter
                    adapterChatList = new AdapterChatList(getContext(),userList);
                    chatlistRv.setAdapter(adapterChatList);
                    //set last mess
                    for (int i = 0;i<userList.size();i++){
                        lastMess(userList.get(i).getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void lastMess(String userId) {
     DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
     reference.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
             String lastMess = "default";
             for (DataSnapshot ds:snapshot.getChildren()) {
                 ModelChat chat = ds.getValue(ModelChat.class);
                 if (chat==null){
                     continue;
                 }
                 String sender = chat.getSender();
                 String receiver = chat.getReceiver();
                 if (sender == null || receiver == null) {
                     continue;
                 }
                 if (chat.getReceiver().equals(currentUser.getUid()) && chat.getSender().equals(userId) ||
                         chat.getReceiver().equals(userId) && chat.getSender().equals(currentUser.getUid())) {
                     lastMess = chat.getMessage();
                 }
             }
             adapterChatList.setLastMessMap(userId,lastMess);
             adapterChatList.notifyDataSetChanged();
         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
     });
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null)
        {

        }
        else {
            startActivity(new Intent(getActivity(),MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
        menu.findItem(R.id.action_add_post).setVisible(false);


        super.onCreateOptionsMenu(menu,inflater);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}