package rainvisitor.personal_assistant;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import rainvisitor.personal_assistant.DetailScheduleFragmet.AddFragment;
import rainvisitor.personal_assistant.DetailScheduleFragmet.ContentFragment;
import rainvisitor.personal_assistant.DetailScheduleFragmet.MainFragment;

import static android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE;

public class DetailScheduleActivity extends Activity implements
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_schedule);
        linearLayout = (LinearLayout) findViewById(R.id.activity_detail_schedule);
        initToolbar();
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
                        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
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
                toolbar.setNavigationIcon(null);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            changeContent(FRAGMENT.main);
        }
        return false;
    }

    public void changeContent(FRAGMENT position) {
        Fragment fragment = null;
        CurrentFragment = position;
        switch (position) {
            case main:
                fragment = new MainFragment().newInstance();
                break;
            case content:
                fragment = new ContentFragment().newInstance();
                break;
            case add:
                fragment = new AddFragment().newInstance();
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

}
