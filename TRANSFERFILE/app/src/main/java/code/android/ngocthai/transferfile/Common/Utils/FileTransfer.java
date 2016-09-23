package code.android.ngocthai.transferfile.Common.Utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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

//    public String getFileName(Uri uri) {
//        String result = null;
//        if (uri.getScheme().equals("content")) {
////            Cursor cursor = .query(uri, null, null, null, null);
//            try {
//                if (cursor != null && cursor.moveToFirst()) {
//                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                }
//            } finally {
//                cursor.close();
//            }
//        }
//        if (result == null) {
//            result = uri.getPath();
//            int cut = result.lastIndexOf('/');
//            if (cut != -1) {
//                result = result.substring(cut + 1);
//            }
//        }
//        return result;
//    }

    /**
     * Show choose file and return is URI of FIle
     *
     * @param activity activity called this function
     */
    public static void showChooser(Activity activity) {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, activity.getString(R.string.chooser_title));
        try {
            activity.startActivityForResult(intent, ValuesConst.REQUEST_CODE_CHOOSE_FILE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }

    /**
     * Send file to server
     *
     * @param file_path file path of file will send
     * @param socket
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void SendFile(String file_path, Socket socket, final Activity activity) throws IOException, ClassNotFoundException {
        File file = new File(file_path);
        String file_name = file.getName();
        byte[] buf = new byte[1024];

        //---send file name to server---
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        oos.writeObject(ValuesConst.pass_transfer + "," + file_name);

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

        //---receive response of server---
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        final String msg = (String) ois.readObject();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "" + msg, Toast.LENGTH_SHORT).show();
            }
        });

        //---important---
//        socket.shutdownOutput();
        fis.close();
        bos.close();
    }

    public static void ReceiveFile(Socket socket, final Activity activity) throws IOException, ClassNotFoundException {

        byte[] b = new byte[1024];
        int length = 0;
        int byte_count = 1024;

        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        String msg = (String) ois.readObject();
        if (!msg.isEmpty()) {
            String[] temp = msg.split(",");
            if (temp[0].equalsIgnoreCase(ValuesConst.pass_transfer)) {
                //---true pass of app---
                if (!temp[1].isEmpty()) {
                    //---create a new file---
                    File file = new File(Environment.getExternalStorageDirectory() + "/" + temp[1]);
                    //---receive file and write file---
                    FileOutputStream fos = new FileOutputStream(file);
                    InputStream is = socket.getInputStream();
                    BufferedInputStream in2 = new BufferedInputStream(is, 1024);
                    while ((length = in2.read(b, 0, 1024)) != -1) {
                        byte_count += 1024;
                        fos.write(b, 0, length);
                    }
                    final String file_name = temp[1];
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Received " + file_name, Toast.LENGTH_SHORT).show();
                        }
                    });
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(ValuesConst.status_success);
                } else {
                    //---file name is null. can't create file
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Can't read file", Toast.LENGTH_SHORT).show();
                        }
                    });
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(ValuesConst.status_error);
                }
            } else {
                //---wrong pass---
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(ValuesConst.status_error);
            }
        } else {
            //---msg is null---
        }


    }

}
