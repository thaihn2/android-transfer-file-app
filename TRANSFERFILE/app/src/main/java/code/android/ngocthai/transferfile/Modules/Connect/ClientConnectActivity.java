package code.android.ngocthai.transferfile.Modules.Connect;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import code.android.ngocthai.transferfile.Common.Support.MySocket;
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
                    Snackbar.make(view, "Ip is null", Snackbar.LENGTH_SHORT).show();
                } else {
                    Connect.ClientConnect clientConnect = new Connect.ClientConnect(ip, MySocket.getMyIpAddress(), ValuesConst.PORT_CONNECT, ClientConnectActivity.this);
                    clientConnect.execute();
                }
            }
        });
    }

}
