package rainvisitor.personal_assistant;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class AddScheduleActivity extends Activity {
    private android.support.v7.widget.Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        initToolbar();
    }
    private void initToolbar(){
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        // Set an OnMenuItemClickListener to handle menu item clicks
        toolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_cancel:
                        finish();
                        break;
                    case R.id.action_save:
                        saveActivity();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        // Inflate a menu to be displayed in the toolbar
        toolbar.inflateMenu(R.menu.schedule_add_menu);
        //setSupportActionBar(toolbar);
    }

    private void saveActivity() {
        
    }
}
