package rainvisitor.personal_assistant.DetailScheduleFragmet;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rainvisitor.personal_assistant.DetailScheduleActivity;
import rainvisitor.personal_assistant.R;

import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int Pick_Image_Request = 33;
    //Todo: New Items
    private FrameLayout fl;
    private EditText editText_title;
    private TextView textView_location, textView_dateStart, textView_dateEnd, textView_timeStart, textView_timeEnd;
    private ImageView imageView_addPicture;

    //Todo: RecyclerView
    private RecyclerView recyclerView;
    private ArrayList<Bitmap> images = new ArrayList<>();
    private ArrayList<Uri> uriArray = new ArrayList<>();
    private long count = 0;
    private LinearLayout linearLayout;
    private Calendar now, start, end, temp;
    private int current_date = 0, current_time = 0;

    private String sdatestring, edatestring, stimestring, etimestring;
    private long stimestamp, etimestamp;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context context;

    private DetailScheduleActivity detailScheduleActivity;

    private OnFragmentInteractionListener mListener;


    //FireBase Storage
    private StorageReference mStorageRef;


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
        mStorageRef = FirebaseStorage.getInstance().getReference();


        fl = (FrameLayout) inflater.inflate(R.layout.fragment_detailschedule_add, container, false);
        editText_title = (EditText) fl.findViewById(R.id.edittext_title);
        textView_location = (TextView) fl.findViewById(R.id.textview_location);
        textView_dateStart = (TextView) fl.findViewById(R.id.textview_startdate);
        textView_dateEnd = (TextView) fl.findViewById(R.id.textview_enddate);
        textView_timeStart = (TextView) fl.findViewById(R.id.textview_starttime);
        textView_timeEnd = (TextView) fl.findViewById(R.id.textview_endtime);
        linearLayout = (LinearLayout) fl.findViewById(R.id.linealayout_detailadd);
        TextView textview_newpicture = (TextView) fl.findViewById(R.id.textView_newpicture);
        detailScheduleActivity = (DetailScheduleActivity) getActivity();
        detailScheduleActivity.collapsingToolbar.setTitle("新增行程");
        now = Calendar.getInstance();
        String date = now.get(Calendar.YEAR) + "年" + (now.get(Calendar.MONTH) + 1) + "月" + now.get(Calendar.DAY_OF_MONTH) + "日";
        String timeS = now.get(Calendar.HOUR_OF_DAY) < 10 ? "0" : "";
        timeS += now.get(Calendar.HOUR_OF_DAY) + ":";
        timeS += now.get(Calendar.MINUTE) < 10 ? "0" : "";
        timeS += now.get(Calendar.MINUTE);
        String timeE = timeS;

        textView_dateStart.setText(date);
        textView_dateEnd.setText(date);
        textView_timeStart.setText(timeS);
        textView_timeEnd.setText(timeE);

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        temp = Calendar.getInstance();
        start.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
        end.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
        textView_dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddFragment.this,
                        start.get(Calendar.YEAR),
                        start.get(Calendar.MONTH),
                        start.get(Calendar.DATE)
                );
                current_date = 1;
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        textView_dateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddFragment.this,
                        end.get(Calendar.YEAR),
                        end.get(Calendar.MONTH),
                        end.get(Calendar.DATE)
                );
                current_date = 2;
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        textView_timeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        AddFragment.this,
                        start.get(Calendar.HOUR_OF_DAY),
                        start.get(Calendar.MINUTE),
                        false);
                current_time = 1;
                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });
        textView_timeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        AddFragment.this,
                        end.get(Calendar.HOUR_OF_DAY),
                        end.get(Calendar.MINUTE),
                        false);
                current_time = 2;
                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });



        //Todo: RecycleView

        context = fl.getContext();
        recyclerView = (RecyclerView) fl.findViewById(R.id.recycleview_picture);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        Button btn_add = (Button) fl.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSchedule();
                UploadImage();
            }
        });
        //Todo: 選取相片
        imageView_addPicture = (ImageView) fl.findViewById(R.id.imageView_addpicture);
        imageView_addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, Pick_Image_Request);
            }
        });
        textview_newpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(Intent.createChooser(intent, "Select an Image."), Pick_Image_Request);
            }
        });

        return fl;
    }


    //TODO: 回傳相片
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Pick_Image_Request && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            uriArray.add(uri);
            Log.e("uri", uri.toString());
            ContentResolver cr = getActivity().getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                images.add(bitmap);
                AddFragment.MyAdapter MyAdapter = new AddFragment.MyAdapter(images);
                recyclerView.setAdapter(MyAdapter);

            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }
    }

    private void UploadImage(){
    for(int i = 0;i < uriArray.size();i++) {
        if (uriArray.get(i) != null) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            final ProgressDialog progressDialog = new ProgressDialog(fl.getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference riversRef = mStorageRef.child(user.getUid() + "/event" + start.getTimeInMillis() + "Picture" + i);

            riversRef.putFile(uriArray.get(i))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(fl.getContext(), "Image Uploaded.", Toast.LENGTH_LONG);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(fl.getContext(), exception.getMessage(), Toast.LENGTH_LONG);

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred());
                            progressDialog.setMessage((int) (progress + 0.5) + "% Uploaded...");
                        }
                    })
            ;
        } else {
            Toast.makeText(fl.getContext(), "Error Happened！", Toast.LENGTH_LONG);
        }
    }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        temp = Calendar.getInstance();
        String date = year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日";
        switch (current_date) {
            case 1:
                temp.set(year, monthOfYear, dayOfMonth, start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE));
                Log.e("Date parse", year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日" + start.get(Calendar.HOUR_OF_DAY) + ":" + start.get(Calendar.MINUTE));
                Log.e("End", end.getTimeInMillis() + "");
                Log.e("Temp", temp.getTimeInMillis() + "");
                if (temp.getTimeInMillis() - end.getTimeInMillis() > 60000) {
                    current_date = 1;
                    checkdate();
                    return;
                }
                start = temp;
                textView_dateStart.setText(date);
                break;
            case 2:
                temp.set(year, monthOfYear, dayOfMonth, end.get(Calendar.HOUR_OF_DAY), end.get(Calendar.MINUTE));
                Log.e("Date parse", year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日" + start.get(Calendar.HOUR_OF_DAY) + ":" + start.get(Calendar.MINUTE));
                Log.e("Start", start.getTimeInMillis() + "");
                Log.e("Temp", temp.getTimeInMillis() + "");
                if (start.getTimeInMillis() - temp.getTimeInMillis() > 60000) {
                    current_date = 2;
                    checkdate();
                    return;
                }
                end = temp;
                textView_dateEnd.setText(date);
                break;
            default:
                break;
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        temp = Calendar.getInstance();
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String time = hourString + ":" + minuteString;
        switch (current_time) {
            case 1:
                temp.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DATE), hourOfDay, minute);
                if (temp.getTimeInMillis() - end.getTimeInMillis() > 60000) {
                    current_time = 1;
                    checktime();
                    return;
                }
                start = temp;
                textView_timeStart.setText(time);
                break;
            case 2:
                temp.set(end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DATE), hourOfDay, minute);
                if (start.getTimeInMillis() - temp.getTimeInMillis() > 60000) {
                    current_time = 2;
                    checktime();
                    return;
                }
                end = temp;
                textView_timeEnd.setText(time);
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

    private void checkdate() {
        final Snackbar snackbar = Snackbar
                .make(linearLayout, "結束時間必須大於開始時間", Snackbar.LENGTH_INDEFINITE)
                .setAction("確定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (current_date == 1) {
                            DatePickerDialog dpd = DatePickerDialog.newInstance(
                                    AddFragment.this,
                                    start.get(Calendar.YEAR),
                                    start.get(Calendar.MONTH),
                                    start.get(Calendar.DATE)
                            );
                            dpd.show(getFragmentManager(), "Datepickerdialog");
                        } else {
                            DatePickerDialog dpd = DatePickerDialog.newInstance(
                                    AddFragment.this,
                                    end.get(Calendar.YEAR),
                                    end.get(Calendar.MONTH),
                                    end.get(Calendar.DATE)
                            );
                            dpd.show(getFragmentManager(), "Datepickerdialog");
                        }
                    }
                });
        snackbar.getView().setBackgroundColor(Color.rgb(239, 83, 80));
        snackbar.show();
    }

    private void checktime() {
        final Snackbar snackbar = Snackbar
                .make(linearLayout, "結束時間必須大於開始時間", Snackbar.LENGTH_INDEFINITE)
                .setAction("確定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (current_time == 1) {
                            TimePickerDialog tpd = TimePickerDialog.newInstance(
                                    AddFragment.this,
                                    start.get(Calendar.HOUR_OF_DAY),
                                    start.get(Calendar.MINUTE),
                                    false);
                            tpd.show(getFragmentManager(), "Timepickerdialog");
                        } else {
                            TimePickerDialog tpd = TimePickerDialog.newInstance(
                                    AddFragment.this,
                                    end.get(Calendar.HOUR_OF_DAY),
                                    end.get(Calendar.MINUTE),
                                    false);
                            tpd.show(getFragmentManager(), "Timepickerdialog");
                        }
                    }
                });
        snackbar.getView().setBackgroundColor(Color.rgb(239, 83, 80));
        snackbar.show();
    }

    //Todo: 有問題
    private void addSchedule() {
/*
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
*/

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (textView_location.getText().toString().equals("") || editText_title.getText().toString().equals("") ||
                        textView_dateStart.getText().toString().equals("") || textView_timeStart.getText().toString().equals("") ||
                        textView_dateEnd.getText().toString().equals("") || textView_timeEnd.getText().toString().equals("")) {

                    final Snackbar snackbar = Snackbar
                            .make(linearLayout, "請勿留空", Snackbar.LENGTH_INDEFINITE)
                            .setAction("確定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                    snackbar.show();
                } else {
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

                    //Todo: Insert Data
                    String UID = detailScheduleActivity.current_activity_uid;
                    DatabaseReference mDatabase = dataSnapshot.child("activity").child(UID).child("activity_child").getRef();
                    count = dataSnapshot.child("activity").child(UID).child("activity_child").getChildrenCount();
                    Log.e("count",count+"123");
                    DatabaseReference dr = mDatabase.child((count) + "").getRef();
                    dr.child("creator").setValue(user.getDisplayName());
                    dr.child("title").setValue(editText_title.getText() + "");
                    dr.child("content").setValue("內容");
                    dr.child("time").child("begin").setValue(start.getTimeInMillis());
                    dr.child("time").child("end").setValue(end.getTimeInMillis());
                    dr.child("imagecount").setValue(images.size());
                    /*long count_activity = dataSnapshot.child("users").child(uid).child("activtys").getChildrenCount();
                    userDatabase.child("activtys").child((count_activity + 1) + "").child("uid").setValue((count + 1) + "");*/
                    detailScheduleActivity.changeContent(DetailScheduleActivity.FRAGMENT.main);
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
        if (dpd != null) dpd.setOnDateSetListener(this);
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


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Bitmap> BitMap;

        public MyAdapter(List<Bitmap> Bitmap) {
            this.BitMap = Bitmap;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_image, null);

            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {

            viewHolder.imgView.setImageBitmap(BitMap.get(position));
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView imgView;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                imgView = (ImageView) itemLayoutView.findViewById(R.id.list_image);
            }
        }


        @Override
        public int getItemCount() {
            return BitMap.size();
        }
    }
}
