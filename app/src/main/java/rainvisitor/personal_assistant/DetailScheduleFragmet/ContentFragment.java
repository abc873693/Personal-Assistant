package rainvisitor.personal_assistant.DetailScheduleFragmet;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rainvisitor.personal_assistant.DetailScheduleActivity;
import rainvisitor.personal_assistant.R;

import static rainvisitor.personal_assistant.Drawer.SchedulesFragment.getDate;

public class ContentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DetailScheduleActivity detailScheduleActivity;
    private String content = "", creator = "", title = "", place = "";
    Date date_begin, date_end;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //Firebase
    private StorageReference mStorageRef;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FrameLayout fl;
    private RecyclerView recyclerView;
    private Context context;
    private ArrayList<Bitmap> images = new ArrayList<>();
    private int imagecount;
    private String imagetime;


    private TextView textView_creator, textView_time, textView_content;

    public ContentFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public ContentFragment newInstance(String param1, String param2) {
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ContentFragment newInstance() {
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, "");
        args.putString(ARG_PARAM2, "");
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

        fl = (FrameLayout) inflater.inflate(R.layout.fragment_detailschedule_content, container, false);
        detailScheduleActivity = (DetailScheduleActivity) getActivity();
        textView_content = (TextView) fl.findViewById(R.id.textview_content);
        textView_creator = (TextView) fl.findViewById(R.id.textview_creator);
        textView_time = (TextView) fl.findViewById(R.id.textview_time);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        context = fl.getContext();
        recyclerView = (RecyclerView) fl.findViewById(R.id.recycleview_picture);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot ds = dataSnapshot.child("activity").child(detailScheduleActivity.current_activity_uid).child("activity_child").child(detailScheduleActivity.current_schedule_uid);
                content = ds.child("content").getValue().toString();
                creator = ds.child("creator").getValue().toString();
                title = ds.child("title").getValue().toString();
                imagecount = Integer.parseInt(ds.child("imagecount").getValue().toString());
                imagetime =  ds.child("time").child("begin").getValue().toString();

                Log.e("imagecount", ds.child("imagecount").getValue() + "");
                Log.e("mParam1", ds.child("content").getValue() + "");
                detailScheduleActivity.collapsingToolbar.setTitle(title);
                textView_content.setText(content);
                String begin = getDate(Long.parseLong(ds.child("time").child("begin").getValue().toString()), "yyyy年 MM月 dd日 hh點mm分");
                String end = getDate(Long.parseLong(ds.child("time").child("end").getValue().toString()), "yyyy年 MM月 dd日 hh點mm分");
                textView_time.setText(begin + "\n到\n" + end);
                textView_creator.setText(creator);

                DownloadImage();
                Log.d("Content", "title = " + title + " creator = "+ creator + " content = " + content);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.e("abc", mParam1);
        Log.e("cde", mParam2);
        // Inflate the layout for this fragment

        return fl;
    }

    private void DownloadImage() {
        //Todo: Loop fetch Image Uri
        for (int i = 0; i < imagecount; i++) {
            mStorageRef.child(user.getUid() + "/event" + imagetime + "Picture" + i).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                                Log.e("URI", uri.toString());

                                new AsyncTask<String, Void, Bitmap>()
                                {
                                    @Override
                                    protected Bitmap doInBackground(String... strings) {
                                        String url = strings[0];
                                        return getBitmapFromURL(url);
                                    }
                                    @Override
                                    protected void onPostExecute(Bitmap result){
                                        images.add(result);
                                        ContentFragment.MyAdapter MyAdapter = new ContentFragment.MyAdapter(images);
                                        recyclerView.setAdapter(MyAdapter);
                                        super.onPostExecute(result);
                                    }
                                }.execute(uri.toString());


                            Toast.makeText(fl.getContext(), "Image Downloading...", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(fl.getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
        //Todo:Loop End
    }

    private Bitmap getBitmapFromURL(String imageurl) {
        try {
            URL url = new URL(imageurl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    class MyAdapter extends RecyclerView.Adapter<ContentFragment.MyAdapter.ViewHolder> {
        private List<Bitmap> BitMap;

        public MyAdapter(List<Bitmap> Bitmap) {
            this.BitMap = Bitmap;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_image, null);

            ContentFragment.MyAdapter.ViewHolder viewHolder = new ContentFragment.MyAdapter.ViewHolder(itemLayoutView);
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
