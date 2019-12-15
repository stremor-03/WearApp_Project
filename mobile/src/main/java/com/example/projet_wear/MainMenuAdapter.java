package com.example.projet_wear;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.RecyclerViewHolder> implements Serializable{

    private ArrayList<MessageReceiver> dataSource = new ArrayList<MessageReceiver>();

    private Context context;
    RecyclerViewHolder recyclerViewHolder;
    ArrayList<RecyclerViewHolder> displayItem;
    ViewGroup view;

    public MainMenuAdapter(Context context, ArrayList<MessageReceiver> dataArgs){
        this.context = context;
        this.dataSource = dataArgs;
        this.displayItem = new ArrayList<>();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_list_item,parent,false);

        this.view = parent;
        recyclerViewHolder = new RecyclerViewHolder(view);
        displayItem.add(recyclerViewHolder);

        return recyclerViewHolder;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        RelativeLayout menuContainer;
        TextView menuItem;
        ImageView menuIcon;

        public RecyclerViewHolder(View view) {
            super(view);
            menuContainer = view.findViewById(R.id.menu_container);
            menuItem = view.findViewById(R.id.menu_studentid);
            menuIcon = view.findViewById(R.id.menu_icon);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        if(getItemCount() > 0) {

            MessageReceiver data_provider = dataSource.get(position);

            holder.menuItem.setText(String.valueOf(data_provider.getStudent_id()));
           // holder.menuIcon.setImageResource(R.drawable.close_button);
            holder.menuContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    MessageReceiver m = dataSource.get(position);

                    Intent intent = new Intent(context, MessageActivity.class);

                    intent.putExtra("message", m);

                    context.startActivity(intent);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }




}