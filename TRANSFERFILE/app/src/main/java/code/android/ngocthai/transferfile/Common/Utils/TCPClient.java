package code.android.ngocthai.transferfile.Common.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
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
import code.android.ngocthai.transferfile.R;

/**
 * Created by Thaihn on 22/09/2016.
 */
public class TCPClient {


//    public static class ClientSendResponse extends AsyncTask<Void, Void, Void> {
//
//        private String ip_address_server;
//        private int port;
//        private String msg_to_server;
//
//        public ClientSendResponse(String ip_server, int port, String msg_to_server) {
//            this.ip_address_server = ip_server;
//            this.msg_to_server = msg_to_server;
//            this.port = port;
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            Socket socket = null;
//            DataOutputStream dataOutputStream = null;
//            DataInputStream dataInputStream = null;
//
//            try {
//                socket = new Socket(ip_address_server, port);
//
//                if (!msg_to_server.isEmpty()) {
//                    dataOutputStream.writeUTF(msg_to_server);
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                MySocket.closeSocket(socket);
//                MySocket.closeDataInput(dataInputStream);
//                MySocket.closeDataOutput(dataOutputStream);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//        }
//    }

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
     * Client send file to server with the same port
     */
    public static class ClientSendFile extends AsyncTask<Void, Void, Void> {

        private String file_path, ip_server;
        private int port;
        private Activity activity;
        private String msg_server = "";
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(activity.getString(R.string.dialog_sending));
            progressDialog.show();
        }

        /**
         * Constructor default to add values
         *
         * @param file_path file path of file will send
         * @param ip_server ip of server connect to
         * @param activity  activity called this function
         * @param port      port of connect socket
         */
        public ClientSendFile(String file_path, String ip_server, Activity activity, int port) {
            this.file_path = file_path;
            this.ip_server = ip_server;
            this.port = port;
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... params) {

            Socket socket = null;
//            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(ip_server, port);
                //---send file and file name to server---
                FileTransfer.SendFile(file_path, socket, activity);
                //---response from server---
//                dataInputStream = new DataInputStream(socket.getInputStream());
//                msg_server = dataInputStream.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
                msg_server = "IO Exception : " + e.toString();
            } catch (ClassNotFoundException w) {
                w.printStackTrace();
                msg_server = "ClassNotFound : " + w.toString();
            } finally {
//                MySocket.closeDataInput(dataInputStream);
                MySocket.closeSocket(socket);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (msg_server.equalsIgnoreCase(ValuesConst.status_success)) {
                //---send file success to server---
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "Sent file with " + ip_server, Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (msg_server.equalsIgnoreCase(ValuesConst.status_error)) {
                //---send file fail to server---
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "Send file fail with " + ip_server, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "Exception : " + msg_server, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

//    /**
//     * Client send file to server
//     */
//    public static class ClientSendFile extends Thread {
//
//        private String file_path, ip_server;
//        private int port;
//        private Activity activity;
//
//        /**
//         * Constructor default to add values
//         *
//         * @param file_path file path of file will send
//         * @param ip_server ip of server connect to
//         * @param activity  activity called this function
//         * @param port      port of connect socket
//         */
//        public ClientSendFile(String file_path, String ip_server, Activity activity, int port) {
//            this.file_path = file_path;
//            this.ip_server = ip_server;
//            this.port = port;
//            this.activity = activity;
//        }
//
//        @Override
//        public void run() {
//            super.run();
//
//            Socket socket = null;
//
//            try {
//
//                while (true) {
//                    socket = new Socket(ip_server, port);
//                    //---send file in here---
//                    FileTransfer.SendFile(file_path, socket, activity);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                final String error = e.toString();
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(activity, "IOException" + error, Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            } finally {
//                MySocket.closeSocket(socket);
//            }
//        }
//    }
}
