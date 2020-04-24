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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity
{
    private EditText emailTextView, passwordTextView;
    private Button Btn;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;
    private TextView forgot_password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // taking instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        // initialising all views through id defined above
        emailTextView = findViewById(R.id.email_login);
        passwordTextView = findViewById(R.id.password_login);
        Btn = findViewById(R.id.login_page);
        progressbar = findViewById(R.id.progressBar);
        forgot_password = findViewById(R.id.forgot_password);
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this,forgot_password.class));
            }
        });
        // Set on Click Listener on Sign-in button
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v)
            {
                Log.i("Login", "onClick: called");
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
                {
                    Log.i("Login", "onClick: connectivity checked :sucess");
                    progressbar.setVisibility(View.VISIBLE);
                    // Take the value of two edit texts in Strings
                    String email, password;
                    email = emailTextView.getText().toString();
                    password = passwordTextView.getText().toString();
                    // validations for input email and password
                    if (TextUtils.isEmpty(email))
                    {
                        Log.i("Login", "onClick: email checked :unsucess");
                        Toast.makeText(v.getContext(),"Please enter email!!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (TextUtils.isEmpty(password))
                    {
                        Log.i("Login", "onClick: password checked :unsucess");
                        Toast.makeText(v.getContext(),"Please enter password!!",Toast.LENGTH_LONG).show();
                        return;
                    }
                    Log.i("Login", "onClick:connecting with firebase ");
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                Log.i("Login", "login successful ");
                                Toast.makeText(v.getContext(),"Login successful!!",Toast.LENGTH_LONG).show();
                                // hide the progress bar
                                progressbar.setVisibility(View.INVISIBLE);
                                Log.i("Login", "Intent being proccesing ");
                                // if sign-in is successful
                                // intent to home activity
                                Intent intent = new Intent(login.this, Home.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                // sign-in failed
                                Toast.makeText(v.getContext(),"Login failed!!",Toast.LENGTH_LONG).show();
                                // hide the progress bar
                                progressbar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });

                }
                else
                {
                    Log.i("Login", "onClick: connectivity checked :unsucess");
                    Toast.makeText(v.getContext(),"There is no internet connection",Toast.LENGTH_SHORT).show();
                }
                // show the visibility of progress bar to show loading

            }
        });
    }
    public void link(View v)
    {
        Intent intent = new Intent(login.this, regestration.class);
        startActivity(intent);

    }
}
