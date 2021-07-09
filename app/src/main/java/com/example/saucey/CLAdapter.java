package com.example.saucey;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class CLAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    public List<Contact> list;
    private boolean tan = false;

    public CLAdapter(Context context){
        this.context = context;
        this.list = new ArrayList<>();
    }

    public void setItems(ArrayList<Contact> contacts){
     list.addAll(contacts);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(context).inflate(R.layout.contact_list,parent,false);
        return new contactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
             int i = 0;
             contactViewHolder contactViewHolder = (contactViewHolder) holder;
             Contact contact = list.get(position);
             contactViewHolder.name.setText(contact.fullName);
             contactViewHolder.description.setText(contact.email);
             Picasso.get().load(contact.retrievalCode).into(contactViewHolder.pimage);

             if(!tan){
                 contactViewHolder.card.setCardBackgroundColor(0xFFFFFF);
                 this.tan = true;
             }else{
                 contactViewHolder.card.setCardBackgroundColor(0xFAF9FE);
                 this.tan = false;
             }

              final String str = contact.fullName;

             contactViewHolder.card.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent intent = new Intent(context.getApplicationContext(),Profile.class);
                     intent.putExtra("selected_name",str);
                     context.startActivity(intent);
                 }
             });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
