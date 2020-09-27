package in.kay.flixtube.UI.IntroUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;

import in.kay.flixtube.R;
import in.kay.flixtube.Utils.Helper;

public class SignupActivity extends AppCompatActivity {

    EditText etusername, etpassword, etemail, etconfirmpassword;
    private FirebaseAuth mAuth;
    String name, email, password, confirmpassword;
    DatabaseReference rootRef;
    Helper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        helper=new Helper();
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        etemail = findViewById(R.id.et_email);
        etusername = findViewById(R.id.et_firstname);
        etpassword = findViewById(R.id.et_password);
        etconfirmpassword = findViewById(R.id.et_confirmpassword);
        findViewById(R.id.tv_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             GotoLoginActivity();
            }
        });
        findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {
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
        confirmpassword = etconfirmpassword.getText().toString();

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
        if(!password.equals(confirmpassword)){
            etconfirmpassword.setError("Passwords do not match");
            etconfirmpassword.requestFocus();
            return;
        }
        else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mAuth.getCurrentUser().sendEmailVerification();
                        TastyToast.makeText(SignupActivity.this,"Welcome "+name+ " to Flixtube. Please check your mail for further process",TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                        Createdatabase();
                        GotoLoginActivity();
                        SendUserWelcomeMail(email,name);
                    } else
                        TastyToast.makeText(SignupActivity.this,task.getException().getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);

                }
            });
        }

    }

    private void SendUserWelcomeMail(String email, String name) {
        BackgroundMail.newBuilder(this)
                .withUsername(helper.decryptedMsg("Flixtube","oOnn5LVNkpTVyu1K619Po1pN0BBMN+9y5RCT9ALXjto="))
                .withPassword(helper.decryptedMsg("Flixtube","RkS001zLel/UerhgNCNJGw=="))
                .withProcessVisibility(false)
                .withMailto(email)
                .withType(BackgroundMail.TYPE_HTML)
                .withSubject("Welcome "+name)
                .withBody("<html lang=\"en\" data-lt-installed=\"true\"><head>\n" +
                        "    <meta charset=\"utf-8\">\n" +
                        "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                        "    <title></title>\n" +
                        "    <link href=\"https://fonts.googleapis.com/css?family=Lato:300,400|Montserrat:700\" rel=\"stylesheet\" type=\"text/css\">\n" +
                        "\n" +
                        "<style>\n" +
                        "    @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@800&display=swap');\n" +
                        "    @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@200&display=swap');\n" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body style=\"background-image: url('https://1.bp.blogspot.com/-xbuQ3STFAxY/X2x6eGIe-nI/AAAAAAAAAAc/pqe7fThqZqk6xMl5dzL8VYjhd7VukGmUQCNcBGAsYHQ/s1920/intro_back.png'); background-attachment: fixed; background-repeat: no-repeat;background-size: cover;\">\n" +
                        "    <div class=\"adn ads\" style=\"display:block\" data-message-id=\"#msg-f:1678715116486140118\" data-legacy-message-id=\"174bfe81153c20d6\">\n" +
                        "        <div class=\"aju\">\n" +
                        "        </div>\n" +
                        "        <div id=\":lo\">\n" +
                        "            <div class=\"qQVYZb\"></div>\n" +
                        "            <div class=\"utdU2e\"></div>\n" +
                        "            <div class=\"lQs8Hd\" jsaction=\"SN3rtf:rcuQ6b\" jscontroller=\"i3Ohde\"></div>\n" +
                        "            <div class=\"btm\"></div>\n" +
                        "        </div>\n" +
                        "        <div class=\"\">\n" +
                        "            <div class=\"aHl\"></div>\n" +
                        "            <div id=\":mt\" tabindex=\"-1\"></div>\n" +
                        "            <div id=\":lm\" class=\"ii gt\">\n" +
                        "                <div id=\":ln\" class=\"a3s aXjCH msg-735102637744627034\"><u></u>\n" +
                        "                    <div width=\"100%\" bgcolor=\"#0d253f\" style=\"position: relative;top: 5rem;margin:0;\">\n" +
                        "                        <center style=\"width:100%;text-align:left;\">\n" +
                        "                            <div style=\"max-width:680px;margin:auto\" class=\"m_-735102637744627034email-container\">\n" +
                        "                                <table role=\"presentation\" aria-hidden=\"true\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\" width=\"100%\" style=\"max-width:680px\">\n" +
                        "                                    <tbody>\n" +
                        "                                        <tr>\n" +
                        "                                            <td style=\"padding-top:30px;padding-left:20px;padding-right:20px;text-align:left\">\n" +
                        "                                                <img style=\"display:block\" src=\"https://1.bp.blogspot.com/-nSfV5mMt7RM/X2yVffDd5lI/AAAAAAAAAAo/H6vrGaWJE7Uwk1Pr9xxE3PRbwQp8kS7ewCNcBGAsYHQ/s1304/Logo.png\" aria-hidden=\"true\" width=\"auto\" height=\"58\"  border=\"0\" class=\"CToWUd\">\n" +
                        "                                            </td>\n" +
                        "                                        </tr>\n" +
                        "                                    </tbody>\n" +
                        "                                </table>\n" +
                        "                                <table class=\"m_-735102637744627034email-container\" role=\"presentation\" aria-hidden=\"true\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\" width=\"100%\" style=\"max-width:680px\">\n" +
                        "                                    <tbody>\n" +
                        "                                        <tr>\n" +
                        "                                            <td>\n" +
                        "                                                <table role=\"presentation\" aria-hidden=\"true\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n" +
                        "                                                    <tbody>\n" +
                        "                                                        <tr>\n" +
                        "                                                            <td style=\"padding:40px 20px 0 20px;text-align:left;font-family:'Source Sans Pro',-apple-system,BlinkMacSystemFont,Helvetica,Arial,sans-serif;color:#fff\">\n" +
                        "                                                                <h2 style=\"font-size: 2.5rem;font-family: 'Poppins', sans-serif;font-weight:700;letter-spacing:0.08em;margin:0 0 8px 0;color:#fff;\">\n" +
                        "                                                                    "+name+", welcome to Flixtube!</h2>\n" +
                        "                                                                <hr style=\"text-align:left;margin:0px;width:40px;height:3px;color:#01b4e4;background-color:#01b4e4;border-radius:4px;border:none\">\n" +
                        "\n" +
                        "                                                                <p style=\"font-weight:300;color:#fff;font-family: 'Poppins', sans-serif;font-size: 15px;\">\n" +
                        "                                                                    Thanks for registering an account on Flixtube.We're\n" +
                        "                                                                    excited to see you join the community! As a member\n" +
                        "                                                                    of Flixtube, you are getting a free account by which\n" +
                        "                                                                    you can watch lot of new movies every week.</p>\n" +
                        "\n" +
                        "                                                                <p style=\"font-size:15px;font-family: 'Poppins', sans-serif;font-weight:300;color:#fff;\">\n" +
                        "                                                                    The best thing about Flixtube app is that we provide\n" +
                        "                                                                    user ads free experience with end to end\n" +
                        "                                                                    Encryption..</p>\n" +
                        "                                                                <p style=\"margin:40px 0;color:#fff\"></p>\n" +
                        "                                                            </td>\n" +
                        "                                                        </tr>\n" +
                        "                                                    </tbody>\n" +
                        "                                                </table>\n" +
                        "                                            </td>\n" +
                        "                                        </tr>\n" +
                        "                                    </tbody>\n" +
                        "                                </table>\n" +
                        "                                <table class=\"m_-735102637744627034email-container\" role=\"presentation\" aria-hidden=\"true\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\" width=\"100%\" style=\"max-width:680px\">\n" +
                        "                                    <tbody>\n" +
                        "                                        <tr>\n" +
                        "                                            <td style=\"padding:30px 20px 30px 20px\">\n" +
                        "                                                <hr style=\"color:#fff;height:1px;border:0;background-color:#fff\">\n" +
                        "                                            </td>\n" +
                        "                                        </tr>\n" +
                        "                                        <tr>\n" +
                        "                                        </tr>\n" +
                        "                                        <tr>\n" +
                        "                                            <td align=\"left\" valign=\"top\" style=\"color:#fff;font-family:'Open Sans',Helvetica,Arial,sans-serif;font-size:13px;font-weight:normal;padding:0 20px\">\n" +
                        "                                                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-collapse:collapse\">\n" +
                        "                                                    <tbody>\n" +
                        "                                                        <tr>\n" +
                        "                                                        </tr>\n" +
                        "                                                    </tbody>\n" +
                        "                                                </table>\n" +
                        "                                            </td>\n" +
                        "                                        </tr>\n" +
                        "                                        <tr>\n" +
                        "                                            <td style=\"padding:0 20px 40px 20px;width:100%;font-size:13px;font-family:'Source Sans Pro',Arial,sans-serif;text-align:left;color:#fff\" class=\"m_-735102637744627034x-gmail-data-detectors\">\n" +
                        "                                                <p style=\"margin:0;padding:0;font-size:13px\">You are receiving this\n" +
                        "                                                    email because you are a registered user on Flixtube</p>\n" +
                        "                                            </td>\n" +
                        "                                        </tr>\n" +
                        "                                    </tbody>\n" +
                        "                                </table>\n" +
                        "                            </div>\n" +
                        "                        </center>\n" +
                        "                        <div class=\"yj6qo\"></div>\n" +
                        "                        <div class=\"adL\">\n" +
                        "                        </div>\n" +
                        "                    </div>\n" +
                        "                    <div class=\"adL\">\n" +
                        "                    </div>\n" +
                        "                </div>\n" +
                        "            </div>\n" +
                        "            <div id=\":mp\" class=\"ii gt\" style=\"display:none\">\n" +
                        "                <div id=\":mo\" class=\"a3s aXjCH undefined\"></div>\n" +
                        "            </div>\n" +
                        "            <div class=\"hi\"></div>\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "    <div class=\"ajx\"></div>\n" +
                        "</body>\n" +
                        "</html>")
                .send();
    }

    private void GotoLoginActivity() {
        Intent intent=new Intent(SignupActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void Createdatabase() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("Email", helper.encryptString(name,email));
        map.put("Name", name);
        map.put("MobileUid",helper.encryptString(name,helper.deviceId(getBaseContext())));
        map.put("Membership", "Normal");
        map.put("Violation", "No");
        rootRef.child("User").child(mAuth.getCurrentUser().getUid()).setValue(map);
    }

}
