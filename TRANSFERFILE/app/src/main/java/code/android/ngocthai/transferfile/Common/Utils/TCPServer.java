package code.android.ngocthai.transferfile.Common.Utils;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import code.android.ngocthai.transferfile.Common.Support.MySocket;
import code.android.ngocthai.transferfile.Modules.Transfer.ServerReceiveFileActivity;

/**
 * Created by Thaihn on 22/09/2016.
 */
public class TCPServer {

    public static class ServerConnect extends Thread {

        private ServerSocket serverSocket;
        private Activity activity;

        public ServerConnect(ServerSocket serverSocket, Activity activity) {
            this.serverSocket = serverSocket;
            this.activity = activity;
        }

        @Override
        public void run() {

            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;

            try {
                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(ValuesConst.PORT_CONNECT));

                while (true) {
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    String msg_from_client = "";
                    msg_from_client = dataInputStream.readUTF();

                    if (msg_from_client.equalsIgnoreCase("")) {
                        //---no msg--
                        dataOutputStream.writeUTF(ValuesConst.status_error);
                    } else {
                        String[] temp = msg_from_client.split(",");
                        String pass = temp[0];
                        String ip = temp[1];
                        if (pass.equalsIgnoreCase(ValuesConst.pass_transfer)) {
                            //---match pass---
                            if (!ip.isEmpty()) {
                                dataOutputStream.writeUTF(ValuesConst.status_success);
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
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

    public static class ServerReceiveFile extends Thread {

        private ServerSocket serverSocket;
        private Activity activity;
        private int port;

        public ServerReceiveFile(ServerSocket serverSocket, Activity activity, int port) {
            this.serverSocket = serverSocket;
            this.activity = activity;
            this.port = port;
        }

        @Override
        public void run() {
            super.run();

            Socket socket = null;

            try {
                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(ValuesConst.PORT_CONNECT));

                while (true) {
                    socket = serverSocket.accept();
                    FileTransfer.ReceiveFile(socket, activity);
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
                MySocket.closeSocket(socket);
            }
        }
    }
}
