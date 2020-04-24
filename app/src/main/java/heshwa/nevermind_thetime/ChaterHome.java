package heshwa.nevermind_thetime;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import heshwa.nevermind_thetime.AdapterForViews.UserAdapter;
import heshwa.nevermind_thetime.Model.Chat;
import heshwa.nevermind_thetime.Model.Chatlist;
import heshwa.nevermind_thetime.Model.User;
import heshwa.nevermind_thetime.Notifications.Token;


/**
 * A simple {@link Fragment} subclass.
 */

/* Below lines implement comparator class*/
    class TScomparator implements Comparator<Chatlist>
    {
        public int compare(Chatlist c1,Chatlist c2)
        {
            long x=(long)c1.getPriority();
            long y=(long)c2.getPriority();
            Timestamp new1=new Timestamp(x);
            Timestamp new2=new Timestamp(y);
            return(new2.compareTo(new1));
        }
    }


public class ChaterHome extends Fragment
{
    private HashMap<String,Integer>  connect;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers=new ArrayList<>();
    private ProgressBar progressbar;

    FirebaseUser fuser;
    DatabaseReference reference;
    private ArrayList<Chatlist> usersList;


    public ChaterHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //Log.i("ChatterHome:","runing on create method");
        View view=inflater.inflate(R.layout.fragment_chater_home, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        progressbar = view.findViewById(R.id.progressbar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        fuser= FirebaseAuth.getInstance().getCurrentUser();
        usersList=new ArrayList<>();

        reference=FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        ValueEventListener listener =new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                progressbar.setVisibility(View.VISIBLE);
                usersList.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Chatlist chatlist=snapshot.getValue(Chatlist.class);
                    assert chatlist!=null;
                    Log.i("Chatter hOme:",chatlist.getId()+chatlist.getPriority());
                    usersList.add(chatlist);
                    Log.i("Chatter hOme:",chatlist.toString());
                }
                Collections.sort(usersList,new TScomparator());
                connect=new HashMap<>();
                for(int i=0; i<usersList.size();i++)
                {
                    connect.put(usersList.get(i).getId(),i);
                }
                Date currentTime = Calendar.getInstance().getTime();
                Log.d("times starting",""+currentTime);


                chatlist();
                //reference.removeEventListener(this);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addValueEventListener(listener);
        //reference.removeEventListener(listener);


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                updateToken(newToken);
            }
        });





        return view;
    }
    private void updateToken(String token)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1=new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }
    private void chatlist()
    {
        Log.i("ChatterHome:","processing chatlist");
        mUsers=new ArrayList<>();
        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("Users");
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                mUsers.clear();
                for(int i=0;i<connect.size();i++)
                {
                    mUsers.add(new User());
                }
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    User user=snapshot.getValue(User.class);
                    if(connect.containsKey(user.getid()))
                    {
                        int j=connect.get(user.getid());
                        mUsers.remove(j);
                        Log.i("CHatterHome:",""+user.getusername()+":"+j);
                        mUsers.add(j,user);
                    }

                }

                userAdapter=new UserAdapter(getContext(),mUsers,true);
                recyclerView.setAdapter(userAdapter);
                progressbar.setVisibility(View.GONE);

                Date currentTime2 = Calendar.getInstance().getTime();
                Log.d("times ending",""+currentTime2);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }




}
