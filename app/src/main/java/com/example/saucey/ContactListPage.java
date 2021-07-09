package com.example.saucey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ContactListPage extends AppCompatActivity implements View.OnClickListener{
     private FloatingActionButton contactCreate;
     private SwipeRefreshLayout swipeRefreshLayout;
     private RecyclerView recyclerView;
     public CLAdapter clAdapter;
     private DatabaseReference firebaseDatabase;
     private FirebaseAuth mAuth;
     private Boolean isLoading = false;
     private String key = null;
     private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list_page);
        contactCreate = findViewById(R.id.contactCreate);
        contactCreate.setOnClickListener(this);
        swipeRefreshLayout = findViewById(R.id.swip);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        clAdapter = new CLAdapter(this);
//        RecyclerView.AdapterDataObserver defaultObserver = new RecyclerView.AdapterDataObserver(){
//            @Override
//            public void onChanged() {
//                Intent intent = getIntent();
//                finish();
//                startActivity(intent);
//            }
//        };
//        clAdapter.registerAdapterDataObserver(defaultObserver);
        recyclerView.setAdapter(clAdapter);
        mAuth = FirebaseAuth.getInstance();
        String[] id = mAuth.getCurrentUser().getEmail().split("@");
        userID = id[0];
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("contactList");
        loadData();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
               LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
               int totalItems = linearLayoutManager.getItemCount();
               int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
               if(totalItems < lastVisibleItem + 3){
                       if(!isLoading){
                           isLoading = true;
                           loadData();
                       }
                  }
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if(direction == ItemTouchHelper.LEFT){
                swipeRefreshLayout.setRefreshing(true);
                firebaseDatabase.child(clAdapter.list.get(position).fullName).removeValue();
                clAdapter.notifyItemRemoved(position);
                swipeRefreshLayout.setRefreshing(false);
                FirebaseDatabase.getInstance().getReference("Users").child(userID).child("EmailList").child(clAdapter.list.get(position).fullName).removeValue();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
              Toast.makeText(ContactListPage.this,"Swiped!" + clAdapter.list.get(position).fullName, Toast.LENGTH_SHORT).show();
             }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ContactListPage.this,R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_cancel_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);
       get(key).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               ArrayList<Contact> contacts = new ArrayList<>();
               for(DataSnapshot data: snapshot.getChildren()){
                    Contact contact = data.getValue(Contact.class);
                    contacts.add(contact);
                    key = data.getKey();
               }
               clAdapter.setItems(contacts);
               clAdapter.notifyDataSetChanged();
               isLoading = false;
               swipeRefreshLayout.setRefreshing(false);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
               swipeRefreshLayout.setRefreshing(false);
           }
       });
    }

    public Query get(String key){
        if(key == null){
            return firebaseDatabase.orderByKey().limitToFirst(8);
        }
        return firebaseDatabase.orderByKey().startAfter(key).limitToFirst(8);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.contactCreate:
                startActivity(new Intent(ContactListPage.this,ContactCreation.class));
        }
    }
}