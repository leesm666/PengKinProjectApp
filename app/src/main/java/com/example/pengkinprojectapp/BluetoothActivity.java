package com.example.pengkinprojectapp;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {



    BluetoothAdapter myBluetooth;  // 나의 블루투스
    private Set<BluetoothDevice> pairedDevices;  //주변 블루투스 장치
    Button toggle_button, pair_button;
    ListView pairedlist;
    public static String EXTRA_ADRESS = "device_address";
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();  //블루투스 기능 여부 표시
        toggle_button = (Button) findViewById(R.id.button_toggle); //블루투스 on/off 버튼
        pair_button = (Button) findViewById(R.id.button_pair);  //블루투스 리스트 확인 버튼
        pairedlist = (ListView) findViewById(R.id.device_list); // 리스트뷰

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_ADVERTISE,
                            Manifest.permission.BLUETOOTH_CONNECT


                    },
                    1);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.BLUETOOTH

                    },
                    1);
        }

        String[] permission_list = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(BluetoothActivity.this, permission_list, 1);


        toggle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBluetooth();


            }


        });

        pair_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listdevice();
            }
        });


    }


    private void toggleBluetooth() {  //블루투스 on/off 버튼 메소드
        if (myBluetooth == null) {
            Toast.makeText(getApplicationContext(), "블루투스 장치가 없습니다.", Toast.LENGTH_SHORT).show(); //블루투스 off 상태를 알려줌
        }

        if (!myBluetooth.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivity(enableBTIntent);


        }
        if (myBluetooth.isEnabled()) {
            myBluetooth.disable();
        }

    }

    private void listdevice() {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice bt: pairedDevices)
            {
                list.add(bt.getName() + "\n"+bt.getAddress()); //찾은 장치의 이름과 주소를 리스트뷰에 추가

            }
        }

        else
        {
            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.",Toast.LENGTH_SHORT).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        pairedlist.setAdapter(adapter);
        pairedlist.setOnItemClickListener(selectDevice); // 선택한 장치로 연결



    }

    public AdapterView.OnItemClickListener selectDevice = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length()-17); //주소 정의

            Intent comintent = new Intent (BluetoothActivity.this, Comunication.class);
            comintent.putExtra(EXTRA_ADRESS,address ); //레이아웃 전송
            startActivity(comintent);
        }
    };



}
