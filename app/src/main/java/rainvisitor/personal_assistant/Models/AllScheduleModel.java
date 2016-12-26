package rainvisitor.personal_assistant.Models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Ray on 2016/12/23.
 */

public class AllScheduleModel {
    public String title;
    public long date_begin;
    public long date_end;
    public String content;
    public String location;
    public String creator;
    public Boolean join = false;
    public String num;
    public LatLng latLng = null;
}
