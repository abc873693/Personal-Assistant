package rainvisitor.personal_assistant;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import rainvisitor.personal_assistant.Drawer.MoneyFragment;
import rainvisitor.personal_assistant.Drawer.NotesFragment;
import rainvisitor.personal_assistant.Drawer.RestaurantFragment;
import rainvisitor.personal_assistant.Drawer.SchedulesFragment;
import rainvisitor.personal_assistant.Drawer.StartFragment;

import static android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NotesFragment.OnFragmentInteractionListener,
        RestaurantFragment.OnFragmentInteractionListener,
        SchedulesFragment.OnFragmentInteractionListener,
        MoneyFragment.OnFragmentInteractionListener,
        StartFragment.OnFragmentInteractionListener {
    FrameLayout frameLayout;
    public ImageView user_image;
    public TextView user_name, user_email;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //把所有辨識的可能結果印出來看一看，第一筆是最 match 的。
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String all = "";
                for (String r : result) {
                    all = all + r + "\n";
                }
                Log.d("resultCode", all);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = (FrameLayout) findViewById(R.id.content_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "請說話..."); //語音辨識 Dialog 上要顯示的提示文字
                startActivityForResult(intent, 1);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //LinearLayout navHeader =(LinearLayout) LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        //navigationView.addHeaderView(navHeader);
        View headerView = navigationView.getHeaderView(0);
        user_image = (ImageView) headerView.findViewById(R.id.imageView_userImage);
        user_name = (TextView) headerView.findViewById(R.id.txt_userName);
        user_email = (TextView) headerView.findViewById(R.id.txt_userEmail);
        changeContent(0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_note:
                changeContent(1);
                break;
            case R.id.nav_schedule:
                changeContent(2);
                break;
            case R.id.nav_money_management:
                changeContent(3);
                break;
            case R.id.nav_restaurant:
                changeContent(4);
                break;
            case R.id.nav_setting:
                break;
            default:
                changeContent(0);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeContent(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new StartFragment().newInstance();
                break;
            case 1:
                fragment = new NotesFragment().newInstance("記事");
                break;
            case 2:
                fragment = new SchedulesFragment().newInstance("行程", "");
                break;
            case 3:
                fragment = new MoneyFragment().newInstance("金錢管理", "");
                break;
            case 4:
                fragment = new RestaurantFragment().newInstance("找餐廳", "");
                break;
            default:

                break;
        }
        if (fragment != null) {
            FragmentTransaction fragTrans = getFragmentManager().beginTransaction();
            fragTrans.setTransition(TRANSIT_FRAGMENT_FADE);
            fragTrans.replace(R.id.content_main, fragment);
            fragTrans.commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //you can leave it empty
    }
}
