package in.kay.flixtube;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;
    EditText etpassword, etemail;
    String email,password;
    TextView forgotpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth= FirebaseAuth.getInstance();
        etemail = findViewById(R.id.email);
        etpassword = findViewById(R.id.password);
        forgotpassword=findViewById(R.id.textreset);

        findViewById(R.id.textsignup).setOnClickListener(this);
        findViewById(R.id.buttonlogin).setOnClickListener(this);
        findViewById(R.id.textreset).setOnClickListener(this);
    }

    private void login(){

        email = etemail.getText().toString();
        password = etpassword.getText().toString();

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
        else{
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(login.this, "Successful sign-in", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.textsignup:
                startActivity(new Intent(login.this,signup.class));
                break;

            case R.id.buttonlogin:
                login();
                break;

            case R.id.textreset:
                final EditText resetmail=new EditText(view.getContext());
                AlertDialog.Builder resetdialog= new AlertDialog.Builder(view.getContext());
                resetdialog.setTitle("Reset password?");
                resetdialog.setMessage("Enter the email to receive the password reset link");
                resetdialog.setView(resetmail);

                resetdialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email= resetmail.getText().toString();
                        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(login.this, "Reset link has been sent", Toast.LENGTH_SHORT).show();

                                }
                                else {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                resetdialog.create().show();
                break;

        }
    }
}



