package in.kay.flixtube;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class signup extends AppCompatActivity implements View.OnClickListener {

    EditText etusername, etpassword, etemail;
    private FirebaseAuth mAuth;
    String name,email,password;
    DatabaseReference rootRef;
    //public static String uname="in.kay.flixtube.name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        etemail = findViewById(R.id.email);
        etusername = findViewById(R.id.name);
        etpassword = findViewById(R.id.password);
        findViewById(R.id.textlogin).setOnClickListener(this);
        findViewById(R.id.buttonsignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    private void signUp() {

        name = etusername.getText().toString();
        email = etemail.getText().toString();
        password = etpassword.getText().toString();

        if (name.isEmpty()) {
            etusername.setError("Username is required");
            etusername.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            etemail.setError("Email cannot be empty");
            etemail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etemail.setError("Please enter a valid email id");
            etemail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etpassword.setError("Password cannot be empty");
            etpassword.requestFocus();
            return;
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        mAuth.getCurrentUser().sendEmailVerification();
                        Toast.makeText(signup.this, name + ", you are registered successfully, check your email for verification", Toast.LENGTH_LONG).show();
                      // Intent intent= new Intent(signup.this,navactivity.class);
                       // intent.putExtra(uname,name);
                        //startActivity(intent);
                        createdatabase();
                    } else
                        Toast.makeText(signup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }

    }
        private void createdatabase() {
            HashMap<String, Object> map = new HashMap<>();
            map.put("Email", email);
            map.put("Name", name);
            map.put("Membership","Normal");
            map.put("Violation","No");
            rootRef.child("User").child(mAuth.getCurrentUser().getUid()).setValue(map);
        }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textlogin:
                startActivity(new Intent(this, login.class));
                break;

        }

    }
}


