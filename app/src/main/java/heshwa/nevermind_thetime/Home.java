package heshwa.nevermind_thetime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import heshwa.nevermind_thetime.Model.Chat;
import heshwa.nevermind_thetime.Model.User;

public class Home extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView username;
    private CircleImageView profile_image;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference,reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        viewPager = findViewById(R.id.home_pager);
        tabLayout = findViewById(R.id.tablayout_home);
        username = findViewById(R.id.Username);
        profile_image = findViewById(R.id.profile_image);
        profile_image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v)
            {
                Intent intent=new Intent(Home.this,profile_display.class);
                startActivity(intent);
                return true;
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Log.i("Home","datachange");
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getusername());
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);

                }
                else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //For Showing no of unrEAD MESAAGES
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                HomeAdapter homeAdapter = new HomeAdapter(getSupportFragmentManager());
                int unread=0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Chat chat=snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen())
                    {
                        unread++;
                    }
                }
                if (unread == 0){
                    homeAdapter.addFragment(new ChaterHome(), "Chats");
                } else {
                    homeAdapter.addFragment(new ChaterHome(), "("+unread+") Chats");
                }

                viewPager.setAdapter(homeAdapter);
                tabLayout.setupWithViewPager(viewPager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                Intent i= new Intent(Home.this,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                Toast.makeText(getApplicationContext(), "Successfully logged out", Toast.LENGTH_LONG).show();
                return true;
            case R.id.userdisplay:
                Log.i("Home:","userdisplay option selected");
                Intent intent= new Intent(Home.this,Users.class);
                startActivity(intent);
                //finish();
                return true;
            case R.id.settings:
//                Intent intent= new Intent(Home.this,User.class);
//                startActivity(intent);
                return true;
        }
        return false;
    }
    private void status(String status)
    {
        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        databaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
