package rainvisitor.personal_assistant;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rainvisitor.personal_assistant.Database.SP_Service;
import rainvisitor.personal_assistant.Drawer.MoneyFragment;
import rainvisitor.personal_assistant.Drawer.NotesFragment;
import rainvisitor.personal_assistant.Drawer.RestaurantFragment;
import rainvisitor.personal_assistant.Drawer.SchedulesFragment;
import rainvisitor.personal_assistant.Drawer.StartFragment;
import rainvisitor.personal_assistant.libs.ImageDownloaderTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NotesFragment.OnFragmentInteractionListener,
        RestaurantFragment.OnFragmentInteractionListener,
        SchedulesFragment.OnFragmentInteractionListener,
        MoneyFragment.OnFragmentInteractionListener,
        StartFragment.OnFragmentInteractionListener {
    FrameLayout frameLayout;
    private ImageView user_image;
    private TextView user_name, user_email;
    private FloatingActionButton fab;
    private int CurrentFragment = 0;
    private static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("application/xml; charset=utf-8");
    private final int REQUEST_Voice_Recognition = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_Voice_Recognition) {
            if (resultCode == RESULT_OK) {
                //把所有辨識的可能結果印出來看一看，第一筆是最 match 的。
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String all = "";
                for (String r : result) {
                    all = all + r + "\n";
                    new AsyncGetCKIP().execute(r);
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

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CurrentFragment == 0) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "請說話..."); //語音辨識 Dialog 上要顯示的提示文字
                    startActivityForResult(intent, REQUEST_Voice_Recognition);
                    new AsyncGetCKIP().execute("安安你好");
                } else if (CurrentFragment == 2) {
                    Intent Schedule_intent = new Intent(MainActivity.this, AddScheduleActivity.class);
                    startActivity(Schedule_intent);
                }
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
        SP_Service sp_service = new SP_Service(MainActivity.this);
        if(sp_service.getLoginState()) {
            user_name.setText(sp_service.username_get());
            user_email.setText(sp_service.userEmail_get());
            new ImageDownloaderTask(user_image).execute(sp_service.userPhotoURL_get());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*Intent intent = new Intent(MainActivity.this, FirebaseMessagingService.class);
        stopService(intent);*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("離開")
                    .setMessage("請問要離開程式?")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("離開", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    })
                    .show();
            //super.onBackPressed();
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
            case R.id.nav_home:
                changeContent(0);
                break;
            case R.id.nav_map:
                changeContent(4);
                break;
            case R.id.nav_setting:
                startActivity(new Intent().setClass(MainActivity.this, SettingActivity.class));
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
        CurrentFragment = position;
        switch (position) {
            case 0:
                fragment = new StartFragment().newInstance();
                fab.setVisibility(View.GONE);
                break;
            case 1:
                fragment = new NotesFragment().newInstance("記事");
                fab.setVisibility(View.GONE);
                break;
            case 2:
                fragment = new SchedulesFragment().newInstance("行程", "");
                fab.setVisibility(View.VISIBLE);
                fab.setImageResource(R.drawable.ic_add_black);
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue_500)));
                break;
            case 4:
                fragment = new RestaurantFragment().newInstance("找活動", "");
                fab.setVisibility(View.GONE);
                //startActivity(new Intent().setClass(MainActivity.this, MapsActivity.class));
                break;
            default:

                break;
        }
        if (fragment != null) {
            FragmentTransaction fragTrans = getFragmentManager().beginTransaction();
            fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            FragmentManager fragmentManager = getFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentByTag(CurrentFragment+"");
            if (currentFragment != null) {
                fragTrans.remove(currentFragment);
            }
            fragTrans.replace(R.id.content_main, fragment, position+"");
            fragTrans.commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //you can leave it empty
    }

    public class AsyncGetCKIP extends AsyncTask<String, String, String> {
        private HttpURLConnection conn = null;
        private final String HOST_IP = "140.109.19.104";
        private final int PORT = 1501;
        private final String url = "http://" + HOST_IP + ":" + PORT;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String postBody =
                        "<wordsegmentation version=\"0.1\">" +
                                "<option showcategory=\"1\" />" +
                                "<authentication username=\"abc873693\" password=\"rain05081620\" />" +
                                "<text>" + params[0] + "</text>" +
                                "</wordsegmentation>";
                OkHttpClient okHttpClient = new OkHttpClient();
                okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
                okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
                RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("Content-Type", "application/xml; charset=utf-8") //Notice this request has header if you don't need to send a header just erase this part
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.e("HttpService", "onFailure() Request was: ");

                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        String str = response.body().string();
                        Log.e("response ", "onResponse(): " + str);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                /*if (conn != null) {
                    conn.disconnect();
                }*/
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //Log.d("DISP result",result);
            try {

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "資料取得失敗!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}
