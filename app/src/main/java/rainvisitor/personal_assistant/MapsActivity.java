package rainvisitor.personal_assistant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import rainvisitor.personal_assistant.libs.Utils;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingSearchView mSearchView;
    private Location mLocation;
    private RelativeLayout relateLayout;
    private LatLng Slect_LatLng;
    private String Slect_Name;
    private Boolean Slect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        relateLayout = (RelativeLayout) findViewById(R.id.relateLayout);
        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                String g = oldQuery;
                Geocoder geocoder = new Geocoder(getBaseContext());
                List<Address> addresses = null;

                try {
                    // Getting a maximum of 3 Address that matches the input
                    // text
                    addresses = geocoder.getFromLocationName(g, 3);
                    if (addresses != null && !addresses.toString().equals("")) {
                        for (Address i : addresses) {
                            Log.d("addresses", i.toString());
                        }
                        if (addresses.size() != 0) search(addresses);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //get suggestions based on newQuery

                //pass them on to the search view
                //mSearchView.swapSuggestions(newSuggestions);
            }
        });
        mapFragment.getMapAsync(this);
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_location:
                        GetMyLocation();
                        break;
                    case R.id.action_OK:
                        SendData();
                        break;
                    default:
                        break;
                }

            }
        });

    }

    private void GetMyLocation() {
        mMap.clear();
        Slect = true;
        mLocation = mMap.getMyLocation();
        LatLng sydney = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(sydney).title("您的位置"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        Slect_LatLng = sydney;
        Slect_Name ="您的位置";
    }

    private void SendData() {
        if (Slect) {
            Intent resultIntent = new Intent();
            // TODO Add extras or a data URI to this intent as appropriate.
            resultIntent.putExtra("Longitude", Slect_LatLng.longitude);
            resultIntent.putExtra("Latitude", Slect_LatLng.latitude);
            resultIntent.putExtra("Name", Slect_Name);
            setResult(Utils.RESULT_LOCATION, resultIntent);
            finish();
        } else {
            final Snackbar snackbar = Snackbar
                    .make(relateLayout, "請選取一個紅標", Snackbar.LENGTH_INDEFINITE)
                    .setAction("確定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
            snackbar.show();
        }
    }


    protected void search(List<Address> addresses) {
        Address address = (Address) addresses.get(0);
        Double home_long = address.getLongitude();
        Double home_lat = address.getLatitude();
        LatLng latLng = new LatLng(home_lat, home_long);
        String addressText = String.format(
                "%s, %s",
                address.getMaxAddressLineIndex() > 0 ? address
                        .getAddressLine(0) : "", address.getCountryName());
        Log.e("address", address.toString());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(addressText);

        mMap.clear();
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        Log.d("address", "Latitude:" + address.getLatitude() + ", Longitude:"
                + address.getLongitude());
        final Snackbar snackbar = Snackbar
                .make(relateLayout, address.getFeatureName(), Snackbar.LENGTH_INDEFINITE)
                .setAction("確定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        snackbar.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            GetMyLocation();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.e("onMapReady", "無法定位");
            return;
        } else {
            try {
                GetMyLocation();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                Slect = true;
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(point);
                markerOptions.title("您選取的位置");
                Slect_LatLng = point;
                Slect_Name ="您選取的位置";
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                final Snackbar snackbar = Snackbar
                        .make(relateLayout, point.toString(), Snackbar.LENGTH_INDEFINITE)
                        .setAction("確定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                snackbar.show();
            }
        });
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng newLocation = marker.getPosition();
                mLocation.setLatitude(newLocation.latitude);
                mLocation.setLongitude(newLocation.longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 15.0f));
                Slect_LatLng = newLocation;
                Slect_Name = marker.getTitle();
                Slect = true;
            }

            @Override
            public void onMarkerDragStart(Marker marker) {
            }

        });
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
}
