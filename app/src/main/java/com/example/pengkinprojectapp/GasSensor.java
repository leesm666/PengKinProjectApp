package com.example.pengkinprojectapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class GasSensor extends Activity {

    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;

    BluetoothSocket btSocket = null;
    private Boolean isBtConnected = false;

    String address = null;
    TextView mylabel, userGuardian;
    EditText myTextbox;

    private InputStream inputStream = null;

    private OutputStream outputStream = null;
    BluetoothAdapter mBluetoothAdapter;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //사용할 UUID
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;

    volatile boolean stopWorker;

    private static final int REQUEST_CODE = 100;

    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ex_main);

        Intent newint = getIntent();
        address = newint.getStringExtra(BluetoothActivity.EXTRA_ADRESS); //메인 레이어에서 전송

        Button autoBtn = (Button) findViewById(R.id.autoBtn);
        mylabel = (TextView) findViewById(R.id.air);
        userGuardian = findViewById(R.id.userGuardian);


        new BTbaglan().execute();

//        receiveData();


        autoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                receiveData();
            }
        });

        //권한 허용
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {android.Manifest.permission.SEND_SMS};

                requestPermissions(permissions, REQUEST_CODE);

            }
        }
    }



    public void receiveData() {
        final Handler handler = new Handler();
        //데이터 수신을 위한 버퍼 생성
        readBufferPosition = 0;
        readBuffer = new byte[1024];

        //데이터 수신을 위한 쓰레드 생성
        workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        //데이터 수신 확인
                        int byteAvailable = inputStream.available();
                        //데이터 수신 된 경우
                        if (byteAvailable > 0) {
                            //입력 스트림에서 바이트 단위로 읽어옴
                            byte[] bytes = new byte[byteAvailable];
                            inputStream.read(bytes);
                            //입력 스트림 바이트를 한 바이트씩 읽어옴
                            for (int i = 0; i < byteAvailable; i++) {
                                final byte tempByte = bytes[i];
                                //개행문자를 기준으로 받음 (한줄)
                                if (tempByte == '\n') {
                                    //readBuffer 배열을 encodeBytes로 복사
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    //인코딩 된 바이트 배열을 문자열로 변환
                                    String text = new String(encodedBytes, "UTF-8");

//                                    int text = Integer.parseInt(new String(encodedBytes, "UTF-8"));

                                    int gaslevel;
                                    try {
                                        gaslevel = Integer.parseInt(text.trim());
                                    } catch (NumberFormatException ex) {
                                        ex.printStackTrace();
                                        gaslevel = 0;
                                    }
                                    System.out.println(gaslevel);
                                    readBufferPosition = 0;


                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            //여기서 센서값을 받을 예정!
                                            mylabel.setText(text);

//                                            // 파이어베이스 realtime database에서 phoneNum 값 가져오기
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Appproject");

                                            mDatabaseRef.child("UserAccount").child(user.getUid()).child("phoneNum")
                                                    .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            String value = snapshot.getValue(String.class);

                                                            userGuardian.setText(value);


                                                            int gaslevel =0;
                                                            String message = "가스 누출 경고문자";
                                                            if (value.length() > 0 && gaslevel > 400) {
                                                                __sendSMS(value, message);
                                                                Toast.makeText(getBaseContext(), "문자 전송 완료",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                            else if (value.length()<0) {
                                                                Toast.makeText(getBaseContext(), "전화번호 값이 없습니다.",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                        }
                                    });
                                } // 개행문자가 아닐경우
                                else {
                                    readBuffer[readBufferPosition++] = tempByte;
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
                try {
                    //1초 마다 받아옴
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        workerThread.start();


    }

    void sendData() throws IOException {
        String msg = myTextbox.getText().toString();
        msg += "";
//        air.setText("Data Sent " + msg);
    }

    void onButton() throws IOException {
        mmOutputStream.write("1".getBytes());
    }

    void offButton() throws IOException {
        mmOutputStream.write("2".getBytes());
    }

    void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
//        air.setText("Bluetooth Closed");
    }

    // 모니터링 안하고 발송을 원한다면 아래 함수를 이용
    private void __sendSMS(String phoneNumber, String message)
    {
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, Sendsms.class), PendingIntent.FLAG_IMMUTABLE);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
        Toast.makeText(getBaseContext(), "문자 전송 완료", Toast.LENGTH_SHORT).show();
    }

    private class BTbaglan extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(GasSensor.this, "연결 중...", "잠시만 기다려주세요...");
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
                    mmOutputStream = btSocket.getOutputStream();
                    mmInputStream = btSocket.getInputStream();

                    outputStream = btSocket.getOutputStream();
                    inputStream = btSocket.getInputStream();

//                    receiveData();

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
