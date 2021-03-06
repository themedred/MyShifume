package com.ihm.shifumeredahibba;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.net.Socket;
import java.util.concurrent.ExecutionException;


public class GameActivity extends Activity {

    public final static int DISCONNECTED = 42;
    private TextView tvHim ;
    private TextView tvYou;
    private TextView tvResult;

    private Socket socket;

    ImageButton paperButton;
    ImageButton rockButton;
    ImageButton scissorButton;
    private Button btn_rematch;


    private Action my_move;
    private Action his_move;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        btn_rematch = findViewById(R.id.btn_rematch);
        btn_rematch.setEnabled(false);
        btn_rematch.setVisibility(View.GONE);
        btn_rematch.setOnClickListener(new RematchListener(this));

        tvResult = findViewById(R.id.tvResult);
        tvHim = findViewById(R.id.prout);
        tvYou = findViewById(R.id.tvYou);
        Intent intent = getIntent();
        WifiP2pInfo info = intent.getParcelableExtra("deviceInfo");


        socket = null;
        try {
            if(info.isGroupOwner){

                socket = new ServerConnectionInitiator().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tvHim, this).get();

            }else{
                socket = new ClientConnectionInitiator().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, info.groupOwnerAddress.getHostAddress(), new Integer(8888), tvHim, this).get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



        paperButton = findViewById(R.id.paperButton);
        rockButton = findViewById(R.id.rockButton);
        scissorButton = findViewById(R.id.scissorButton);



        paperButton.setOnClickListener(new GameClickListener(Action.PAPER, socket, tvYou, this));
        rockButton.setOnClickListener(new GameClickListener(Action.ROCK, socket, tvYou, this));
        scissorButton.setOnClickListener(new GameClickListener(Action.SCISSOR, socket, tvYou, this));


    }

    public void onMessageReceived(Action message){
        tvHim.setText("Waiting for your turn ...");
        his_move = message;
        resolution();
    }

    public void resolution(){
        if(my_move != null && his_move !=null){
            if(my_move == Action.PAPER){//paper
                if(his_move == Action.PAPER)
                    tvResult.setText("It's a Tie, try again.");
                else if(his_move == Action.ROCK){
                    tvResult.setText("You Won :D");
                }else
                    tvResult.setText("You Lost :( ");
            }else if(my_move == Action.ROCK){// rock
                if(his_move == Action.PAPER)
                    tvResult.setText("You Lost :( ");
                else if(his_move == Action.ROCK){
                    tvResult.setText("It's a Tie, try again.");
                }else
                    tvResult.setText("You Won :D");
            }else if(my_move == Action.SCISSOR){ //scissors
                if(his_move == Action.PAPER)
                    tvResult.setText("You Won :D");
                else if(his_move == Action.ROCK){
                    tvResult.setText("You Lost :( ");
                }else
                    tvResult.setText("It's a Tie, try again.");
            }else{
            }
            tvHim.setText("Enemy : " + his_move.getStringValue());
            btn_rematch.setEnabled(true);
            btn_rematch.setVisibility(View.VISIBLE);
        }


    }

    public void setMyMove(Action myMove) {
        this.my_move = myMove;
    }

    public void enableGameButton(boolean b){
        paperButton.setEnabled(b);
        scissorButton.setEnabled(b);
        rockButton.setEnabled(b);

    }

    public void reset(){
        enableGameButton(true);
        my_move = null;
        his_move = null;
        tvHim.setText("");
        tvYou.setText("");
        tvResult.setText("");
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void onBackPressed(){
        new MessageSender().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,socket, Action.DISCONNECT);
        this.finish();
    }


}
