package com.ihm.shifumeredahibba;

import android.os.AsyncTask;
import android.view.View;



class RematchListener implements View.OnClickListener {

    private GameActivity activity;


    public RematchListener(GameActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        activity.reset();
        new MessageSender().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,activity.getSocket(),Action.REPLAY);
    }
}
