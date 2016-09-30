package code.android.ngocthai.transferfile.Modules.Transfer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.net.ServerSocket;

import code.android.ngocthai.filechooser.FileChoose.Utils.FileUtils;
import code.android.ngocthai.transferfile.Common.Support.MySocket;
import code.android.ngocthai.transferfile.Common.Utils.FileTransfer;
import code.android.ngocthai.transferfile.Common.Utils.Transfer;
import code.android.ngocthai.transferfile.Common.Utils.ValuesConst;
import code.android.ngocthai.transferfile.Modules.Connect.MainActivity;
import code.android.ngocthai.transferfile.R;

public class ClientSendFileActivity extends AppCompatActivity {

    private Button btn_choose;
    private Toolbar toolbar;
    private String file_path;
    private ServerSocket server_socket_file;
    private String ip_partner;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_send_file);

        initToolbar();
        initView();

    }

    /**
     * Support actionbar with toolbar
     */
    private void initToolbar() {
        //---toolbar---
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("FILE TRANSFER LAN");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {


        //---Choose file button---
        btn_choose = (Button) findViewById(R.id.btn_choose_file_client);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        Transfer.ServerReceiveFile serverReceiveFile = new Transfer.ServerReceiveFile(server_socket_file, this,9696, ip_partner);
        serverReceiveFile.start();

        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //---check permission of android 6.0 +
                ValuesConst.checkPermissionAndroidSixPlus(ClientSendFileActivity.this, ValuesConst.REQUEST_CODE_PERMISSION_CLIENT, ValuesConst.REQUEST_CODE_CHOOSE_FILE_CLIENT);
            }
        });

        //---button send file---
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFile_path().isEmpty()) {
                    Snackbar.make(view, "No file selected", Snackbar.LENGTH_SHORT).show();
                } else {
                    //---send file in here---
                    Transfer.ClientSendFile clientSendFile = new Transfer.ClientSendFile(getFile_path(), ip_partner, ClientSendFileActivity.this, 6969);
                    clientSendFile.execute();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ValuesConst.REQUEST_CODE_PERMISSION_CLIENT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    FileTransfer.showChooser(ClientSendFileActivity.this, ValuesConst.REQUEST_CODE_CHOOSE_FILE_CLIENT);
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
     * Called when activity start and resume
     */
    @Override
    protected void onStart() {
        super.onStart();
        ip_partner = getIntent().getStringExtra(ValuesConst.key_send_ip_client);
//        if(server_socket_file == null) {
//            Transfer.ServerReceiveFile serverReceiveFile = new Transfer.ServerReceiveFile(server_socket_file, this, 9696, ip_partner);
//            serverReceiveFile.start();
//        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        MySocket.closeServerSocket(server_socket_file);
//    }

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
            case ValuesConst.REQUEST_CODE_CHOOSE_FILE_CLIENT: {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        //---result of data is Uri---
                        setFile_path(FileUtils.getPath(ClientSendFileActivity.this, data.getData()));
                        Toast.makeText(ClientSendFileActivity.this, "" + getFile_path(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    /**
//     * Get ip of partner from client connect activity sent
//     *
//     * @return string ip of server connect to
//     */
//    private void getIpPartner() {
//
//    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

}
