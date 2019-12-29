package com.example.projet_wear;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayDeque;

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.RecyclerViewHolder> implements Serializable{

    private ArrayDeque<MessageReceiver> dataSource;

    private Context context;
    RecyclerViewHolder recyclerViewHolder;
    ViewGroup view;

    public MainMenuAdapter(Context context, ArrayDeque<MessageReceiver> dataArgs){
        this.context = context;
        this.dataSource = dataArgs;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_list_item,parent,false);

        this.view = parent;
        recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        ConstraintLayout menuContainer;

        TextView studentIDView;
        TextView messageView;

        TextView studentIDView_R;
        TextView messageView_R;

        Guideline guideline;

        public RecyclerViewHolder(View view) {
            super(view);

            menuContainer = view.findViewById(R.id.menu_container);
            studentIDView = view.findViewById(R.id.menu_studentid);
            messageView = view.findViewById(R.id.menu_message);

            studentIDView_R = view.findViewById(R.id.menu_R_studentid);
            messageView_R = view.findViewById(R.id.menu_R_message);

            guideline = view.findViewById(R.id.middleline);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        if(getItemCount() > 0) {

            MessageReceiver data_provider = (MessageReceiver) dataSource.toArray()[position];

            data_provider.setMessageView(this.context,holder);

            holder.menuContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    MessageReceiver m = (MessageReceiver) dataSource.toArray()[position];

                    Intent intent = new Intent(context, MessageActivity.class);
                    intent.putExtra("message", m);

                    context.startActivity(intent);

                }
            });
        }
    }

    
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

}