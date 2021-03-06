package com.example.milo.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUESTCODE_OPEN = 1;
    private ListView mListView;
    private BluetoothController mController = new BluetoothController();
    private BroadcastReceiver mBTReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 1);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Toast.makeText(MainActivity.this, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.STATE_ON:
                    Toast.makeText(MainActivity.this, "蓝牙已开启", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Toast.makeText(MainActivity.this, "正在开启蓝牙", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Toast.makeText(MainActivity.this, "正在关闭蓝牙", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(MainActivity.this, "未知状态", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBTReceiver, intentFilter);
        mListView = (ListView) findViewById(R.id.bt_devices_lv);
        findViewById(R.id.is_support_btn).setOnClickListener(this);
        findViewById(R.id.is_opened_btn).setOnClickListener(this);
        findViewById(R.id.open_btn).setOnClickListener(this);
        findViewById(R.id.close_btn).setOnClickListener(this);
        findViewById(R.id.search_btn).setOnClickListener(this);
        findViewById(R.id.visible_btn).setOnClickListener(this);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.is_support_btn:
                boolean flag = mController.isSupportBluetooth();
                Toast.makeText(MainActivity.this, "flag: " + flag, Toast.LENGTH_SHORT).show();
                break;
            case R.id.is_opened_btn:
                boolean isTurnOn = mController.getBluetoothStatus();
                Toast.makeText(MainActivity.this, "isTurnOn: " + isTurnOn, Toast.LENGTH_SHORT).show();
                break;
            case R.id.open_btn:
                mController.turnOnBluetooth(this, REQUESTCODE_OPEN);
                break;
            case R.id.close_btn:
                mController.turnOffBluetooth();
                mListView.setVisibility(View.GONE);
                break;
            case R.id.visible_btn:
                Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(getVisible, 0);
                break;
            case R.id.search_btn:
                /*pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();

                ArrayList list = new ArrayList();
                for (BluetoothDevice bt : pairedDevices)
                    list.add(bt.getName());*/
                mController.discoveryBluetooth();
                mListView.setVisibility(View.VISIBLE);
                break;
        }
    }

    // 创建一个接收ACTION_FOUND广播的BroadcastReceiver
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 发现设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Toast.makeText(MainActivity.this, "Showing Devices", Toast.LENGTH_SHORT).show();
                // 从Intent中获取设备对象
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                ArrayList list = new ArrayList();
                for (int i = 0; i < device.getAddress().length(); i++) {
                    list.add(device.getName() + " --- " + device.getAddress());
                }
                ArrayAdapter mArrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, list);
                mListView.setAdapter(mArrayAdapter);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK) {
            Toast.makeText(MainActivity.this, "终于打开了", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unregisterReceiver(mBTReceiver);
    }
}
