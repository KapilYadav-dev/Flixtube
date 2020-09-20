package in.kay.flixtube.Utils;


import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

import in.dd4you.appsconfig.DD4YouConfig;

public class Helper {
    DD4YouConfig dd4YouConfig;
    public String encryptString(String password,String str) {
        try {
            String encryptedMsg = AESCrypt.encrypt(password, str);
            return encryptedMsg;
        }catch (GeneralSecurityException e){
            return "Error";
        }
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

    public void DownloadFile(Context context,String title,String type,String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(type);
        request.setTitle(title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title+".mp4");
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
}
