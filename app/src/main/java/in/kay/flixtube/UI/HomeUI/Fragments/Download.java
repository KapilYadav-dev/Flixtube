package in.kay.flixtube.UI.HomeUI.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.sdsmdg.tastytoast.TastyToast;

import java.io.File;
import java.util.ArrayList;

import in.kay.flixtube.BuildConfig;
import in.kay.flixtube.R;
import in.kay.flixtube.UI.HomeUI.MainActivity;

public class Download extends Fragment {
    View view;
    ArrayList<String> arrayList=new ArrayList<>();
    ListView listView;
    Context mcontext;
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view=view;
        listView=view.findViewById(R.id.listview);
        String path = Environment.getExternalStorageDirectory().toString()+"/Flixtube/";
        final File f = new File(path);
        File file[] = f.listFiles();
        if (file==null)
        {
            view.findViewById(R.id.tv_not_found).setVisibility(View.VISIBLE);
            view.findViewById(R.id.iv_not_found).setVisibility(View.VISIBLE);
        }
        else
        {
            for (int i=0; i < file.length; i++)
            {
                arrayList.add(file[i].getName());
            }
            ArrayAdapter arrayAdapter=new ArrayAdapter(mcontext,R.layout.custom_list_view,R.id.textView,arrayList);
            listView.setAdapter(arrayAdapter);
            if (arrayList.size()==0)
            {
                view.findViewById(R.id.tv_not_found).setVisibility(View.VISIBLE);
                view.findViewById(R.id.iv_not_found).setVisibility(View.VISIBLE);
            }
            else
            {
                view.findViewById(R.id.tv_not_found).setVisibility(View.GONE);
                view.findViewById(R.id.iv_not_found).setVisibility(View.GONE);
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String movieName= (String) listView.getItemAtPosition(i);
                    File file = new File(Environment.getExternalStorageDirectory().toString()+"/Flixtube/"+movieName);
                    Uri fileUri = FileProvider.getUriForFile(mcontext, BuildConfig.APPLICATION_ID + ".provider", file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(fileUri, "video/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//DO NOT FORGET THIS EVER
                    startActivity(intent);
                }
            });
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_download, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mcontext=context;
    }
}