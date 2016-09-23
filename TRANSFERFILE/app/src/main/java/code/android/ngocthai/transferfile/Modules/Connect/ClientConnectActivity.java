package code.android.ngocthai.transferfile.Modules.Connect;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import code.android.ngocthai.transferfile.Common.Support.MySocket;
import code.android.ngocthai.transferfile.Common.Utils.TCPClient;
import code.android.ngocthai.transferfile.Common.Utils.ValuesConst;
import code.android.ngocthai.transferfile.R;

public class ClientConnectActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private EditText edt_ip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_connect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edt_ip = (EditText) findViewById(R.id.edt_ip_server);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = edt_ip.getText().toString();
                if (ip.isEmpty()) {
                    Toast.makeText(ClientConnectActivity.this, "IP is null", Toast.LENGTH_SHORT).show();
                } else {
                    TCPClient.ClientConnect clientConnect = new TCPClient.ClientConnect(ip, MySocket.getMyIpAddress(), ValuesConst.PORT_CONNECT, ClientConnectActivity.this);
                    clientConnect.execute();
                }
            }
        });
    }

}
