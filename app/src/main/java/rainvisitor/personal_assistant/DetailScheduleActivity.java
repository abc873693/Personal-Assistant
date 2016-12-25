package rainvisitor.personal_assistant;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import rainvisitor.personal_assistant.DetailScheduleFragmet.AddFragment;
import rainvisitor.personal_assistant.DetailScheduleFragmet.ContentFragment;
import rainvisitor.personal_assistant.DetailScheduleFragmet.MainFragment;

public class DetailScheduleActivity extends AppCompatActivity implements
        MainFragment.OnFragmentInteractionListener
        , AddFragment.OnFragmentInteractionListener
        , ContentFragment.OnFragmentInteractionListener {
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

}
