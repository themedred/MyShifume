package com.ihm.shifumeredahibba;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;



public class ServerConnectionInitiator extends AsyncTask<Object, Integer, Socket>{

    private TextView tvHim;
    private Activity activity;
    @Override
    protected Socket doInBackground(Object[] objects) {

        Socket client = null;
        tvHim = (TextView) objects[0];
        activity = (Activity) objects[1];
        try {

            ServerSocket serverSocket = new ServerSocket(8888);
            client = serverSocket.accept();

            DataOutputStream mDataOutputStream = new DataOutputStream(client.getOutputStream());
            mDataOutputStream.writeUTF("Hello I'm the server");

            serverSocket.close();

        } catch (IOException e) {
            Log.e(WiFiDirectActivity.TAG, e.getMessage());
        }
        return client;
    }

    protected void onPostExecute(Socket socket) {
        new MessageReceiver().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, socket, tvHim, activity);
    }

}
