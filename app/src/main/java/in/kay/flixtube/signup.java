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

public class signup extends AppCompatActivity implements View.OnClickListener {

    EditText etusername, etpassword, etemail;
    private FirebaseAuth mAuth;
    String name,email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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

        mAuth = FirebaseAuth.getInstance();
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
        }
        if (password.length() < 6) {
            etpassword.setError("Password should have minimum 6 characters");
            etpassword.requestFocus();
            return;
        }
        else{
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(signup.this, name+", you are registered successfully", Toast.LENGTH_SHORT).show();
                    }

                    else
                        Toast.makeText(signup.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                }
            });
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textlogin:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }

    }
}


