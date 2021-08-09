package com.example.saucey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import static android.content.ContentValues.TAG;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactListPage extends AppCompatActivity implements View.OnClickListener, Nearby {
    private FloatingActionButton contactCreate;
    private FloatingActionButton nearbyBtn;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    public CLAdapter clAdapter;
    private DatabaseReference firebaseDatabase;
    private FirebaseAuth mAuth;
    private Boolean isLoading = false;
    private String key = null;
    private String userID;
    private Boolean networkPermissionGranted = false;
    double latitude, longitude;
    int PROXIMITY_RADIUS = 10000;
    final int COARSE_LOCATION = 99, FINE_LOCATION = 100;
//    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//    Location location;
    FusedLocationProviderClient fusedLocationProviderClient;
    List<HashMap<String,String>> listOfPlaces;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,10,locationListener);
        setContentView(R.layout.activity_contact_list_page);
        contactCreate = findViewById(R.id.contactCreate);
        nearbyBtn = findViewById(R.id.contactFetch);
        contactCreate.setOnClickListener(this);
        nearbyBtn.setOnClickListener(this);
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
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItems = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (totalItems < lastVisibleItem + 3) {
                    if (!isLoading) {
                        isLoading = true;
                        loadData();
                    }
                }
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.LEFT) {
                swipeRefreshLayout.setRefreshing(true);
                firebaseDatabase.child(clAdapter.list.get(position).fullName).removeValue();
                clAdapter.notifyItemRemoved(position);
                swipeRefreshLayout.setRefreshing(false);
                FirebaseDatabase.getInstance().getReference("Users").child(userID).child("EmailList").child(clAdapter.list.get(position).fullName).removeValue();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                Toast.makeText(ContactListPage.this, "Swiped!" + clAdapter.list.get(position).fullName, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ContactListPage.this, R.color.red))
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
                for (DataSnapshot data : snapshot.getChildren()) {
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

    public Query get(String key) {
        if (key == null) {
            return firebaseDatabase.orderByKey().limitToFirst(8);
        }
        return firebaseDatabase.orderByKey().startAfter(key).limitToFirst(8);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contactCreate:
                startActivity(new Intent(ContactListPage.this, ContactCreation.class));
                break;
            case R.id.contactFetch:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION);
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION);
                    }

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, COARSE_LOCATION);
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, COARSE_LOCATION);
                    }
                } else {
                    getLastLocation();
                }
                if(!networkPermissionGranted){
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION);
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION);
                    }

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, COARSE_LOCATION);
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, COARSE_LOCATION);
                    }
                }
                if(networkPermissionGranted){
                    String eye = "doctor";
                    String url = getURL(latitude, longitude, eye);
                    Toast.makeText(ContactListPage.this, url, Toast.LENGTH_SHORT).show();
                    Object dataTransfer[] = new Object[1];
                    dataTransfer[0] = url;
                    Toast.makeText(ContactListPage.this,"Lat: "+latitude,Toast.LENGTH_SHORT).show();
                    Log.e(TAG, url);
                    GetNearbyFacilitiesData getNearbyFacilitiesData = new GetNearbyFacilitiesData();
                    getNearbyFacilitiesData.delegate = this;
                    getNearbyFacilitiesData.execute(dataTransfer);
                }else{
                    Toast.makeText(ContactListPage.this,"Please Grant Location Permissions In Your Settings",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private String getURL(double latitude, double longitude, String eye) {
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location=" + latitude + "," + longitude);
        stringBuilder.append("&radius=" + PROXIMITY_RADIUS);
        stringBuilder.append("&type=" + eye);
        stringBuilder.append("&key=" + "AIzaSyD34X8apcKr0YY8jWFMh6gUvHvkj-IngcU");
        return stringBuilder.toString();
    }

    @Override
    protected void onStart() {
       super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION);
            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, COARSE_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, COARSE_LOCATION);
            }
        } else {
            getLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation();
                    networkPermissionGranted = true;
                } else {
                    Toast.makeText(ContactListPage.this,"Cannot Proceed Without Location Permission",Toast.LENGTH_SHORT).show();
                    networkPermissionGranted = false;
                }
        }
    }

    private void getLastLocation() {
//        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        latitude = location.getLatitude();
//        longitude = location.getLongitude();
//        LocationListener locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                longitude = location.getLongitude();
//                latitude = location.getLatitude();
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//
//            }
//        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    Toast.makeText(ContactListPage.this,"Lat: " + latitude,Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ContactListPage.this,"location was null",Toast.LENGTH_SHORT).show();
                }
            }
        });
        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ContactListPage.this,"error getting last location",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void sendData(List<HashMap<String, String>> NearbyPlaces) {
        Log.e(TAG, "sendData = "+NearbyPlaces.toString());
        String[] facilities = new String[9];
        int i = 0;
        for(HashMap<String,String> facility: NearbyPlaces){
            if(i == 9){
                break;
            }
            facilities[i] = facility.get("place_name");
            facilities[i + 1] = facility.get("vicinity");

            facilities[i + 2] = distance(latitude,longitude, Double.parseDouble(facility.get("lat")),Double.parseDouble(facility.get("lng")));

            i += 3;
        }
        Intent intent = new Intent(ContactListPage.this, NearbyLocations.class);
        intent.putExtra("locations", facilities);
        startActivity(intent);
    }

    private String distance(double latitude, double longitude, double lat, double lng) {
        double lat1 = Math.toRadians(latitude);
        double lng1 = Math.toRadians(longitude);
        double lat2= Math.toRadians(lat);
        double lng2 = Math.toRadians(lng);

        double dlat = lat2 - lat1;
        double dlong = lng2 - lng1;

        double a = Math.pow(Math.sin(dlat/2),2) + Math.cos(lat1) * Math.cos(lat2) + Math.pow(Math.sin(dlong/2),2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double r = 6371;

        return String.valueOf(c*r);
    }
}