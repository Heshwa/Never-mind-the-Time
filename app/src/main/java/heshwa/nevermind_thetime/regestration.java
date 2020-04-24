package heshwa.nevermind_thetime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import javax.sql.StatementEvent;

public class regestration extends AppCompatActivity
{
    private Button register;
    private EditText emailTextView,passwordTextView,userTextView;
    private FirebaseAuth mAuth;
    private ProgressBar progressbar;
    private DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regestration);
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.passwd);
        register=findViewById(R.id.signup);
        progressbar = findViewById(R.id.progressbar);
        userTextView=findViewById(R.id.User);
        mAuth = FirebaseAuth.getInstance();
        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
                {
                    progressbar.setVisibility(View.VISIBLE);
                    final String email, password,username;
                    email = emailTextView.getText().toString();
                    password = passwordTextView.getText().toString();
                    username=userTextView.getText().toString();
                    // Validations for input email and password
                    if (TextUtils.isEmpty(username))
                    {
                        Toast.makeText(getApplicationContext(),"Please enter Username!!",Toast.LENGTH_LONG).show();
                        progressbar.setVisibility(View.GONE);
                        return;
                    }
                    if (TextUtils.isEmpty(email)||!(email.contains("@")&&(email.contains("."))))
                    {
                        Toast.makeText(getApplicationContext(),"Please enter valid email!!",Toast.LENGTH_LONG).show();
                        progressbar.setVisibility(View.GONE);
                        return;
                    }
                    if (TextUtils.isEmpty(password))
                    {
                        Toast.makeText(getApplicationContext(),"Please enter password!!",Toast.LENGTH_LONG).show();
                        progressbar.setVisibility(View.GONE);
                        return;
                    }
                    if (password.length()<8)
                    {
                        Toast.makeText(getApplicationContext(),"Password length should be greater than 8 characters",Toast.LENGTH_LONG).show();
                        progressbar.setVisibility(View.GONE);
                        return;
                    }
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                Log.i("Registration","email is linked");
                                FirebaseUser firebaseUser=mAuth.getCurrentUser();
                                String userid=firebaseUser.getUid();
                                Log.i("Registration","Current user's id"+userid);
                                Log.i("Registration","Current user name "+username);
                                databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(userid);
                                HashMap<String, String> hashMap= new HashMap<>();
                                hashMap.put("id",userid);
                                hashMap.put("username",username);
                                hashMap.put("imageURL","default");
                                hashMap.put("status","offline");
                                hashMap.put("search",username.toLowerCase());
                                Log.i("Registration","Current user details in hashmap "+hashMap);

                                databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            Log.i("Registration","Full Sucess");
                                            Toast.makeText(v.getContext(), "Registration successful!",Toast.LENGTH_LONG).show();
                                            progressbar.setVisibility(View.GONE);
                                            Intent intent = new Intent(regestration.this,Home.class);
                                            startActivity(intent);

                                        }

                                    }
                                });




                            }
                            else
                            {
                                // Registration failed
                                Toast.makeText(v.getContext(),"Registration failed!!"+ " Please try again later", Toast.LENGTH_LONG).show();
                                // hide the progress bar
                                progressbar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(v.getContext(),"There is no internet connection",Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public void link2(View v)
    {
        Intent intent = new Intent(regestration.this, login.class);
        startActivity(intent);

    }
}
