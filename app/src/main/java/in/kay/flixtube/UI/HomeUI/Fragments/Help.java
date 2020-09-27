package in.kay.flixtube.UI.HomeUI.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.sdsmdg.tastytoast.TastyToast;

import in.kay.flixtube.R;


public class Help extends Fragment {

    Button btn;
    EditText msg,sub;

    View view;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view=view;
        msg=view.findViewById(R.id.etmsg);
        sub=view.findViewById(R.id.etsub);
        btn=view.findViewById(R.id.sendbtn);
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
                    TastyToast.makeText(getContext(),"You cannot send an empty mail",TastyToast.LENGTH_SHORT,TastyToast.CONFUSING);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help, container, false);
    }
}