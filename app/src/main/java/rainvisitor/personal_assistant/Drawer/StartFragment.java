package rainvisitor.personal_assistant.Drawer;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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

import rainvisitor.personal_assistant.MainActivity;
import rainvisitor.personal_assistant.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DATABASE_TAG = "Firebase Database";
    private TextView textView_version;
    MainActivity mainActivity;

    // TODO: Rename and change types of parameters

    private OnFragmentInteractionListener mListener;

    public StartFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public StartFragment newInstance() {
        StartFragment fragment = new StartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        mainActivity = (MainActivity) getActivity();
        view.findViewById(R.id.layout_notes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.changeContent(1);
            }
        });
        view.findViewById(R.id.layout_schedules).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.changeContent(2);
            }
        });
        view.findViewById(R.id.layout_money).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.changeContent(3);
            }
        });
        view.findViewById(R.id.layout_restaurants).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.changeContent(4);
            }
        });
        textView_version = (TextView) view.findViewById(R.id.text_version);
        getFirebaseDatabse();
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getFirebaseDatabse(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("version");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = "版本:" + dataSnapshot.getValue(String.class);
                textView_version.setText(value);
                Log.d(DATABASE_TAG, "Value is: " + value);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(DATABASE_TAG, "Failed to read value.", error.toException());
            }
        });
    }


}
