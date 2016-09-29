package code.android.ngocthai.transferfile.Common.Utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import code.android.ngocthai.filechooser.FileChoose.Utils.FileUtils;
import code.android.ngocthai.transferfile.R;

/**
 * Created by Thaihn on 23/09/2016.
 */
public class FileTransfer {

    /**
     * Show choose file and return is URI of FIle
     *
     * @param activity activity called this function
     */
    public static void showChooser(Activity activity, int request_code) {
        //---Use the GET_CONTENT intent from the utility class---
        Intent target = FileUtils.createGetContentIntent();
        //---Create the chooser Intent---
        Intent intent = Intent.createChooser(
                target, activity.getString(R.string.chooser_title));
        try {
            activity.startActivityForResult(intent, request_code);
        } catch (ActivityNotFoundException e) {
            //---The reason for the existence of aFileChooser---
        }
    }

    /**
     * Send file to server
     *
     * @param file_path file path of file will send
     * @param socket    socket of transfer file
     * @throws IOException            exception input or output of read file
     * @throws ClassNotFoundException
     */
    public static void SendFile(String file_path, Socket socket) throws IOException, ClassNotFoundException {
        //---create with real file path and get all information of file---
        File file = new File(file_path);
        String file_name = file.getName();
        byte[] buf = new byte[1024];

        //---send file name to server and pass to check in server---
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        oos.writeObject(ValuesConst.pass_transfer + "," + file_name);
        //---read file to send to server---
        OutputStream os = socket.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        FileInputStream fis = new FileInputStream(file);

        int i;
        int byte_count = 1024;
        while ((i = fis.read(buf, 0, 1024)) != -1) {
            byte_count += 1024;
            bos.write(buf, 0, i);
            bos.flush();
        }
    }


//    public static void SendFile(String file_path, Socket socket, final Activity activity) throws IOException, ClassNotFoundException {
//        //---create with real file path and get all information of file---
//        File file = new File(file_path);
//        String file_name = file.getName();
//        byte[] buf = new byte[1024];
//
//        //---send file name to server and pass to check in server---
//        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//        oos.flush();
//        oos.writeObject(ValuesConst.pass_transfer + "," + file_name);
//        //---read file to send to server---
//        OutputStream os = socket.getOutputStream();
//        BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
//        FileInputStream fis = new FileInputStream(file);
//
//        int i;
//        int byte_count = 1024;
//        while ((i = fis.read(buf, 0, 1024)) != -1) {
//            byte_count += 1024;
//            bos.write(buf, 0, i);
//            bos.flush();
//        }
//        //---response of server
//        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//        final String msg = (String) ois.readObject();
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(activity, "" + msg, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    /**
     * Receive file from client
     *
     * @param socket socket send file
     * @return file name and result write file
     * @throws IOException            error if IO Exception
     * @throws ClassNotFoundException error not found class
     */
    public static String ReceiveFile(Socket socket) throws IOException, ClassNotFoundException {

        byte[] b = new byte[1024];
        int length = 0;
        String result = "";
        String file_name = "";
        int byte_count = 1024;
        //---receive msg fom client---
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        String msg = (String) ois.readObject();

        if (!msg.isEmpty()) {
            String[] temp = msg.split(",");
            file_name = temp[1];
            if (temp[0].equalsIgnoreCase(ValuesConst.pass_transfer)) {
                //---true pass of app---
                if (!temp[1].isEmpty()) {
                    //---create a new file---
                    File file = new File(Environment.getExternalStorageDirectory() + "/" + temp[1]);

                    //---receive file and write file---
                    FileOutputStream fos = new FileOutputStream(file);
                    InputStream is = socket.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is, 1024);
                    while ((length = bis.read(b, 0, 1024)) != -1) {
                        byte_count += 1024;
                        fos.write(b, 0, length);
                    }
                    result = "true";
                } else {
                    //---file name is null. can't create file
                    result = "false";
                }
            } else {
                //---wrong pass word---
                result = "false";
            }
        } else {
            //---msg is null---
            result = "false";
        }
        return result + "," + file_name;
    }

//    public static void sendFile(String file_path, Socket socket) throws IOException {
//        File f = new File(file_path);
//
//        long length = f.length();
//        String name = f.getName();
//        byte[] bytes = new byte[16 * 1024];
//
//        //---send file name to server and pass to check in server---
//        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//        oos.flush();
//        oos.writeObject(ValuesConst.pass_transfer + "," + name);
//
//        InputStream is = new FileInputStream(f);
//        OutputStream os = socket.getOutputStream();
//
//        int count;
//        while ((count = is.read(bytes)) > 0) {
//            os.write(bytes, 0, count);
//        }
//    }
//
//    public static void receiveFile(Socket socket) throws IOException, ClassNotFoundException {
//
//        //---receive msg fom client---
//        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//        String msg = (String) ois.readObject();
//
//        String[] temp = msg.split(",");
//
//
//
//    }

}
