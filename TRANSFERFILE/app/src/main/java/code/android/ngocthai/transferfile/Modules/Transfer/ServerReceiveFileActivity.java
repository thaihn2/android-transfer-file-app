package code.android.ngocthai.transferfile.Modules.Transfer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.net.ServerSocket;

import code.android.ngocthai.transferfile.Common.Utils.TCPServer;
import code.android.ngocthai.transferfile.Common.Utils.ValuesConst;
import code.android.ngocthai.transferfile.R;

public class ServerReceiveFileActivity extends AppCompatActivity {

    private ServerSocket serverSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_receive_file);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        TCPServer.ServerReceiveFile serverReceiveFile = new TCPServer.ServerReceiveFile(serverSocket, ServerReceiveFileActivity.this, ValuesConst.PORT_SEND_FILE);
        serverReceiveFile.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
