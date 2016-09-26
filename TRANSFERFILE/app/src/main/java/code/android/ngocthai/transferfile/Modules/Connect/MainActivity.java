package code.android.ngocthai.transferfile.Modules.Connect;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.net.ServerSocket;

import code.android.ngocthai.transferfile.Common.Support.MySocket;
import code.android.ngocthai.transferfile.R;

public class MainActivity extends AppCompatActivity {


    private ServerSocket serverSocket;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private TextView txt_ip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        //---toolbar---
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //---find view by id---
        txt_ip = (TextView) findViewById(R.id.txt_id_main);
        txt_ip.setText(MySocket.getMyIpAddress());
        //---fab---
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ClientConnectActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MySocket.closeServerSocket(serverSocket);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (serverSocket == null) {
            //---start server to listen client---
            Connect.ServerConnect serverConnect = new Connect.ServerConnect(serverSocket, MainActivity.this);
            serverConnect.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
