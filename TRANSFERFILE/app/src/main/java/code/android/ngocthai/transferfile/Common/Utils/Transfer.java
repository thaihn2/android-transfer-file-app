package code.android.ngocthai.transferfile.Common.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import code.android.ngocthai.transferfile.Common.Support.MySocket;
import code.android.ngocthai.transferfile.Modules.Transfer.Response;
import code.android.ngocthai.transferfile.R;

/**
 * Created by Thaihn on 26/09/2016.
 */
public class Transfer {

    /**
     * Client send file to server with the same port_send_file
     */
    public static class ClientSendFile extends AsyncTask<Void, Void, Void> {

        private String file_path, ip_server;
        private int port;
        private Activity activity;
        private ProgressDialog progressDialog;
        private boolean result;

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
         * @param port      port_send_file of connect socket
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

            try {
                socket = new Socket(ip_server, port);
                //---send file and file name to server---
//
                FileTransfer.SendFile(file_path, socket);
                result = true;
                //---response from server---
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            } catch (ClassNotFoundException w) {
                w.printStackTrace();
                result = false;
            } finally {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (result) {
                Toast.makeText(activity, "Send success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Send fail", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * Server receive file from client
     */
    public static class ServerReceiveFile extends Thread {

        private ServerSocket serverSocket;
        private Activity activity;
        private int port_send_file, port_response;
        private String ip_partner;

        /**
         * Constructor default to add values to this function
         *
         * @param serverSocket
         * @param activity
         * @param port_send_file
         * @param ip_partner
         */
        public ServerReceiveFile(ServerSocket serverSocket, Activity activity, int port_send_file, String ip_partner) {
            this.serverSocket = serverSocket;
            this.activity = activity;
//            this.port_response = port_response;
            this.ip_partner = ip_partner;
            this.port_send_file = port_send_file;
        }

        @Override
        public void run() {
            super.run();

            Socket socket = null;

            try {
                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(port_send_file));

                while (true) {
                    socket = serverSocket.accept();
                    //---result of receive file---
                    String result = FileTransfer.ReceiveFile(socket);
                    if (!result.equalsIgnoreCase("")) {
                        String[] str = result.split(",");
                        final String file_name = str[1];
                        if (str[0].equalsIgnoreCase("true")) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "Received " + file_name, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "Can't receive file", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        //---receive is null---
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
                final String error = e.toString();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "" + error, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                final String error = e.toString();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "" + error, Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {

            }
        }
    }

//    public static class ServerReceiveFile extends Thread {
//
//        private ServerSocket serverSocket;
//        private Activity activity;
//        private int port_send_file, port_response;
//        private String ip_partner;
//        private boolean status_receive;
//
//        /**
//         * Constructor default to add values to this function
//         *
//         * @param serverSocket
//         * @param activity
//         * @param port_send_file
//         * @param port_response
//         * @param ip_partner
//         */
//        public ServerReceiveFile(ServerSocket serverSocket, Activity activity, int port_send_file, int port_response, String ip_partner) {
//            this.serverSocket = serverSocket;
//            this.activity = activity;
//            this.port_response = port_response;
//            this.ip_partner = ip_partner;
//            this.port_send_file = port_send_file;
//        }
//
//        @Override
//        public void run() {
//            super.run();
//
//            Socket socket = null;
//
//            try {
//                serverSocket = new ServerSocket();
//                serverSocket.setReuseAddress(true);
//                serverSocket.bind(new InetSocketAddress(port_send_file));
//
//                while (true) {
//                    socket = serverSocket.accept();
//                    do {
//                        //---result of receive file---
//                        String result = FileTransfer.ReceiveFile(socket);
//                        if (!result.equals("")) {
//                            String[] str = result.split(",");
//                            final String file_name = str[1];
//                            if (str[0].equalsIgnoreCase("true")) {
//                                //---receive success---
//                                activity.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(activity, "Receive " + file_name, Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                                status_receive = true;
//                            } else {
//                                status_receive = false;
//                            }
//                        }
//                    } while (!status_receive);
//
//                    if (status_receive) {
//                        Response.ClientResponse clientResponse = new Response.ClientResponse(ip_partner, "success", port_response);
//                        clientResponse.execute();
//                    } else {
//                        Response.ClientResponse clientResponse = new Response.ClientResponse(ip_partner, "fail", port_response);
//                        clientResponse.execute();
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (ClassNotFoundException w) {
//                w.printStackTrace();
//            } finally {
//                MySocket.closeSocket(socket);
//            }
//        }
//    }
}