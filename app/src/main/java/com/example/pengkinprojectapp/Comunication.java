package com.example.pengkinprojectapp;

import android.annotation.SuppressLint;
    import android.app.ProgressDialog;
    import android.bluetooth.BluetoothAdapter;
    import android.bluetooth.BluetoothDevice;
    import android.bluetooth.BluetoothServerSocket;
    import android.bluetooth.BluetoothSocket;
    import android.content.Intent;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.view.MotionEvent;
    import android.view.View;
    import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;

    import java.io.IOException;
    import java.util.UUID;

public class Comunication extends AppCompatActivity {

    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;

    BluetoothSocket btSocket = null;
    BluetoothDevice remoteDevice;
    BluetoothServerSocket mmServer;

    private Boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //사용할 UUID

    Button A_Button, C_Button;
    ImageButton G_Button,B_Button,R_Button,L_Button, IL_Button, IR_Button;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comunication);
        Intent newint = getIntent();
        address = newint.getStringExtra(BluetoothActivity.EXTRA_ADRESS); //메인 레이어에서 전송

        G_Button = (ImageButton) findViewById(R.id.forward_button); // 전진 버튼
        B_Button = (ImageButton) findViewById(R.id.backward_button); // 후진 버튼
        L_Button = (ImageButton) findViewById(R.id.left_button); // 좌회전 버튼
        R_Button = (ImageButton) findViewById(R.id.right_button); // 우회전 버튼
        IL_Button = (ImageButton) findViewById(R.id.Inleft_button); // 좌회전 버튼
        IR_Button = (ImageButton) findViewById(R.id.Inright_button); // 우회전 버튼
        A_Button = (Button) findViewById(R.id.auto_button); // 자율주행모드 버튼
        C_Button = (Button) findViewById(R.id.control_button); // 수동조작모드 버튼

        G_Button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { //전진 버튼 키다운 이벤트

                if(btSocket!=null)
                {
                    switch(event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            try
                            {   // 'g'값 아두이노로 전송
                                btSocket.getOutputStream().write("g".toString().getBytes());
                            }
                            catch (IOException e) //오류 예외처리
                            {
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            try
                            {    // 정지인 's'값 전송
                                btSocket.getOutputStream().write("s".toString().getBytes());
                            }
                            catch (IOException e)
                            {
                            }
                            break;
                    }


                }
                return false;
            }
        });

        R_Button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { //우회전 버튼 키다운 이벤트

                if(btSocket!=null)
                {
                    switch(event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            try
                            {
                                btSocket.getOutputStream().write("r".toString().getBytes());
                            }

                            catch (IOException e)
                            {

                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            try
                            {
                                btSocket.getOutputStream().write("s".toString().getBytes());
                            }

                            catch (IOException e)
                            {

                            }
                            break;

                    }


                }
                return false;
            }
        });

        L_Button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { //좌회전 버튼 키다운 이벤트

                if(btSocket!=null)
                {
                    switch(event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            try
                            {
                                btSocket.getOutputStream().write("l".toString().getBytes());
                            }

                            catch (IOException e)
                            {

                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            try
                            {
                                btSocket.getOutputStream().write("s".toString().getBytes());
                            }

                            catch (IOException e)
                            {

                            }
                            break;

                    }


                }
                return false;
            }
        });

        B_Button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { //후진 버튼 키다운 이벤트

                if(btSocket!=null)
                {
                    switch(event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            try
                            {
                                btSocket.getOutputStream().write("b".toString().getBytes());
                            }

                            catch (IOException e)
                            {

                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            try
                            {
                                btSocket.getOutputStream().write("s".toString().getBytes());
                            }

                            catch (IOException e)
                            {

                            }
                            break;

                    }


                }
                return false;
            }
        });

        IR_Button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { //제자리 우회전 버튼 키다운 이벤트

                if(btSocket!=null)
                {
                    switch(event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            try
                            {
                                btSocket.getOutputStream().write("w".toString().getBytes());
                            }

                            catch (IOException e)
                            {

                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            try
                            {
                                btSocket.getOutputStream().write("s".toString().getBytes());
                            }

                            catch (IOException e)
                            {

                            }
                            break;

                    }


                }
                return false;
            }
        });

        IL_Button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { //제자리 좌회전 버튼 키다운 이벤트

                if(btSocket!=null)
                {
                    switch(event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            try
                            {
                                btSocket.getOutputStream().write("q".toString().getBytes());
                            }

                            catch (IOException e)
                            {

                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            try
                            {
                                btSocket.getOutputStream().write("s".toString().getBytes());
                            }

                            catch (IOException e)
                            {

                            }
                            break;

                    }


                }
                return false;
            }
        });

        A_Button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { //자율주행모드 버튼 키다운 이벤트

                if(btSocket!=null)
                {

                    try {
                        btSocket.getOutputStream().write("a".toString().getBytes());
                    } catch (IOException e) {

                    }

                }
                return false;
            }
        });

        C_Button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { //수동조작모드 버튼 키다운 이벤트

                if(btSocket!=null)
                {

                    try {
                        btSocket.getOutputStream().write("c".toString().getBytes());
                    } catch (IOException e) {

                    }

                }
                return false;
            }
        });



        new BTbaglan().execute();


    }




        private void Disconnect() {
            if (btSocket != null) {
                try {
                    btSocket.close();

                } catch (IOException e) {
                    //msg("Error");
                }
            }
            finish();
        }

        @Override
        public void onBackPressed() {
            super.onBackPressed();
            Disconnect();
        }


        private class BTbaglan extends AsyncTask<Void, Void, Void> {
            private boolean ConnectSuccess = true;

            @Override
            protected void onPreExecute() {
                progress = ProgressDialog.show(Comunication.this, "연결 중...", "잠시만 기다려주세요...");
            }

            @Override
            protected Void doInBackground(Void... devices) {
                try {
                    if (btSocket == null || !isBtConnected) {
                        myBluetooth = BluetoothAdapter.getDefaultAdapter();
                        BluetoothDevice cihaz = myBluetooth.getRemoteDevice(address);
                        btSocket = cihaz.createInsecureRfcommSocketToServiceRecord(myUUID);
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        btSocket.connect();


                    }
                } catch (IOException e) {
                    ConnectSuccess = false;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if (!ConnectSuccess) {
                    // msg("연결 오류입니다. 다시 시도해 주세요.");
                    Toast.makeText(getApplicationContext(), "연결 오류입니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    //   msg("연결 성공");
                    Toast.makeText(getApplicationContext(), "연결 성공", Toast.LENGTH_SHORT).show();

                    isBtConnected = true;
                }
                progress.dismiss();
            }

        }

}
