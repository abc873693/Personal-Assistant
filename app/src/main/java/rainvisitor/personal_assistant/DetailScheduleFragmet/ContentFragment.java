package rainvisitor.personal_assistant.DetailScheduleFragmet;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

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
        View view = inflater.inflate(R.layout.fragment_detailschedule_content, container, false);
        detailScheduleActivity = (DetailScheduleActivity) getActivity();
        textView_content = (TextView) view.findViewById(R.id.textview_content);
        textView_creator = (TextView) view.findViewById(R.id.textview_creator);
        textView_time = (TextView) view.findViewById(R.id.textview_time);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot ds = dataSnapshot.child("activity").child(detailScheduleActivity.current_activity_uid).child("activity_child").child(detailScheduleActivity.current_activity_uid);
                content = ds.child("content").getValue().toString();
                //creator = ds.child("creator").getValue().toString();
                title = ds.child("title").getValue().toString();
                Log.e("mParam1", ds.child("content").getValue() + "");
                detailScheduleActivity.collapsingToolbar.setTitle(title);
                textView_content.setText(content);
                String begin = getDate(Long.parseLong(ds.child("time").child("begin").getValue().toString()), "yyyy年 MM月 dd日 hh點mm分");
                String end = getDate(Long.parseLong(ds.child("time").child("end").getValue().toString()), "yyyy年 MM月 dd日 hh點mm分");
                textView_time.setText(begin + "\n到\n" + end);
                textView_creator.setText(creator);
                Log.d("Content", "title = " + title + " creator = "+ creator + " content = " + content);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.e("abc", mParam1);
        Log.e("cde", mParam2);
        // Inflate the layout for this fragment
        return view;
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
}
