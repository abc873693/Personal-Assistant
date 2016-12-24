package rainvisitor.personal_assistant.DetailScheduleFragmet;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import rainvisitor.personal_assistant.DetailScheduleActivity;
import rainvisitor.personal_assistant.R;

public class AddFragment extends Fragment implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //Todo: New Items
    private EditText editText_title;
    private TextView textView_location, textView_dateStart, textView_dateEnd, textView_timeStart, textView_timeEnd;
    private ImageView imageView_addPicture;

    //Todo: RecyclerView
    private ArrayList<Image> images = new ArrayList<>();
    private RecyclerView recyclerView;

    private long count = 0;
    private LinearLayout linearLayout;
    private Calendar now;
    private int current_date = 0, current_time = 0;

    private String sdatestring,edatestring,stimestring,etimestring;
    private long stimestamp, etimestamp;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DetailScheduleActivity detailScheduleActivity;

    private OnFragmentInteractionListener mListener;

    public AddFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public AddFragment newInstance(String param1, String param2) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AddFragment newInstance() {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout fl = (FrameLayout) inflater.inflate(R.layout.fragment_detailschedule_add, container, false);
        editText_title = (EditText) fl.findViewById(R.id.edittext_title);
        textView_location = (TextView) fl.findViewById(R.id.textview_location);
        textView_dateStart = (TextView) fl.findViewById(R.id.textview_startdate);
        textView_dateEnd = (TextView) fl.findViewById(R.id.textview_enddate);
        textView_timeStart = (TextView) fl.findViewById(R.id.textview_starttime);
        textView_timeEnd = (TextView) fl.findViewById(R.id.textview_endtime);
        TextView textview_newpicture = (TextView) fl.findViewById(R.id.textView_newpicture);
        detailScheduleActivity = (DetailScheduleActivity) getActivity();

        now = Calendar.getInstance();
        String date = now.get(Calendar.YEAR) + "年" + now.get(Calendar.MONTH) + "月" + now.get(Calendar.DAY_OF_MONTH) + "日";
        String timeS = now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
        String timeE = now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);

        sdatestring = edatestring = now.get(Calendar.YEAR) + "/" + now.get(Calendar.MONTH) + "/" + now.get(Calendar.DAY_OF_MONTH);
        stimestring = etimestring = now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);

        textView_dateStart.setText(date);
        textView_dateEnd.setText(date);
        textView_timeStart.setText(timeS);
        textView_timeEnd.setText(timeE);


        textView_dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                current_date = 1;
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        textView_dateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                current_date = 2;
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        Button btn_add = (Button) fl.findViewById(R.id.btn_add);
        /*btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSchedule();
            }
        });*/
        textView_timeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        AddFragment.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false);
                current_time = 1;
                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });
        textView_timeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        AddFragment.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false);
                current_time = 2;
                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });



        /*//Todo: RecycleView
        recyclerView = (RecyclerView) fl.findViewById(R.id.recycleview_picture);
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // 设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        // 创建数据集
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // 创建Adapter，并指定数据集
        MyAdapter adapter = new MyAdapter(dataset);
        // 设置Adapter
        recyclerView.setAdapter(adapter);*/

        //Todo: 選取相片
        imageView_addPicture = (ImageView) fl.findViewById(R.id.imageView_addpicture);
        imageView_addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 33);
            }
        });
        textview_newpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 33);
            }
        });

        return fl;
    }




    //TODO: 回傳相片
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "年" + (++monthOfYear) + "月" + (dayOfMonth) + "日";
        Log.e("onDateSet",view.toString());
        switch (current_date){
            case 1:
                textView_dateStart.setText(date);
                sdatestring = year + "/" + (++monthOfYear) + "/" + (dayOfMonth);
                break;
            case 2:
                textView_dateEnd.setText(date);
                edatestring = year + "/" + (++monthOfYear) + "/" + (dayOfMonth);
                break;
            default:
                break;
        }
    }
    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String time = hourString+":"+minuteString;
        switch (current_time){
            case 1:
                textView_timeStart.setText(time);
                stimestring = time;
                break;
            case 2:
                textView_timeEnd.setText(time);
                etimestring = time;
                break;
            default:
                break;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    private void addSchedule() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Calendar startdtoc = Calendar.getInstance();
        try{
            startdtoc.setTime(df.parse(sdatestring + " " + stimestring));
            stimestamp = startdtoc.getTimeInMillis();
            startdtoc.setTime(df.parse(edatestring + " " + etimestring));
            etimestamp = startdtoc.getTimeInMillis();
        }catch(ParseException e){
            e.printStackTrace();
        }


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if (snap.getKey() == "activity") count = snap.getChildrenCount();
                    Log.e("count", count + "");
                }
                if (textView_location.getText() + "" == "" || editText_title.getText() + "" == "" ||
                        textView_dateStart.getText() + "" == "" || textView_timeStart.getText() + "" == "" || textView_dateEnd.getText() + "" == "" || textView_timeEnd.getText() + "" == "") {

                    final Snackbar snackbar = Snackbar
                            .make(linearLayout, "請勿留空", Snackbar.LENGTH_INDEFINITE)
                            .setAction("確定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                    snackbar.show();
                }
                else if(stimestamp > etimestamp) {
                    Snackbar snackbar = Snackbar
                            .make(linearLayout, "開始時間不得大於結束時間！", Snackbar.LENGTH_INDEFINITE)
                            .setAction("確定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                    snackbar.show();

                }else{
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid;
                    if (user != null) {
                        String name = user.getDisplayName();
                        String email = user.getEmail();
                        Uri photoUrl = user.getPhotoUrl();

                        // The user's ID, unique to the Firebase project. Do NOT use this value to
                        // authenticate with your backend server, if you have one. Use
                        // FirebaseUser.getToken() instead.
                        uid = user.getUid();
                        Log.e("getCurrentUser", "uid = " + uid + "  name = " + name + "  email = " + email + "  photoUrl = " + photoUrl);
                    } else uid = "0";
                    DatabaseReference mDatabase = dataSnapshot.child("activity").getRef();
                    DatabaseReference userDatabase = dataSnapshot.child("users").child(uid).getRef();
                    count = dataSnapshot.child("activity").getChildrenCount();
                    DatabaseReference dr = mDatabase.child((count + 1) + "").getRef();
                    dr.child("location").setValue(textView_location.getText() + "");
                    dr.child("title").setValue(editText_title.getText() + "");
                    dr.child("time").child("begin").setValue(stimestamp);
                    dr.child("time").child("end").setValue(etimestamp);
                    dr.child("members").child("0").child("authority").setValue("creator");
                    dr.child("members").child("0").child("uid").setValue(uid);
                    long count_activity = dataSnapshot.child("users").child(uid).child("activtys").getChildrenCount();
                    Log.e("count_activity", "count_activity" + count_activity);
                    userDatabase.child("activtys").child((count_activity + 1) + "").child("uid").setValue((count + 1) + "");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");
        if(dpd != null) dpd.setOnDateSetListener(this);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
