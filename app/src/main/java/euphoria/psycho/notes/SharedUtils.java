package euphoria.psycho.notes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;

public class SharedUtils {

    private static Context sContext;


    public static File getExternalStorageDirectoryFile(String fileName) {
        return new File(Environment.getExternalStorageDirectory(), fileName);
    }

    public static Context getContext() {
        return sContext;
    }

    public static void setContext(Context context) {
        sContext = context;
    }

    public static int requestPermissions(Activity activity) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int r = 99;
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, r);
            return r;
        }
        return 0;

    }

}
