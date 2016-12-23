package rainvisitor.personal_assistant;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddScheduleActivity extends AppCompatActivity {
    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
        initToolbar();
    }

    private void initToolbar() {
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        // Set an OnMenuItemClickListener to handle menu item clicks
        toolbar.setTitle("新增行程");
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
        toolbar.inflateMenu(R.menu.toolber_add);
        //setSupportActionBar(toolbar);
    }

    public long count = 0;

    private void saveActivity() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if (snap.getKey() == "activity") count = snap.getChildrenCount();
                    Log.e("count", count + "");
                }
                DatabaseReference mDatabase;
                EditText title = (EditText) findViewById(R.id.edittext_title);
                EditText content = (EditText) findViewById(R.id.edittext_content);
                TextView location = (TextView) findViewById(R.id.textview_location);
                TextView startdate = (TextView) findViewById(R.id.textview_startdate);
                TextView starttime = (TextView) findViewById(R.id.textview_starttime);
                TextView enddate = (TextView) findViewById(R.id.textview_enddate);
                TextView endtime = (TextView) findViewById(R.id.textview_endtime);
                mDatabase = FirebaseDatabase.getInstance().getReference();
                if (location.getText() + "" == "" || content.getText() + "" == "" || title.getText() + "" == "" ||
                        startdate.getText() + "" == "" || starttime.getText() + "" == "" || enddate.getText() + "" == "" || endtime.getText() + "" == "") {
                    LinearLayout addschedule = (LinearLayout) findViewById(R.id.linelayout_addschedule);
                    final Snackbar snackbar = Snackbar
                            .make(addschedule, "請物留空", Snackbar.LENGTH_INDEFINITE)
                            .setAction("確定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                    snackbar.show();
                } else {
                    mDatabase.child("activity").child((count + 1) + "").child("location").setValue(location.getText() + "");
                    mDatabase.child("activity").child((count + 1) + "").child("content").setValue(content.getText() + "");
                    mDatabase.child("activity").child((count + 1) + "").child("title").setValue(title.getText() + "");
                    mDatabase.child("activity").child((count + 1) + "").child("time").child("starttime").setValue(startdate.getText() + " " + starttime.getText() + "");
                    mDatabase.child("activity").child((count + 1) + "").child("time").child("endtime").setValue(enddate.getText() + " " + endtime.getText() + "");
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
