package com.dzt.btcommunication;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.dzt.btcommunication.adapter.ChatListAdapter;
import com.dzt.btcommunication.javabean.SiriListItem;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by M02323 on 2017/9/12.
 */

public class DeviceFragment extends Fragment {
	private ListView mListView;
	private ArrayList<SiriListItem> list;
	private Button seachButton, serviceButton;
	private ChatListAdapter mAdapter;
	private View view;
	/* 取得默认的蓝牙适配器 */
	private BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_device, container, false);
		initWidgets(view);
		return view;
	}

	private void initWidgets(View view) {
		list = new ArrayList<>();
		mAdapter = new ChatListAdapter(getActivity(), list);
		mListView = (ListView) view.findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setFastScrollEnabled(true);
		mListView.setOnItemClickListener(mDeviceClickListener);

		// Register for broadcasts when a device is discovered
		IntentFilter discoveryFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		getActivity().registerReceiver(mReceiver, discoveryFilter);

		// Register for broadcasts when discovery has finished
		IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		getActivity().registerReceiver(mReceiver, foundFilter);

		// Get a set of currently paired devices
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

		// If there are paired devices, add each one to the ArrayAdapter
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				list.add(new SiriListItem(device.getName() + "\n" + device.getAddress(), true));
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(list.size() - 1);
			}
		} else {
			list.add(new SiriListItem("没有设备已经配对", true));
			mAdapter.notifyDataSetChanged();
			mListView.setSelection(list.size() - 1);
		}

		seachButton = (Button) view.findViewById(R.id.start_seach);
		seachButton.setOnClickListener(seachButtonClickListener);

		serviceButton = (Button) view.findViewById(R.id.start_service);
		serviceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MainActivity.serviceOrClient = MainActivity.ServerOrClient.SERVICE;
				MainActivity activity = (MainActivity) getActivity();
				activity.setCurrentTab(1);
			}
		});
	}

	private View.OnClickListener seachButtonClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (mBtAdapter.isDiscovering()) {
				mBtAdapter.cancelDiscovery();
				seachButton.setText("重新搜索");
			} else {
				list.clear();
				mAdapter.notifyDataSetChanged();

				Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
				if (pairedDevices.size() > 0) {
					for (BluetoothDevice device : pairedDevices) {
						list.add(new SiriListItem(device.getName() + "\n" + device.getAddress(), true));
						mAdapter.notifyDataSetChanged();
						mListView.setSelection(list.size() - 1);
					}
				} else {
					list.add(new SiriListItem("No devices have been paired", true));
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(list.size() - 1);
				}
					/* 开始搜索 */
				mBtAdapter.startDiscovery();
				seachButton.setText("停止搜索");
			}
		}
	};

	// The on-click listener for all devices in the ListViews
	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			// Cancel discovery because it's costly and we're about to connect

			SiriListItem item = list.get(arg2);
			String info = item.message;
			String address = info.substring(info.length() - 17);
			MainActivity.BlueToothAddress = address;

			AlertDialog.Builder StopDialog = new AlertDialog.Builder(getActivity());//定义一个弹出框对象
			StopDialog.setTitle("连接");//标题
			StopDialog.setMessage(item.message);
			StopDialog.setPositiveButton("连接", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					mBtAdapter.cancelDiscovery();
					seachButton.setText("重新搜索");

					MainActivity.serviceOrClient = MainActivity.ServerOrClient.CLIENT;
					MainActivity activity = (MainActivity) getActivity();
					activity.setCurrentTab(1);
				}
			});
			StopDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					MainActivity.BlueToothAddress = null;
				}
			});
			StopDialog.show();
		}
	};
	// The BroadcastReceiver that listens for discovered devices and
	// changes the title when discovery is finished
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// If it's already paired, skip it, because it's been listed already
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					list.add(new SiriListItem(device.getName() + "\n" + device.getAddress(), false));
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(list.size() - 1);
				}
				// When discovery is finished, change the Activity title
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				//setProgressBarIndeterminateVisibility(false);
				if (mListView.getCount() == 0) {
					list.add(new SiriListItem("没有发现蓝牙设备", false));
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(list.size() - 1);
				}
				seachButton.setText("重新搜索");
			}
		}
	};

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}
		// Unregister broadcast listeners
		getActivity().unregisterReceiver(mReceiver);
	}
}
