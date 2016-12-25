package rainvisitor.personal_assistant.Drawer;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rainvisitor.personal_assistant.Models.AllScheduleModel;
import rainvisitor.personal_assistant.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static String USER_UID = "4wCRmeLUdtUBREByNn1GHFdFsnl2";
    private static final String DATABASE_TAG = "Firebase Database";
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<AllScheduleModel> lists = new ArrayList<>();
    private TextView textView;
    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    public NotesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public NotesFragment newInstance(String param1) {
        NotesFragment fragment = new NotesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid;
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            uid = user.getUid().toString();
            USER_UID = user.getUid().toString();
            Log.e("getCurrentUser", "uid = " + uid + "  name = " + name + "  email = " + email + "  photoUrl = " + photoUrl);
        } else uid = "0";
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        context = getActivity();
        recyclerView = (RecyclerView) view.findViewById(R.id.list_AllView);
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
        DatabaseReference myRef = database.getReference();
        Log.e(DATABASE_TAG, "getUserActivity...");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lists.clear();
                for (DataSnapshot ds : dataSnapshot.child("activity").getChildren()) {
                    AllScheduleModel model = new AllScheduleModel();
                    String num = ds.getKey().toString();
                    model.num = num;
                    model.join = false;
                    model.title = ds.child("title").getValue().toString();
                    model.content = ds.child("content").getValue().toString();
                    model.date_begin = Long.parseLong(ds.child("time").child("begin").getValue().toString());
                    model.date_end = Long.parseLong(ds.child("time").child("end").getValue().toString());
                    model.location = ds.child("location").child("name").getValue().toString();
                    String ID = ds.child("members").child("0").child("uid").getValue().toString();
                    for (DataSnapshot dss : dataSnapshot.child("users").getChildren()) {
                        if (dss.getKey().equals(ID)) {
                            model.creator = dss.child("name").getValue().toString();
                            Log.e("Bingo", model.creator);
                            break;
                        }
                    }
                    long count = 0;
                    count = dataSnapshot.child("users").child(USER_UID).child("activtys").getChildrenCount();
                    for (long i = 1; i <= count; i++) {
                        String get = dataSnapshot.child("users").child(USER_UID).child("activtys").child(i + "").child("uid").getValue().toString();
                        if (get.equals(num)) {
                            model.join = true;
                            Log.e("Get", get);
                            break;
                        }
                    }
                    lists.add(model);
                    Log.e("List", model.join + ":");
                    //adapter.add(ds.child("name").getValue().toString());
                }
               /* if (schedules.size() != 0) {
                    getScheduleData();
                }*/
                ContactAdapter customAdapter = new NotesFragment.ContactAdapter(lists);
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

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

        private List<AllScheduleModel> contactList;

        private ContactAdapter(List<AllScheduleModel> contactList) {
            this.contactList = contactList;
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        @Override
        public void onBindViewHolder(ContactAdapter.ContactViewHolder holder, final int position) {
            String begin = getDate(lists.get(position).date_begin, "yyyy年 MM月 dd日 hh點mm分");
            String end = getDate(lists.get(position).date_end, "yyyy年 MM月 dd日 hh點mm分");
            holder.textView_creator.setText(lists.get(position).creator + "");
            holder.textView_title.setText(lists.get(position).title);
            holder.textView_time.setText(begin + " ~ " + end + " At " + lists.get(position).location);
            holder.textView_content.setText(lists.get(position).content);
            if (!lists.get(position).join) {
                holder.button_join.setText("我要參加");
                holder.button_join.setEnabled(true);
            } else {
                holder.button_join.setText("已參加");
                holder.button_join.setEnabled(false);
            }
            holder.button_join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DatabaseReference userDatabase = dataSnapshot.child("users").child(USER_UID).getRef();
                            long count_activity = dataSnapshot.child("users").child(USER_UID).child("activtys").getChildrenCount();
                            Log.e("count_activity", "count_activity" + count_activity);
                            userDatabase.child("activtys").child((count_activity + 1) + "").child("uid").setValue(lists.get(position).num);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }

        @Override
        public NotesFragment.ContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.list_allschedule, viewGroup, false);
            return new NotesFragment.ContactAdapter.ContactViewHolder(itemView);
        }

        class ContactViewHolder extends RecyclerView.ViewHolder {
            TextView textView_title;
            TextView textView_time;
            TextView textView_content;
            TextView textView_creator;
            Button button_join;
            CardView cardView;

            private ContactViewHolder(View convertView) {
                super(convertView);
                textView_title = (TextView) convertView.findViewById(R.id.textView_title);
                textView_time = (TextView) convertView.findViewById(R.id.textView_time);
                textView_content = (TextView) convertView.findViewById(R.id.textView_content);
                textView_creator = (TextView) convertView.findViewById(R.id.textview_creator);
                button_join = (Button) convertView.findViewById(R.id.button_join);
                cardView = (CardView) convertView.findViewById(R.id.card_view);
            }
        }
    }
}
