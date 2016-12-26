package rainvisitor.personal_assistant.Drawer;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import rainvisitor.personal_assistant.MainActivity;
import rainvisitor.personal_assistant.Models.AllScheduleModel;
import rainvisitor.personal_assistant.R;
import rainvisitor.personal_assistant.libs.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RestaurantFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RestaurantFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String USER_UID = "4wCRmeLUdtUBREByNn1GHFdFsnl2";
    private static final String DATABASE_TAG = "Firebase Database";
    private MainActivity mainActivity;
    private Context context;
    private MapView mapView;
    private GoogleMap mMap;
    private Location mLocation;
    private RelativeLayout frameLayout;
    private LatLng Slect_LatLng;
    private String Slect_Name;
    private Boolean Slect = false;
    private ArrayList<AllScheduleModel> lists = new ArrayList<>();
    private int index = -1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RestaurantFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RestaurantFragment.
     */
    // TODO: Rename and change types and number of parameters
    public RestaurantFragment newInstance(String param1, String param2) {
        RestaurantFragment fragment = new RestaurantFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);
       /* SupportMapFragment mapFragment = (SupportMapFragment) view.getSupportFragmentManager()
                .findFragmentById(R.id.map);*/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid;
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            uid = user.getUid().toString();
            USER_UID = user.getUid().toString();
            Log.e("getCurrentUser", "uid = " + uid + "  name = " + name + "  email = " + email + "  photoUrl = " + photoUrl);
        } else uid = "0";
        mainActivity = (MainActivity) getActivity();
        mapView = (MapView) view.findViewById(R.id.map);
        frameLayout = (RelativeLayout) view.findViewById(R.id.frameLayout);
        context = getActivity();
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // 無權限，向使用者請求
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    // GetMyLocation();
                    mMap.setMyLocationEnabled(true);
                }
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    Log.e("onMapReady", "無法定位");
                    return;
                } else {
                    try {
                        mMap.setMyLocationEnabled(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        String tmp = String.valueOf(marker.getZIndex());
                        index = Integer.parseInt(tmp);
                        LatLng newLocation = marker.getPosition();
                        mLocation.setLatitude(newLocation.latitude);
                        mLocation.setLongitude(newLocation.longitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 15.0f));
                        Slect_LatLng = newLocation;
                        Slect_Name = marker.getTitle();
                        Slect = true;
                        AllScheduleModel model = lists.get(index);
                        Boolean s = model.join;
                        final Snackbar snackbar = Snackbar
                                .make(frameLayout, s ? "已參加" : "參加", Snackbar.LENGTH_INDEFINITE)
                                .setAction("確定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AllScheduleModel model = lists.get(index);
                                        if(!model.join){
                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            DatabaseReference myRef = database.getReference();
                                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                    DatabaseReference userDatabase = dataSnapshot.child("users").child(user.getUid()).getRef();
                                                    long count_activity = dataSnapshot.child("users").child(user.getUid()).child("activtys").getChildrenCount();
                                                    Log.e("count_activity", "count_activity" + count_activity);
                                                    userDatabase.child("activtys").child((count_activity + 1) + "").child("uid").setValue(lists.get(index).num);
                                                    lists.get(index).join = true;
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                });
                        snackbar.show();
                    }

                    @Override
                    public void onMarkerDragStart(Marker marker) {
                    }

                });
                mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        mMap.clear();
                        for (int i = 0; i < lists.size(); i++) {
                            AllScheduleModel model = lists.get(i);
                            if (model.latLng != null) {
                                mLocation = mMap.getMyLocation();
                                LatLng lng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                                if (Utils.isNear(model.latLng, lng)) {
                                    mMap.addMarker(new MarkerOptions().zIndex(i).position(model.latLng).title(model.title));
                                }
                            }
                        }
                        return false;
                    }
                });
            }
        });
        getUserActivity();
        // Inflate the layout for this fragment
        return view;
    }

    private void GetMyLocation() {
        Slect = true;
        mLocation = mMap.getMyLocation();
        LatLng sydney = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(sydney).title("您的位置"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        Slect_LatLng = sydney;
        Slect_Name = "您的位置";
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        mMap.setMyLocationEnabled(true);
                        //GetMyLocation();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void getUserActivity() {
        lists.clear();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        Log.e(DATABASE_TAG, "getUserActivity...");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.child("activity").getChildren()) {
                    AllScheduleModel model = new AllScheduleModel();
                    String num = ds.getKey();
                    model.num = num;
                    model.join = false;
                    model.title = ds.child("title").getValue().toString();
                    model.content = ds.child("content").getValue().toString();
                    model.date_begin = Long.parseLong(ds.child("time").child("begin").getValue().toString());
                    model.date_end = Long.parseLong(ds.child("time").child("end").getValue().toString());
                    model.location = ds.child("location").child("name").getValue().toString();
                    Double Latitude = Double.parseDouble(ds.child("location").child("Latitude").getValue().toString());
                    Double Longitude = Double.parseDouble(ds.child("location").child("Longitude").getValue().toString());
                    if (!(Latitude == 0 && Longitude == 0)) {
                        model.latLng = new LatLng(Latitude, Longitude);
                    }
                    String ID = ds.child("members").child("0").child("uid").getValue().toString();
                    for (DataSnapshot dss : dataSnapshot.child("users").getChildren()) {
                        if (dss.getKey().equals(ID)) {
                            model.creator = dss.child("name").getValue().toString();
                            Log.e("Bingo", model.creator);
                            break;
                        }
                    }
                    long count = 0;
                    count = dataSnapshot.child("users").child(USER_UID).child("activtys").getChildrenCount();
                    for (long i = 1; i <= count; i++) {
                        String get = dataSnapshot.child("users").child(USER_UID).child("activtys").child(i + "").child("uid").getValue().toString();
                        if (get.equals(num)) {
                            model.join = true;
                            Log.e("Get", get);
                            break;
                        }
                    }
                    Log.e("List", model.join + ":");
                    //adapter.add(ds.child("name").getValue().toString());
                    lists.add(model);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(DATABASE_TAG, "Failed to read value.", error.toException());
            }
        });
    }


}
