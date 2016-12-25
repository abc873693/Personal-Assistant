package rainvisitor.personal_assistant;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import rainvisitor.personal_assistant.DetailScheduleFragmet.AddFragment;
import rainvisitor.personal_assistant.DetailScheduleFragmet.ContentFragment;
import rainvisitor.personal_assistant.DetailScheduleFragmet.MainFragment;
import rainvisitor.personal_assistant.libs.Utils;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class DetailScheduleActivity extends AppCompatActivity implements
        MainFragment.OnFragmentInteractionListener
        , AddFragment.OnFragmentInteractionListener
        , ContentFragment.OnFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public enum FRAGMENT {
        main,
        add,
        content,
        accountdetail,
        discussdetail
    }

    public CollapsingToolbarLayout collapsingToolbar;
    public TextView textView_location;
    public android.support.v7.widget.Toolbar toolbar;
    public CoordinatorLayout linearLayout;
    public NestedScrollView nestedScrollView;
    public AppBarLayout appBarLayout;
    public FRAGMENT CurrentFragment = FRAGMENT.main;
    public MenuItem menuItem_add, menuItem_share;
    public String current_activity_uid;
    public String current_schedule_uid;
    public Location mLocation;
    public Location eLocation;
    private FloatingActionButton fab;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_schedule);
        if (getIntent().getExtras() != null) {
            current_activity_uid = getIntent().getExtras().getString("activity_uid");
        } else current_activity_uid = "null";
        linearLayout = (CoordinatorLayout) findViewById(R.id.activity_detail_schedule);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        nestedScrollView = (NestedScrollView) findViewById(R.id.NestedScrollView);
        textView_location = (TextView) findViewById(R.id.textView_location);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        //collapsingToolbar.setTitle("Title");
        collapsingToolbar.setExpandedTitleGravity(Gravity.BOTTOM);
        initToolbar();
        changeContent(FRAGMENT.main);
        setUpGoogleApiClient();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //you can leave it empty
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuItem_add = menu.findItem(R.id.action_add);
        menuItem_share = menu.findItem(R.id.action_share);
        Log.d("toolbar menu", "initial");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    private void initToolbar() {
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        // Inflate a menu to be displayed in the toolbar
        toolbar.inflateMenu(R.menu.toolber_detail);
        onCreateOptionsMenu(toolbar.getMenu());
        // Set an OnMenuItemClickListener to handle menu item clicks
        toolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_share:
                        String str_share = "安安你好";
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, str_share);
                        startActivity(Intent.createChooser(sharingIntent, "分享至"));
                        break;
                    case R.id.action_add:
                        changeContent(FRAGMENT.add);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        Log.d("toolbar", getTitle().toString());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar snackbar = Snackbar
                        .make(linearLayout, "Click ToggleButton", Snackbar.LENGTH_LONG)
                        .setAction("確定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                snackbar.show();*/
                changeContent(FRAGMENT.main);

            }
        });
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permission = ActivityCompat.checkSelfPermission(DetailScheduleActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // 無權限，向使用者請求
                    ActivityCompat.requestPermissions(DetailScheduleActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    /*mLocationRequest = LocationRequest.create();
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    mLocationRequest.setInterval(1000);*/
                    FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, DetailScheduleActivity.this);
                    Location location = FusedLocationApi.getLastLocation(mGoogleApiClient);
                    mLocation = location;
                    eLocation = mLocation;
                    Utils.startNavigationActivity(DetailScheduleActivity.this, mLocation.getLatitude(), mLocation.getLongitude(), eLocation.getLatitude() + 10, eLocation.getLongitude());
                }
                /*Location location = FusedLocationApi.getLastLocation(mGoogleApiClient);
                mLocation = location;
                eLocation = mLocation;
                Utils.startNavigationActivity(DetailScheduleActivity.this, mLocation.getLatitude(), mLocation.getLongitude(), eLocation.getLatitude() + 10, eLocation.getLongitude());*/
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        if (!Utils.checkGPSisOpen(this)) {
            final Snackbar snackbar = Snackbar
                    .make(linearLayout, "請打開GPS", Snackbar.LENGTH_INDEFINITE)
                    .setAction("確定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
            snackbar.show();
        } else {
            FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    synchronized void setUpGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.e("onBackPressed", CurrentFragment.toString());
        switch (CurrentFragment) {
            case main:
                finish();
                break;
            case content:
            case add:
                changeContent(FRAGMENT.main);
                break;
            default:
                super.onBackPressed();
                break;
        }

    }

    public void changeContent(FRAGMENT position) {
        Fragment fragment = null;
        FragmentTransaction fragTrans = getFragmentManager().beginTransaction();
        switch (position) {
            case main:
                fragment = new MainFragment().newInstance(current_activity_uid);
                toolbar.setNavigationIcon(null);
                appBarLayout.setExpanded(true, true);
                ViewCompat.setNestedScrollingEnabled(nestedScrollView, true);
                menuItem_add.setVisible(true);
                menuItem_share.setVisible(true);
                break;
            case content:
                fragment = new ContentFragment().newInstance();
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
                break;
            case add:
                fragment = new AddFragment().newInstance();
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
                appBarLayout.setExpanded(false, true);
                ViewCompat.setNestedScrollingEnabled(nestedScrollView, false);
                menuItem_add.setVisible(false);
                menuItem_share.setVisible(false);
                break;
            default:

                break;
        }
        if (fragment != null) {
            fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            FragmentManager fragmentManager = getFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentByTag(CurrentFragment.toString());
            if (currentFragment != null) {
                fragTrans.remove(currentFragment);
            }
            fragTrans.replace(R.id.content_main, fragment, position.toString());
            fragTrans.commit();
        }
        CurrentFragment = position;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {

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
