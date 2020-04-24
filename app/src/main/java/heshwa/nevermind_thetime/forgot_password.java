package heshwa.nevermind_thetime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedInputStream;

public class forgot_password extends AppCompatActivity
{
    EditText send_email;
    Button btn_reset;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        send_email=findViewById(R.id.send_email);
        btn_reset=findViewById(R.id.btn_reset);
        firebaseAuth=FirebaseAuth.getInstance();

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=send_email.getText().toString();
                if(email.equals(""))
                {
                    Toast.makeText(forgot_password.this,"All fields are required",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(forgot_password.this,"Please check your email",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(forgot_password.this,login.class));
                            }
                            else
                            {
                             String error=task.getException().getMessage();
                                Toast.makeText(forgot_password.this,error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }
}
