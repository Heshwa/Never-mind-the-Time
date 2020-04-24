package heshwa.nevermind_thetime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import heshwa.nevermind_thetime.AdapterForViews.MessageAdapter;
import heshwa.nevermind_thetime.Model.Chat;
import heshwa.nevermind_thetime.Model.User;
import heshwa.nevermind_thetime.Notifications.Client;
import heshwa.nevermind_thetime.Notifications.Data;
import heshwa.nevermind_thetime.Notifications.MyResponse;
import heshwa.nevermind_thetime.Notifications.Sender;
import heshwa.nevermind_thetime.Notifications.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class message extends AppCompatActivity
{
    CircleImageView profile_image,statusdot;
    TextView username;

    FirebaseUser fuser;
    DatabaseReference reference;
    Intent intent;
    ImageButton btn_send;
    EditText text_send;
    MessageAdapter messageAdapter;
    List<Chat> mchat;
    RecyclerView recyclerView;
    ValueEventListener seenListener;
    private ProgressBar progressbar;
    String userid;
    long total_messages;
    long total_messages_in_a_conversation;


    APIService apiService;
    boolean notify=false;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
       //Log.i("Message Activity :","oncreate method started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getWindow().setBackgroundDrawableResource(R.drawable.ch) ;
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(message.this,Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });

       // Log.i("Message Activity :","Toolbarset");
        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image=findViewById(R.id.profile_image);
        progressbar = findViewById(R.id.progressbar);
        statusdot=findViewById(R.id.status);
        username=findViewById(R.id.Username);
        btn_send=findViewById(R.id.btn_send);
        text_send=findViewById(R.id.text_send);
        intent=getIntent();
        userid=intent.getStringExtra("userid");
       // Log.i("Message Activity :","Intent userid got ,"+userid);
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        progressbar.setVisibility(View.VISIBLE);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                notify=true;
                String msg=text_send.getText().toString();
                if(!msg.equals(""))
                {
                   sendMessage(fuser.getUid(),userid,msg);
                }
                else
                {
                    Toast.makeText(message.this,"Enter something to send a message",Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");

            }
        });
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        reference=FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getusername());
                if(user.getImageURL().equals("default"))
                {
                    profile_image.setImageResource(R.mipmap.ic_launcher);

                }
                else
                {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
                if(user.getStatus().equals("online"))
                {
                    statusdot.setVisibility(View.VISIBLE);
                }
                else {
                    statusdot.setVisibility(View.GONE);
                }
                readMessage(fuser.getUid(),userid,user.getImageURL());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        seenMessage(userid);

    }
    private void seenMessage(final String userid)
    {
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Chat chat=snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(fuser.getUid())&&chat.getSender().equals(userid))
                    {
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        final HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);
        reference.child("Chats").push().setValue(hashMap);
       // Log.i("Message activity:","message stored");

        final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid()).child(userid);

        final DatabaseReference chatRef2=FirebaseDatabase.getInstance().getReference("Chatlist").child(userid).child(fuser.getUid());

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                HashMap<String,Object> hashMap2=new HashMap<>();
                if(!dataSnapshot.exists())
                {

                   // Log.i("Message activity:","created chatlist "+userid);
                    hashMap2.put("priority",ServerValue.TIMESTAMP);
                    chatRef.child("id").setValue(userid);
                    chatRef.updateChildren(hashMap2);
                }
                else
                {
                    hashMap2.put("priority",ServerValue.TIMESTAMP);
                    chatRef.updateChildren(hashMap2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        chatRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                HashMap<String,Object> hashMap2=new HashMap<>();
                if(!dataSnapshot.exists())
                {

                   // Log.i("Message activity:","created 2chatlist "+fuser.getUid());
                    chatRef2.child("id").setValue(fuser.getUid());
                    hashMap2.put("priority",ServerValue.TIMESTAMP);
                  chatRef2.updateChildren(hashMap2);

                }
                else
                {

                    hashMap2.put("priority",ServerValue.TIMESTAMP);
                    chatRef2.updateChildren(hashMap2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final  String mag= message;
        reference=FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                User user=dataSnapshot.getValue(User.class);
                Log.i("MessageActivity:","Ready for notification");
                if(notify)
                {
                    Log.i("MessageActivity:","callingv for notification");
                    sendNotification(receiver,user.getusername(),mag);
                }

                notify=false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private  void sendNotification(String receiver,final String username,final String message)
    {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=tokens.orderByKey().equalTo(receiver);
        Log.i("MessageActivity:","SendNotification method:query got");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Token token=snapshot.getValue(Token.class);
                    Log.i("sendNotification","token="+token);
                    Data data=new Data(fuser.getUid(),R.mipmap.ic_launcher,username+": "+message,"New Message",userid);

                    Sender sender= new Sender(data,token.getToken());

                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response)
                        {
                            if(response.code()==200)
                            {
                                if(response.body().sucess!=1)
                                {
                                    //Toast.makeText(message.this," Notification Failed to send",Toast.LENGTH_SHORT).show();
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void readMessage(final String myid, final String userid, final String imageurl)
    {
        mchat=new ArrayList<>();
        total_messages=0;
        total_messages_in_a_conversation=0;
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();

                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Chat chat=snapshot.getValue(Chat.class);
                    assert chat!=null;
                   // Log.i("MesssageActivity:",""+chat);
                    if(chat.getReceiver().equals(myid)&&chat.getSender().equals(userid)|| chat.getReceiver().equals(userid)&&chat.getSender().equals(myid))
                    {
                        mchat.add(chat);
                        progressbar.setVisibility(View.GONE);


                        messageAdapter=new MessageAdapter(message.this,mchat,imageurl);
                        recyclerView.setAdapter(messageAdapter);
                    }



                }
                total_messages_in_a_conversation=mchat.size();
                Log.d("total_messages_in",""+total_messages_in_a_conversation);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }
    private void status(String status)
    {
        reference=FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }
}
