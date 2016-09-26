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

    /**
     * Server receive file from client
     */
    public static class ServerReceiveFile extends Thread {

        private ServerSocket serverSocket;
        private Activity activity;

        public ServerReceiveFile(ServerSocket serverSocket, Activity activity) {
            this.serverSocket = serverSocket;
            this.activity = activity;
        }

        @Override
        public void run() {
            super.run();

            Socket socket = null;

            try {
                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(ValuesConst.PORT_SEND_FILE));

                while (true) {
                    socket = serverSocket.accept();

                    String result = FileTransfer.ReceiveFile(socket, activity);

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
                MySocket.closeSocket(socket);
            }
        }
    }
}
