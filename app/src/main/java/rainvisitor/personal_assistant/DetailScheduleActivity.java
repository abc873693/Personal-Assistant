package rainvisitor.personal_assistant;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import rainvisitor.personal_assistant.DetailScheduleFragmet.AddFragment;
import rainvisitor.personal_assistant.DetailScheduleFragmet.ContentFragment;
import rainvisitor.personal_assistant.DetailScheduleFragmet.MainFragment;

import static android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE;

public class DetailScheduleActivity extends AppCompatActivity implements
        MainFragment.OnFragmentInteractionListener
        , AddFragment.OnFragmentInteractionListener
        , ContentFragment.OnFragmentInteractionListener {
    public enum FRAGMENT {
        main,
        add,
        content,
    }

    private android.support.v7.widget.Toolbar toolbar;
    private LinearLayout linearLayout;
    private FRAGMENT CurrentFragment = FRAGMENT.main;
    private String current_activity_uid ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_schedule);
        if (getIntent().getExtras() != null) {
            current_activity_uid = getIntent().getExtras().getString("activity_uid");
        }
        else current_activity_uid = "null";
        linearLayout = (LinearLayout) findViewById(R.id.activity_detail_schedule);
        initToolbar();
        changeContent(FRAGMENT.main);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //you can leave it empty
    }

    private void initToolbar() {
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        // Set an OnMenuItemClickListener to handle menu item clicks
        toolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_share:
                        changeContent(FRAGMENT.content);
                        /*Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, posts.get(position).link_URL);
                        startActivity(Intent.createChooser(sharingIntent, "分享至"));*/
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
        // Inflate a menu to be displayed in the toolbar
        toolbar.inflateMenu(R.menu.toolber_detail);

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
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.e("onBackPressed",CurrentFragment.toString());
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
        switch (position) {
            case main:
                fragment = new MainFragment().newInstance(current_activity_uid);
                toolbar.setNavigationIcon(null);
                break;
            case content:
                fragment = new ContentFragment().newInstance();
                break;
            case add:
                fragment = new AddFragment().newInstance();
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
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
        CurrentFragment = position;
    }

    public void changeContent(FRAGMENT position,String param1) {
        Fragment fragment = null;
        switch (position) {
            case main:
                fragment = new MainFragment().newInstance(current_activity_uid);
                toolbar.setNavigationIcon(null);
                break;
            case content:
                fragment = new ContentFragment().newInstance(current_activity_uid,param1);
                break;
            case add:
                fragment = new AddFragment().newInstance();
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
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
        CurrentFragment = position;
    }

}
