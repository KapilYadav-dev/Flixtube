package in.kay.flixtube.UI.IntroUI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.sdsmdg.tastytoast.TastyToast;

import in.kay.flixtube.R;
import in.kay.flixtube.UI.HomeUI.MainActivity;

public class mail extends AppCompatActivity {

    Button btn,back;
    EditText msg,sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);
        msg=findViewById(R.id.etmsg);
        sub=findViewById(R.id.etsub);
        btn=findViewById(R.id.sendbtn);
        back=findViewById(R.id.backbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                String[] to={"infoflixtube@gmail.com"};
                intent.putExtra(Intent.EXTRA_EMAIL,to);
                String message=msg.getText().toString();
                String subject=sub.getText().toString();
                intent.putExtra(Intent.EXTRA_SUBJECT,subject);
                intent.putExtra(Intent.EXTRA_TEXT,message);
                intent.setType("message/rfc822");
                if(!message.isEmpty())
                startActivity(Intent.createChooser(intent,"Send email via"));
                else
                    TastyToast.makeText(mail.this,"You cannot send an empty mail",TastyToast.LENGTH_SHORT,TastyToast.CONFUSING);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mail.this, MainActivity.class));
            }
        });

    }
}