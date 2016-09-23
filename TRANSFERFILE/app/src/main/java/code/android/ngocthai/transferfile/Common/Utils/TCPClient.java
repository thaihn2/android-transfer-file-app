package code.android.ngocthai.transferfile.Common.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import code.android.ngocthai.transferfile.Common.Support.MySocket;
import code.android.ngocthai.transferfile.Modules.Transfer.ClientSendFileActivity;

/**
 * Created by Thaihn on 22/09/2016.
 */
public class TCPClient {

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

    public static class ClientSendFile extends Thread {

        private String file_name, file_path, ip_server;
        private int port;
        private Activity activity;

        public ClientSendFile(String file_name, String file_path, String ip_server, Activity activity, int port) {
            this.file_name = file_name;
            this.file_path = file_path;
            this.ip_server = ip_server;
            this.port = port;
            this.activity = activity;
        }


        @Override
        public void run() {
            super.run();

            Socket socket = null;

            try {
                socket = new Socket(ip_server, port);
                while (true) {
                    //---send file in here---
                    FileTransfer.SendFile(file_path, socket, activity);
                }
            } catch (IOException e) {
                e.printStackTrace();
                final String error = e.toString();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "IOException" + error, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                MySocket.closeSocket(socket);
            }
        }
    }

//    public static class ClientSendFile extends AsyncTask<Void, Void, Void> {
//
//        private final Socket socket;
//        private String file_name, file_path, ip_server;
//        private int port;
//        private Activity activity;
//
//
//        /**
//         * Constructor default to add values
//         *
//         * @param socket
//         */
//        public ClientSendFile(Socket socket, String file_name, String file_path, String ip_server, int port, Activity activity) {
//            this.socket = socket;
//            this.file_name = file_name;
//            this.activity = activity;
//            this.file_path = file_path;
//            this.ip_server = ip_server;
//            this.port = port;
//        }
//
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            Socket socket = null;
//
//            try {
//                socket = new Socket(ip_server, port);
//                FileTransfer.SendFile(file_path, socket, file_name, activity);
//            } catch (IOException e) {
//                e.printStackTrace();
//                final String error = e.toString();
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(activity, "IOEXCEPTION : " + error, Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } catch (ClassNotFoundException e) {
//                final String error = e.toString();
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(activity, "ClassNotFound " + error, Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } finally {
//                MySocket.closeSocket(socket);
//            }
//            return null;
//        }
//    }
}
