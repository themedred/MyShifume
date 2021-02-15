package com.ihm.shifumeredahibba;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class MessageReceiver extends AsyncTask<Object, Integer, String>{
    
    @Override
    protected String doInBackground(Object[] objects) {

        Socket socket = (Socket) objects[0];
        final GameActivity activity = (GameActivity) objects[2];
        try {

            while(true){
                final DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                final Action message = Action.valueOf(inputStream.readInt());
                Log.d("MessageReceiver", String.valueOf(message));
                activity.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        if(message.equals(Action.REPLAY)) {
                            activity.reset();
                        }else if(message.equals(Action.DISCONNECT)){
                            activity.finishAffinity();
                        }else
                            activity.onMessageReceived(message);

                    }
                });
            }

        } catch (IOException e) {
            Log.e("MessageReceiver", e.getMessage());
            return null;
        }
    }
}
