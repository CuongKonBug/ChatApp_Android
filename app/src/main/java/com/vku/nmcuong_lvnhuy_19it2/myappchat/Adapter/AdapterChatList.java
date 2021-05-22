package com.vku.nmcuong_lvnhuy_19it2.myappchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vku.nmcuong_lvnhuy_19it2.myappchat.ChatActivity;
import com.vku.nmcuong_lvnhuy_19it2.myappchat.Model.ModelUser;
import com.vku.nmcuong_lvnhuy_19it2.myappchat.R;

import java.util.HashMap;
import java.util.List;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.MyHolder> {

    Context context;
    List<ModelUser> userList;
    private HashMap<String, String> lastMessMap;

    public AdapterChatList(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        lastMessMap = new HashMap<>();
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist, parent,false);
       return new MyHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String hisUid = userList.get(position).getUid();
        String userImage = userList.get(position).getImage();
        String userName = userList.get(position).getName();
        String lastMess = lastMessMap.get(hisUid);
        holder.nameTv.setText(userName);
        if (lastMess==null || lastMess.equals("default")) {
            holder.lastMessTv.setVisibility(View.GONE);
        }
        else {

            holder.lastMessTv.setVisibility(View.VISIBLE);
            holder.lastMessTv.setText(lastMess);
        }
        try {
            Picasso.get().load(userImage).placeholder(R.drawable.ic_default_imgg).into(holder.profileIv);
        }catch (Exception e) {
            Picasso.get().load(R.drawable.ic_default_imgg).into(holder.profileIv);
        }
        if (userList.get(position).getOnlineStatus().equals("online")) {
            holder.onlineStatusTv.setImageResource(R.drawable.circle_online);
        }
        else {
            holder.onlineStatusTv.setImageResource(R.drawable.circle_offline);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", hisUid);
                context.startActivity(intent);
            }
        });
    }

    public void setLastMessMap(String userId, String lastMess) {
        lastMessMap.put(userId,lastMess);
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{
        ImageView profileIv,onlineStatusTv;
        TextView nameTv, lastMessTv;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileIv  =itemView.findViewById(R.id.profileIv);
            onlineStatusTv = itemView.findViewById(R.id.onlineStatusTv);
            nameTv = itemView.findViewById(R.id.nameTv);
            lastMessTv = itemView.findViewById(R.id.lastMessTv);
        }
    }
}
