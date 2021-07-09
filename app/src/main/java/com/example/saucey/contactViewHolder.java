package com.example.saucey;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class contactViewHolder extends RecyclerView.ViewHolder {

     public TextView name,description;
     public CardView card;
    public CircleImageView pimage;
    public contactViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.contact_list_name);
        description = itemView.findViewById(R.id.contact_list_email);
        card = itemView.findViewById(R.id.card);
        pimage = itemView.findViewById(R.id.contact_list_image);
    }
}
