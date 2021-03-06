package rainvisitor.personal_assistant.DetailScheduleFragmet;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rainvisitor.personal_assistant.DetailScheduleActivity;
import rainvisitor.personal_assistant.Models.ActivityModel;
import rainvisitor.personal_assistant.R;

public class MainFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private String UID, Title, Location;
    private Context context;
    private static final String DATABASE_TAG = "Firebase Database";
    private static final String USER_UID = "4wCRmeLUdtUBREByNn1GHFdFsnl2";
    private RecyclerView recyclerView;
    private ArrayList<ActivityModel> lists = new ArrayList<>();
    private ArrayList<String> activitys = new ArrayList<>();
    private ContactAdapter customAdapter;
    private DetailScheduleActivity detailScheduleActivity;
    private DatabaseReference myRef;

    public MainFragment() {
        // Required empty public constructor
    }


    public MainFragment newInstance(String uid) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString("UID", uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UID = getArguments().getString("UID");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detailschedule_main, container, false);
        context = getActivity();
        detailScheduleActivity = (DetailScheduleActivity) getActivity();
        recyclerView = (RecyclerView) view.findViewById(R.id.listview);
        recyclerView.setNestedScrollingEnabled(false);
        getActivityeData();
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

    @Override
    public void onStop() {
        super.onStop();

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getActivityeData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("activity");
        Log.e(DATABASE_TAG, "getUserActivity...");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lists.clear();
                Log.d(DATABASE_TAG, "uid = " + UID);
                Title = dataSnapshot.child(UID).child("title").getValue().toString();
                Location = dataSnapshot.child(UID).child("location").child("name").getValue().toString();
                detailScheduleActivity.collapsingToolbar.setTitle(Title);
                detailScheduleActivity.textView_location.setText(Location);
                for (DataSnapshot ds : dataSnapshot.child(UID).child("activity_child").getChildren()) {
                    ActivityModel model = new ActivityModel();
                    if (ds != null) {
                        try {
                            model.title = ds.child("title").getValue().toString();
                            model.date_begin = Long.valueOf(ds.child("time").child("begin").getValue().toString());
                            model.date_end = Long.valueOf(ds.child("time").child("end").getValue().toString());
                            model.content = ds.child("content").getValue().toString();
                            model.uid = ds.getKey();
                            Log.e(DATABASE_TAG, model.uid + " title=" + model.title + " begin = " + model.date_begin + " end = " + model.date_end);
                            lists.add(model);
                            Log.d("ds", ds.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                for (int i = 0; i < lists.size() - 1; i++) {
                    for (int j = i + 1; j < lists.size(); j++) {
                        if (lists.get(i).date_begin > lists.get(j).date_begin) {
                            ActivityModel tmpA = lists.get(i);
                            ActivityModel tmpB = lists.get(j);
                            lists.set(i, tmpB);
                            lists.set(j, tmpA);
                        }
                    }
                }
                LinearLayoutManager llm = new LinearLayoutManager(context);
                llm.setAutoMeasureEnabled(true);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);
                customAdapter = new ContactAdapter(lists);
                recyclerView.setAdapter(customAdapter);
                Log.w(DATABASE_TAG, " getItemCount = " + customAdapter.getItemCount());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(DATABASE_TAG, "Failed to read value.", error.toException());
            }
        });
    }


    public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
        private Calendar Previous;
        private List<ActivityModel> contactList;

        private ContactAdapter(List<ActivityModel> contactList) {
            Previous = Calendar.getInstance();
            Previous.setTimeInMillis(0);
            this.contactList = contactList;
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.list_activity, parent, false);
            return new ContactViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ContactViewHolder holder, int position) {

            Calendar begin = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            begin.setTimeInMillis(lists.get(position).date_begin);
            end.setTimeInMillis(lists.get(position).date_end);
            long diff = begin.getTimeInMillis() - Previous.getTimeInMillis();
            long day = diff / (24 * 60 * 60 * 1000);
            String date;
            if (day > 0) {
                date = begin.get(Calendar.YEAR) + "/" + (begin.get(Calendar.MONTH) + 1) + "/" + +begin.get(Calendar.DAY_OF_MONTH);
            } else {
                date = "";
                holder.textView_date.setVisibility(View.GONE);
            }
            Previous = begin;
            String tmp1 = ((begin.get(Calendar.HOUR_OF_DAY) < 10) ? "0" : "") + begin.get(Calendar.HOUR_OF_DAY);
            String tmp2 = ((begin.get(Calendar.MINUTE) < 10) ? "0" : "") + begin.get(Calendar.MINUTE);
            holder.textView_date.setText(date);
            holder.textView_title.setText(lists.get(position).title);
            holder.textView_time.setText(tmp1 + ":" + tmp2);
            holder.textView_content.setText(lists.get(position).content);
            holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            // Drag From Left
            //holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.swipeLayout.findViewById(R.id.bottom_wrapper1));
            // Drag From Right
            holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wrapper));
            // Handling different events when swiping
            holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onClose(SwipeLayout layout) {
                    //when the SurfaceView totally cover the BottomView.
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                    //you are swiping.
                }

                @Override
                public void onStartOpen(SwipeLayout layout) {

                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    //when the BottomView totally show.
                }

                @Override
                public void onStartClose(SwipeLayout layout) {

                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                    //when user's hand released.
                }
            });
            holder.textView_see.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View parent = (View) view.getParent();
                    while (!(parent instanceof RecyclerView)) {
                        view = parent;
                        parent = (View) parent.getParent();
                    }
                    int itemPosition = recyclerView.getChildLayoutPosition(view);
                    DetailScheduleActivity detailScheduleActivity = (DetailScheduleActivity) getActivity();
                    detailScheduleActivity.current_schedule_uid = lists.get(itemPosition).uid;
                    detailScheduleActivity.changeContent(DetailScheduleActivity.FRAGMENT.content);
                }
            });
            /*holder.textView_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            holder.textView_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
        }

        class ContactViewHolder extends RecyclerView.ViewHolder {
            TextView textView_title;
            TextView textView_time;
            TextView textView_content;
            TextView textView_see;
            TextView textView_edit;
            TextView textView_delete;
            TextView textView_date;
            SwipeLayout swipeLayout;

            private ContactViewHolder(View convertView) {
                super(convertView);
                textView_title = (TextView) convertView.findViewById(R.id.textView_title);
                textView_time = (TextView) convertView.findViewById(R.id.textView_time);
                textView_content = (TextView) convertView.findViewById(R.id.textView_content);
                swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipeLayout);
                textView_see = (TextView) convertView.findViewById(R.id.tvSee);
                /*textView_edit = (TextView) convertView.findViewById(R.id.tvEdit);
                textView_delete = (TextView) convertView.findViewById(R.id.tvDelete);*/
                textView_date = (TextView) convertView.findViewById(R.id.textView_date);
                swipeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        swipeLayout.open();
                    }
                });
            }
        }


    }
}
