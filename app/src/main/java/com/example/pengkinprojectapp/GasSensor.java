//package com.example.pengkinprojectapp;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothSocket;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.Menu;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.UUID;
//
//public class GasSensor extends Activity {
//
//    private ProgressDialog progress;
//    BluetoothAdapter myBluetooth = null;
//
//    BluetoothSocket btSocket = null;
//    private Boolean isBtConnected = false;
//
//    String address = null;
//    TextView myLabel;
//    EditText myTextbox;
//
//    private InputStream inputStream = null;
//
//    private OutputStream outputStream = null;
//    BluetoothAdapter mBluetoothAdapter;
//    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //사용할 UUID
//    BluetoothSocket mmSocket;
//    BluetoothDevice mmDevice;
//    OutputStream mmOutputStream;
//    InputStream mmInputStream;
//    Thread workerThread;
//    byte[] readBuffer;
//    int readBufferPosition;
//
//    volatile boolean stopWorker;
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.GasSensor, menu);
//        return true;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        Intent newint = getIntent();
//        address = newint.getStringExtra(BluetoothActivity.EXTRA_ADRESS); //메인 레이어에서 전송
//
//        Button openButton = (Button) findViewById(R.id.open);
//        Button closeButton = (Button) findViewById(R.id.close);
//        myLabel = (TextView) findViewById(R.id.label);
//
//
//
//        new BTbaglan().execute();
//
//
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                receiveData();
//            }
//        });
//    }
//
//
//    public void receiveData() {
//        final Handler handler = new Handler();
//        //데이터 수신을 위한 버퍼 생성
//        readBufferPosition = 0;
//        readBuffer = new byte[1024];
//
//        //데이터 수신을 위한 쓰레드 생성
//        workerThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (!Thread.currentThread().isInterrupted()) {
//                    try {
//                        //데이터 수신 확인
//                        int byteAvailable = inputStream.available();
//                        //데이터 수신 된 경우
//                        if (byteAvailable > 0) {
//                            //입력 스트림에서 바이트 단위로 읽어옴
//                            byte[] bytes = new byte[byteAvailable];
//                            inputStream.read(bytes);
//                            //입력 스트림 바이트를 한 바이트씩 읽어옴
//                            for (int i = 0; i < byteAvailable; i++) {
//                                final byte tempByte = bytes[i];
//                                //개행문자를 기준으로 받음 (한줄)
//                                if (tempByte == '\n') {
//                                    //readBuffer 배열을 encodeBytes로 복사
//                                    byte[] encodedBytes = new byte[readBufferPosition];
//                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
//                                    //인코딩 된 바이트 배열을 문자열로 변환
//                                    final String text = new String(encodedBytes, "UTF-8");
//                                    readBufferPosition = 0;
//                                    handler.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            //여기서 센서값을 받을 예정!
//                                            myLabel.setText(text);
//                                        }
//                                    });
//                                } // 개행문자가 아닐경우
//                                else {
//                                    readBuffer[readBufferPosition++] = tempByte;
//                                }
//                            }
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//
//                    }
//                }
//                try {
//                    //1초 마다 받아옴
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        workerThread.start();
//    }
//
//    void sendData() throws IOException {
//        String msg = myTextbox.getText().toString();
//        msg += "";
//        myLabel.setText("Data Sent " + msg);
//    }
//
//    void onButton() throws IOException {
//        mmOutputStream.write("1".getBytes());
//    }
//
//    void offButton() throws IOException {
//        mmOutputStream.write("2".getBytes());
//    }
//
//    void closeBT() throws IOException {
//        stopWorker = true;
//        mmOutputStream.close();
//        mmInputStream.close();
//        mmSocket.close();
//        myLabel.setText("Bluetooth Closed");
//    }
//
//    private class BTbaglan extends AsyncTask<Void, Void, Void> {
//        private boolean ConnectSuccess = true;
//
//        @Override
//        protected void onPreExecute() {
//            progress = ProgressDialog.show(GasSensor.this, "연결 중...", "잠시만 기다려주세요...");
//        }
//
//        @Override
//        protected Void doInBackground(Void... devices) {
//            try {
//                if (btSocket == null || !isBtConnected) {
//                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
//                    BluetoothDevice cihaz = myBluetooth.getRemoteDevice(address);
//                    btSocket = cihaz.createInsecureRfcommSocketToServiceRecord(myUUID);
//                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
//                    btSocket.connect();
//                    mmOutputStream = btSocket.getOutputStream();
//                    mmInputStream = btSocket.getInputStream();
//
//                    outputStream = btSocket.getOutputStream();
//                    inputStream = btSocket.getInputStream();
//
////                    receiveData();
//
//                }
//            } catch (IOException e) {
//                ConnectSuccess = false;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            if (!ConnectSuccess) {
//                // msg("연결 오류입니다. 다시 시도해 주세요.");
//                Toast.makeText(getApplicationContext(), "연결 오류입니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
//                finish();
//            } else {
//                //   msg("연결 성공");
//                Toast.makeText(getApplicationContext(), "연결 성공", Toast.LENGTH_SHORT).show();
//
//                isBtConnected = true;
//            }
//            progress.dismiss();
//        }
//
//    }
//
//
//
//}
