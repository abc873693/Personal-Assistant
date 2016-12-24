package rainvisitor.personal_assistant.Database;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ray on 2016/12/20.
 * 包裝SharedPreferences
 */

public class SP_Service {
    private SharedPreferences data = null;
    private final String USEUID = "user_uid";
    private final String USERNAME = "username";
    private final String USEREMAIL = "user_email";
    private final String USERPHOTOURL = "user_photoURL";
    private final String LOGINSTATE = "login_state";

    public SP_Service(Context context){
        data = context.getSharedPreferences("data", 0);
    }

    public void userUid_set(String str){
        data.edit().putString(USEUID, str).apply();
    }
    public String userUid_get(){
        return data.getString(USEUID,"");
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

    public void setLoginState(Boolean state){
        data.edit().putBoolean(LOGINSTATE, state).apply();
    }

    public  Boolean getLoginState(){
        return data.getBoolean(LOGINSTATE,false);
    }

    public void ClearUserData(){
        data.edit().putBoolean(LOGINSTATE, false)
                .putString(USERNAME, "")
                .putString(USERPHOTOURL, "")
                .putString(USEREMAIL, "").apply();
    }
}
