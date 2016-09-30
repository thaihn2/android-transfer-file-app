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

public class ServerReceiveFileActivity extends AppCompatActivity {

    private ServerSocket server_socket_file;
    //    private ServerSocket server_socket_response;
    private Toolbar toolbar;
    private String ip_partner;
    private Button btn_choose;
    private String file_path;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_receive_file);

        getIpPartner();
        Toast.makeText(ServerReceiveFileActivity.this, "" + ip_partner , Toast.LENGTH_SHORT).show();
        initToolbar();
        initView();

    }

    /**
     * Create support actionbar in here
     */
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_receive_file);
        toolbar.setTitle(R.string.toolbar_transfer_file);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Called when you click on item of actionbar or menu
     *
     * @param item
     * @return
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
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

        //---find view by id---
        btn_choose = (Button) findViewById(R.id.btn_choose_file_server);

        Transfer.ServerReceiveFile serverReceiveFile = new Transfer.ServerReceiveFile(server_socket_file, this, 6969, ip_partner);
        serverReceiveFile.start();

        //---choose file---
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValuesConst.checkPermissionAndroidSixPlus(ServerReceiveFileActivity.this, ValuesConst.REQUEST_CODE_PERMISSION_SERVER, ValuesConst.REQUEST_CODE_CHOOSE_FILE_SERVER);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab_receive_file);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFile_path().isEmpty()) {
                    Snackbar.make(view, "No file selected", Snackbar.LENGTH_SHORT).show();
                } else {
                    //---send file in here---
                    Transfer.ClientSendFile clientSendFile = new Transfer.ClientSendFile(getFile_path(), ip_partner, ServerReceiveFileActivity.this, 9696);
                    clientSendFile.execute();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ValuesConst.REQUEST_CODE_PERMISSION_SERVER: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    FileTransfer.showChooser(this, ValuesConst.REQUEST_CODE_CHOOSE_FILE_SERVER);
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

    @Override
    protected void onStart() {
        super.onStart();
        if (server_socket_file == null) {
            Transfer.ServerReceiveFile serverReceiveFile = new Transfer.ServerReceiveFile(server_socket_file, this, 6969, ip_partner);
            serverReceiveFile.start();
        }
    }


    /**
     * Receive result when choose file in storage and sdcard
     *
     * @param requestCode code request when choose
     * @param resultCode  code result of choose file
     * @param data        data return is kind of URI
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ValuesConst.REQUEST_CODE_CHOOSE_FILE_SERVER: {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        //---result of data is Uri---
                        setFile_path(FileUtils.getPath(this, data.getData()));
                        Toast.makeText(ServerReceiveFileActivity.this, "" + getFile_path(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
    Receive ip of partner
     */
    private void getIpPartner() {
        ip_partner = getIntent().getStringExtra(ValuesConst.key_send_ip_server);
    }

    /**
     * Close server socket when activity destroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MySocket.closeServerSocket(server_socket_file);
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

}
