package code.android.ngocthai.transferfile.Common.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Thaihn on 22/09/2016.
 */
public class ValuesConst {

    public static final int PORT_CONNECT = 1234;

    public static final int PORT_RESPONSE_SERVER = 3456;
    public static final int PORT_RESPONSE_CLIENT = 3457;
    public static final int PORT_SEND_FILE_SERVER = 2323;
    public static final int PORT_SEND_FILE_CLIENT = 2424;

    public static final String pass_transfer = "ngocthai96";

    public static final int REQUEST_CODE_CHOOSE_FILE_CLIENT = 1;
    public static final int REQUEST_CODE_CHOOSE_FILE_SERVER = 2;
    public static final int REQUEST_CODE_PERMISSION_CLIENT = 3;
    public static final int REQUEST_CODE_PERMISSION_SERVER = 4;

    public static final String status_success = "success";
    public static final String status_error = "error";

    public static final String key_send_ip_client = "CLIENTIP";
    public static final String key_send_ip_server = "SERVERIP";


    /**
     * Check permission of android 6.0+
     *
     * @param activity
     */
    public static void checkPermissionAndroidSixPlus(Activity activity, int request_code_permission, int request_code_choose) {

        //---Check request permission with android 6.0+---
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        request_code_permission);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            FileTransfer.showChooser(activity, request_code_choose);
        }
    }
}
