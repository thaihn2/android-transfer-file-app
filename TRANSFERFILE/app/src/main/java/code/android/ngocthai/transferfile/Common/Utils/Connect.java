package code.android.ngocthai.transferfile.Common.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import code.android.ngocthai.transferfile.Common.Support.MySocket;
import code.android.ngocthai.transferfile.Modules.Transfer.ClientSendFileActivity;
import code.android.ngocthai.transferfile.Modules.Transfer.ServerReceiveFileActivity;

/**
 * Created by Thaihn on 26/09/2016.
 */
public class Connect {

    /**
     * Client connect to server
     */
    public static class ClientConnect extends AsyncTask<Void, Void, Void> {

        private String ip_address_server;
        private String response_from_server = "";
        private String my_ip_address;
        private String msg_to_server = ValuesConst.pass_transfer + "," + my_ip_address;
        private int port;
        private Activity activity;

        /**
         * Constructor default to add values
         *
         * @param ip_server ip of server connect to
         * @param port      port for connect
         * @param activity  activity called this function
         */
        public ClientConnect(String ip_server, String my_ip, int port, Activity activity) {
            this.ip_address_server = ip_server;
            this.port = port;
            this.my_ip_address = my_ip;
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... params) {

            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(ip_address_server, port);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                if (!msg_to_server.isEmpty()) {
                    //---write message to server---
                    dataOutputStream.writeUTF(msg_to_server);
                }
                //---receive msg from server---
                response_from_server = dataInputStream.readUTF();

            } catch (IOException e) {
                e.printStackTrace();
                response_from_server = "IOException : " + e.toString();
            } finally {
                //---close connect---
                MySocket.closeSocket(socket);
                MySocket.closeDataInput(dataInputStream);
                MySocket.closeDataOutput(dataOutputStream);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (response_from_server.equalsIgnoreCase(ValuesConst.status_success)) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "" + response_from_server, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity, ClientSendFileActivity.class);
                        intent.putExtra(ValuesConst.key_send_ip_client, ip_address_server);
                        activity.startActivity(intent);
                    }
                });
            } else if (response_from_server.equalsIgnoreCase(ValuesConst.status_error)) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "error connect. please try again", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "" + response_from_server, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }


    /**
     * Class connect server with client using thread and always listen from client.
     */
    public static class ServerConnect extends Thread {

        private ServerSocket serverSocket;
        private Activity activity;

        /*
        Constructor default to add values
         */
        public ServerConnect(ServerSocket serverSocket, Activity activity) {
            this.serverSocket = serverSocket;
            this.activity = activity;
        }

        @Override
        public void run() {
            //---create socket and data input, output to send and receive data from client---
            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;

            try {
                //---create server socket to listen from server---
                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(ValuesConst.PORT_CONNECT));

                while (true) {
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    String msg_from_client = "";
                    //---if message from client is null program is break---
                    msg_from_client = dataInputStream.readUTF();

                    if (msg_from_client.equalsIgnoreCase("")) {
                        //---no msg--
                        dataOutputStream.writeUTF(ValuesConst.status_error);
                    } else {
                        String[] temp = msg_from_client.split(",");
                        String pass = temp[0];
                        final String ip = temp[1];
                        if (pass.equalsIgnoreCase(ValuesConst.pass_transfer)) {
                            //---match pass---
                            if (!ip.isEmpty()) {
                                dataOutputStream.writeUTF(ValuesConst.status_success);
                                //---close socket and data---
                                MySocket.closeDataOutput(dataOutputStream);
                                MySocket.closeSocket(socket);
                                MySocket.closeDataInput(dataInputStream);
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, "Connected with " + ip, Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(activity, ServerReceiveFileActivity.class);
                                        activity.startActivity(i);
                                    }
                                });
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //---close connect with client---
                MySocket.closeSocket(socket);
                MySocket.closeDataOutput(dataOutputStream);
                MySocket.closeDataInput(dataInputStream);
            }
        }
    }
}
