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

    public SP_Service(Context context){
        data = context.getSharedPreferences("data", 0);
    }

    public void username_set(String str){
        data.edit().putString(USERNAME, str).apply();
    }

    public String username_set(){
        return data.getString(USERNAME,"");
    }
}
