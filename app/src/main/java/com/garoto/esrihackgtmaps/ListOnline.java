package com.garoto.esrihackgtmaps;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.esri.arcgisruntime.mapping.view.MapView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.net.IDN;

public class ListOnline extends AppCompatActivity {

    //Firebase

    DatabaseReference onlineRef, currentUserRef, counterRef;
    FirebaseRecyclerAdapter<User, ListOnlineViewHolder> adapter;

    RecyclerView listOnline;
    RecyclerView.LayoutManager layoutManager;

    private LocationRequest mLocationRequest;
//    private MapView mMapView;
    private LocationRequest locationRequest;


    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISTANCE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_online);
//        mMapView = (MapView) findViewById(R.id.mapView);
        //Init View
        listOnline = (RecyclerView) findViewById(R.id.listOnline);
//        listOnline.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listOnline.setLayoutManager(layoutManager);


        //Set Toolbar and Logout / Join Menu
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
//        toolbar.setTitle("We Them Boyz Presence System");
//        setSupportActionBar(toolbar);


//        Firebase
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        counterRef = FirebaseDatabase.getInstance().getReference("lastOnline"); //Create new child name lastOnline
        currentUserRef = FirebaseDatabase.getInstance().getReference("lastOnline")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()); //Create new child in lastOnline with key.

        setUpSystem();

        //After setup system, we just load all user from counterRef and display RecyclerView
        //online list
        updateList();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(DISTANCE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void displayLocation() {

//        if (ActivityCompat.checkSelfPermission(this,Mamifest.permission.ACE))
    }

    private void setUpSystem() {
        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class)) {
                    currentUserRef.onDisconnect().removeValue();
                    counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(), "Online"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateList() {

        FirebaseRecyclerOptions<User> userOptions = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(counterRef, User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, ListOnlineViewHolder>(userOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ListOnlineViewHolder viewHolder, int position, @NonNull final User model) {
                if (model.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                    viewHolder.txtEmail.setText(model.getEmail() + " ME ");
                else
                    viewHolder.txtEmail.setText(model.getEmail());

//                viewHolder.itemClickListener = (ItemClickListener) (view, position){
//                    if (!model.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
//                        Intent map = new Intent(ListOnline.this, MainActivity.class);
//                        map.putExtra("email", model.getEmail());
//                        map.putExtra("lat", mLastLocation.getLatitute());
//                        map.putExtra("lng", mLastLocation.getLongitude());
//                    }
//
//                    startActivity(map);
//                }


            }

            @Override
            public ListOnlineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.user_layout, parent, false);

                return new ListOnlineViewHolder(itemView);
            }
        };


        adapter.startListening();
        listOnline.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_join:
                counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(), "Online"));
                break;

            case R.id.action_log_out:
                currentUserRef.removeValue(); //Delete old value
                break;

            case R.id.maps:
                setContentView(R.layout.activity_main);

        }

        return super.onOptionsItemSelected(item);
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RES_REQUEST).show();
            } else {
                Toast.makeText(this, "Not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }

        return true;
    }


    protected void onStop() {
        if (adapter != null)
            adapter.stopListening();
        super.onStop();
    }
}
