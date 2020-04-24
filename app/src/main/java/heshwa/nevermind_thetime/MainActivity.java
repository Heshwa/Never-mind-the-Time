package heshwa.nevermind_thetime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
{
    TabLayout tabLayout;
    ViewPager viewPager;
    Button login;
    FirebaseUser firebaseUser;
    ImageView imageView;

    @Override
    protected void onStart()
    {
        super.onStart();
        Handler handler = new Handler();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        imageView=findViewById(R.id.loading_image);
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                // do something

                if(firebaseUser!=null)
                {

                    Intent intent = new Intent(MainActivity.this, Home.class);
                    startActivity(intent);
                    finish();

                }
                else
                {
                    imageView.setVisibility(View.GONE);
                }

            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager= findViewById(R.id.Viewpager_startup);
        tabLayout=findViewById(R.id.tablayout_startup);
        startupadapter pageAdapter= new startupadapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);


        login=findViewById(R.id.register);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(v.getContext(),regestration.class);
                startActivity(i);
            }
        });
    }
    public void have_an_account(View v)
    {
        Intent intent = new Intent(MainActivity.this, login.class);
        startActivity(intent);
    }
}
