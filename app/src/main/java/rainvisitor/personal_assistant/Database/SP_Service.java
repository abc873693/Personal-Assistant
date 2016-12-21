package rainvisitor.personal_assistant.Database;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ray on 2016/12/20.
 * 包裝SharedPreferences
 */

public class SP_Service {
    private SharedPreferences data = null;
    private final String USERNAME = "username";
    private final String USEREMAIL = "user_email";
    private final String USERPHOTOURL = "user_photoURL";

    public SP_Service(Context context){
        data = context.getSharedPreferences("data", 0);
    }

    public void username_set(String str){
        data.edit().putString(USERNAME, str).apply();
    }
    public String username_get(){
        return data.getString(USERNAME,"");
    }

    public void userEmail_set(String str){
        data.edit().putString(USEREMAIL, str).apply();
    }
    public String userEmail_get(){
        return data.getString(USEREMAIL,"");
    }

    public void userPhotoURL_set(String str){
        data.edit().putString(USERPHOTOURL, str).apply();
    }

    public String userPhotoURL_get(){
        return data.getString(USERPHOTOURL,"");
    }
}
