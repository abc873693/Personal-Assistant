package rainvisitor.personal_assistant.Drawer;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;

import rainvisitor.personal_assistant.DetailScheduleActivity;
import rainvisitor.personal_assistant.Models.ScheduleModel;
import rainvisitor.personal_assistant.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SchedulesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SchedulesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SchedulesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String DATABASE_TAG = "Firebase Database";
    private static final String USER_UID = "4wCRmeLUdtUBREByNn1GHFdFsnl2";
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<ScheduleModel> lists = new ArrayList<>();
    private ArrayList<String> schedules = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    public SchedulesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public SchedulesFragment newInstance(String param1, String param2) {
        SchedulesFragment fragment = new SchedulesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedules, container, false);
        context = getActivity();
        recyclerView = (RecyclerView) view.findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        getUserActivity();
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
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    context.toString() + " must implement OnFragmentFInteractionListener");
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

    private void getUserActivity() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        Log.e(DATABASE_TAG, "getUserActivity...");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                schedules.clear();
                Log.e(DATABASE_TAG, "onDataChange");
                for (DataSnapshot ds : dataSnapshot.child(USER_UID).child("activtys").getChildren()) {
                    //ScheduleModel model = new ScheduleModel();
                    schedules.add(ds.child("uid").getValue().toString());
                    Log.e(DATABASE_TAG, ds.child("uid").getValue().toString());
                    //adapter.add(ds.child("name").getValue().toString());
                }
                if (schedules.size() != 0) {
                    getScheduleData();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(DATABASE_TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void getScheduleData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("activity");
        Log.e(DATABASE_TAG, "getUserActivity...");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lists.clear();
                Log.e(DATABASE_TAG, "onDataChange");
                for (String uid : schedules) {
                    ScheduleModel model = new ScheduleModel();
                    DataSnapshot ds = dataSnapshot.child(uid);
                    model.title = ds.child("title").getValue().toString();
                    model.date_begin = ds.child("time").child("begin").getValue().toString();
                    model.date_end = ds.child("time").child("end").getValue().toString();
                    model.content = ds.child("content").getValue().toString();
                    model.uid = uid;
                    Log.e(DATABASE_TAG, "Value" + ds.getValue().toString());
                    Log.e(DATABASE_TAG, uid + " title=" + model.title);
                    lists.add(model);
                }
                /*LinearLayoutManager llm = new LinearLayoutManager(context);
                llm.setAutoMeasureEnabled(true);
                llm.setOrientation(LinearLayoutManager.VERTICAL);*/
                ContactAdapter customAdapter = new ContactAdapter(lists);
                recyclerView.setAdapter(customAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(DATABASE_TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

        private List<ScheduleModel> contactList;

        private ContactAdapter(List<ScheduleModel> contactList) {
            this.contactList = contactList;
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        @Override
        public void onBindViewHolder(ContactViewHolder holder, final int position) {
            holder.textView_title.setText(lists.get(position).title);
            holder.textView_time.setText(lists.get(position).date_begin + "到" + lists.get(position).date_end);
            holder.textView_content.setText(lists.get(position).content);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = recyclerView.getChildLayoutPosition(view);
                    Log.d("cardView onClick","itemPosition="+itemPosition);
                    Intent intent = new Intent(context, DetailScheduleActivity.class);
                    intent.putExtra("activity_uid", lists.get(itemPosition).uid);
                    startActivity(intent);
                }
            });
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.list_schedule, viewGroup, false);
            return new ContactViewHolder(itemView);
        }

        class ContactViewHolder extends RecyclerView.ViewHolder {
            TextView textView_title;
            TextView textView_time;
            TextView textView_content;
            CardView cardView;

            private ContactViewHolder(View convertView) {
                super(convertView);
                textView_title = (TextView) convertView.findViewById(R.id.textView_title);
                textView_time = (TextView) convertView.findViewById(R.id.textView_time);
                textView_content = (TextView) convertView.findViewById(R.id.textView_content);
                cardView = (CardView) convertView.findViewById(R.id.card_view);
            }
        }
    }
}
