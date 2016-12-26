package rainvisitor.personal_assistant;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import rainvisitor.personal_assistant.Database.SP_Service;
import rainvisitor.personal_assistant.libs.ImageDownloaderTask;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ImageView user_image = (ImageView) findViewById(R.id.user_photo);
        TextView user_name = (TextView) findViewById(R.id.ueser_name);
        TextView user_email = (TextView) findViewById(R.id.user_mail);
        SP_Service sp_service = new SP_Service(SettingActivity.this);
        if(sp_service.getLoginState()) {
            user_name.setText(sp_service.username_get());
            user_email.setText(sp_service.userEmail_get());
            new ImageDownloaderTask(user_image).execute(sp_service.userPhotoURL_get());
        }
    }
}
