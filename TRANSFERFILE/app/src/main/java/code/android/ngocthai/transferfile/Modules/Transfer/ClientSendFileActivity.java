package code.android.ngocthai.transferfile.Modules.Transfer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.net.ServerSocket;

import code.android.ngocthai.filechooser.FileChoose.Utils.FileUtils;
import code.android.ngocthai.transferfile.Common.Support.MySocket;
import code.android.ngocthai.transferfile.Common.Utils.FileTransfer;
import code.android.ngocthai.transferfile.Common.Utils.TCPClient;
import code.android.ngocthai.transferfile.Common.Utils.ValuesConst;
import code.android.ngocthai.transferfile.Modules.Connect.ClientConnectActivity;
import code.android.ngocthai.transferfile.R;

public class ClientSendFileActivity extends AppCompatActivity {

    private Button btn_choose;
    private Toolbar toolbar;
    private String file_path;
    private String file_name;
    private FloatingActionButton fab;
    private ServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_send_file);

        initView();

    }

    private void initView() {
        //---toolbar---
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //---Choose file button---
        btn_choose = (Button) findViewById(R.id.btn_choose_file);
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //---Check request permission with android 6.0+---
                if (ContextCompat.checkSelfPermission(ClientSendFileActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ClientSendFileActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                        FileTransfer.showChooser(ClientSendFileActivity.this);

                    } else {

                        // No explanation needed, we can request the permission.

                        FileTransfer.showChooser(ClientSendFileActivity.this);

                        ActivityCompat.requestPermissions(ClientSendFileActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                ValuesConst.REQUEST_CODE_PERMISSION);
                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    FileTransfer.showChooser(ClientSendFileActivity.this);
                }
            }
        });

        //---button send file---
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFile_path().isEmpty()) {
                    Snackbar.make(view, "No file selected", Snackbar.LENGTH_SHORT).show();
                } else {
                    //---send file in here---
                    TCPClient.ClientSendFile clientSendFile = new TCPClient.ClientSendFile(getFile_path(), getData(), ClientSendFileActivity.this, ValuesConst.PORT_SEND_FILE);
                    clientSendFile.execute();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ValuesConst.REQUEST_CODE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    FileTransfer.showChooser(ClientSendFileActivity.this);

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Receive result when choose file in storage and sdcard
     *
     * @param requestCode code request when choose
     * @param resultCode  code result of choose file
     * @param data        data is kind of URI
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ValuesConst.REQUEST_CODE_CHOOSE_FILE: {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();
                        Log.i("Uri Receive", "onActivityResult: " + uri.toString());
                        final String path = FileUtils.getPath(ClientSendFileActivity.this, uri);
                        setFile_path(path);
                        Toast.makeText(ClientSendFileActivity.this, "File Selected : " + getFile_path(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Get key from Client connect
     *
     * @return string ip of server connect to
     */
    private String getData() {
        return getIntent().getStringExtra(ValuesConst.key_send_ip_client);
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

}
