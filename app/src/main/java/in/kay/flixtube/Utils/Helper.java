package in.kay.flixtube.Utils;


import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scottyab.aescrypt.AESCrypt;

import java.io.File;
import java.security.GeneralSecurityException;

import in.dd4you.appsconfig.DD4YouConfig;
import in.kay.flixtube.Model.UsersModel;

public class Helper {
    DD4YouConfig dd4YouConfig;

    public String encryptString(String password, String str) {
        try {
            String encryptedMsg = AESCrypt.encrypt(password, str);
            return encryptedMsg;
        }catch (GeneralSecurityException e){
            return "Error";
        }
    }
    public String getDeviceInfo(Context context) {
        dd4YouConfig=new DD4YouConfig(context);
        return dd4YouConfig.getDeviceManufacturer()+" "+dd4YouConfig.getDeviceModel();
    }
    public boolean isNetwork(Context context) {
       dd4YouConfig=new DD4YouConfig(context);
       return dd4YouConfig.isInternetConnectivity();
    }
    public String decryptedMsg(String password,String str) {
        try {
            String decryptedMsg = AESCrypt.decrypt(password, str);
            return decryptedMsg;
        }catch (GeneralSecurityException e){
            return "Error";
        }
    }
    public String deviceId(Context mcontext) {
        dd4YouConfig=new DD4YouConfig(mcontext);
        String id=dd4YouConfig.getDeviceId();
        return id;
    }
    public String Date(Context mcontext) {
        dd4YouConfig=new DD4YouConfig(mcontext);
        String date=dd4YouConfig.getCurrentDate();
        return date;
    }
    public String Time(Context mcontext)
    {
        dd4YouConfig=new DD4YouConfig(mcontext);
        String time=dd4YouConfig.getCurrentTime();
        return time;
    }

    public void DownloadFile(Context context, String title, String type, String url, String path) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(type);
        request.setTitle(title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(path, title+".mp4");
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
}
