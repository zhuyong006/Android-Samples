package com.dzt.btcommunication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dzt.btcommunication.adapter.deviceListAdapter;
import com.dzt.btcommunication.javabean.DeviceListItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by M02323 on 2017/9/12.
 */

public class ChatFragment extends Fragment implements AdapterView.OnItemClickListener,
		View.OnClickListener {
	private static final String TAG = "chatActivity";
	private ListView mListView;
	private ArrayList<DeviceListItem> list;
	private Button sendButton;
	private Button disconnectButton;
	private EditText editMsgView;
	private deviceListAdapter mAdapter;
	private View view;

	/* 一些常量，代表服务器的名称 */
	public static final String PROTOCOL_SCHEME_L2CAP = "btl2cap";
	public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";
	public static final String PROTOCOL_SCHEME_BT_OBEX = "btgoep";
	public static final String PROTOCOL_SCHEME_TCP_OBEX = "tcpobex";

	private BluetoothServerSocket serverSocket = null;
	private ServerThread startServerThread = null;
	private clientThread clientConnectThread = null;
	private BluetoothSocket socket = null;
	private BluetoothDevice device = null;
	private readThread readThread = null;
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_chat, container, false);
		initWidgets(view);
		return view;
	}

	private void initWidgets(View view) {
		list = new ArrayList<>();
		mAdapter = new deviceListAdapter(getActivity(), list);
		mListView = (ListView) view.findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setFastScrollEnabled(true);
		editMsgView = (EditText) view.findViewById(R.id.MessageText);
		editMsgView.clearFocus();

		sendButton = (Button) view.findViewById(R.id.btn_msg_send);
		sendButton.setOnClickListener(this);

		disconnectButton = (Button) view.findViewById(R.id.btn_disconnect);
		disconnectButton.setOnClickListener(this);
	}

	private Handler LinkDetectedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//Toast.makeText(mContext, (String)msg.obj, Toast.LENGTH_SHORT).show();
			if (msg.what == 1) {
				list.add(new DeviceListItem((String) msg.obj, true));
			} else {
				list.add(new DeviceListItem((String) msg.obj, false));
			}
			mAdapter.notifyDataSetChanged();
			mListView.setSelection(list.size() - 1);
		}
	};

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_msg_send:
				String msgText = editMsgView.getText().toString();
				if (msgText.length() > 0) {
					sendMessageHandle(msgText);
					editMsgView.setText("");
					editMsgView.clearFocus();
					//close InputMethodManager
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(editMsgView.getWindowToken(), 0);
				} else
					Toast.makeText(getActivity(), "发送内容不能为空！", Toast.LENGTH_SHORT).show();
				break;
			case R.id.btn_disconnect:
				if (MainActivity.serviceOrClient == MainActivity.ServerOrClient.CLIENT) {
					shutdownClient();
				} else if (MainActivity.serviceOrClient == MainActivity.ServerOrClient.SERVICE) {
					shutdownServer();
				}
				MainActivity.isOpen = false;
				MainActivity.serviceOrClient = MainActivity.ServerOrClient.NONE;
				Toast.makeText(getActivity(), "已断开连接！", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

	}

	public void startClientThread(String address) {
		device = mBluetoothAdapter.getRemoteDevice(address);
		clientConnectThread = new clientThread();
		clientConnectThread.start();
	}

	public void startServerThread() {
		startServerThread = new ServerThread();
		startServerThread.start();
	}

	//开启客户端
	private class clientThread extends Thread {
		public void run() {
			try {
				//创建一个Socket连接：只需要服务器在注册时的UUID号
				// socket = device.createRfcommSocketToServiceRecord(BluetoothProtocols.OBEX_OBJECT_PUSH_PROTOCOL_UUID);
				socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
				//连接
				Message msg2 = new Message();
				msg2.obj = "请稍候，正在连接服务器:" + MainActivity.BlueToothAddress;
				msg2.what = 0;
				LinkDetectedHandler.sendMessage(msg2);

				socket.connect();

				Message msg = new Message();
				msg.obj = "已经连接上服务端！可以发送信息。";
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg);
				//启动接受数据
				readThread = new readThread();
				readThread.start();
			} catch (IOException e) {
				Log.e("connect", "", e);
				Message msg = new Message();
				msg.obj = "连接服务端异常！断开连接重新试一试。";
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg);
			}
		}
	}

	//开启服务器
	private class ServerThread extends Thread {
		public void run() {
			try {
				/* 创建一个蓝牙服务器
				 * 参数分别：服务器名称、UUID	 */
				serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(PROTOCOL_SCHEME_RFCOMM,
						UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

				Log.d("server", "wait cilent connect...");

				Message msg = new Message();
				msg.obj = "请稍候，正在等待客户端的连接...";
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg);

				/* 接受客户端的连接请求 */
				socket = serverSocket.accept();
				Log.d("server", "accept success !");

				Message msg2 = new Message();
				String info = "客户端已经连接上！可以发送信息。";
				msg2.obj = info;
				msg.what = 0;
				LinkDetectedHandler.sendMessage(msg2);
				//启动接受数据
				readThread = new readThread();
				readThread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/* 停止服务器 */
	private void shutdownServer() {
		new Thread() {
			public void run() {
				if (startServerThread != null) {
					startServerThread.interrupt();
					startServerThread = null;
				}
				if (readThread != null) {
					readThread.interrupt();
					readThread = null;
				}
				try {
					if (socket != null) {
						socket.close();
						socket = null;
					}
					if (serverSocket != null) {
						serverSocket.close();/* 关闭服务器 */
						serverSocket = null;
					}
				} catch (IOException e) {
					Log.e("server", "mserverSocket.close()", e);
				}
			}
		}.start();
	}

	/* 停止客户端连接 */
	private void shutdownClient() {
		new Thread() {
			public void run() {
				if (clientConnectThread != null) {
					clientConnectThread.interrupt();
					clientConnectThread = null;
				}
				if (readThread != null) {
					readThread.interrupt();
					readThread = null;
				}
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					socket = null;
				}
			}
		}.start();
	}

	//发送数据
	private void sendMessageHandle(String msg) {
		if (socket == null) {
			Toast.makeText(getActivity(), "没有连接", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			OutputStream os = socket.getOutputStream();
			os.write(msg.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		list.add(new DeviceListItem(msg, false));
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(list.size() - 1);
	}

	//读取数据
	private class readThread extends Thread {
		public void run() {

			byte[] buffer = new byte[1024];
			int bytes;
			InputStream mmInStream = null;

			try {
				mmInStream = socket.getInputStream();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			while (true) {
				try {
					// Read from the InputStream
					if ((bytes = mmInStream.read(buffer)) > 0) {
						byte[] buf_data = new byte[bytes];
						for (int i = 0; i < bytes; i++) {
							buf_data[i] = buffer[i];
						}
						String s = new String(buf_data);
						Message msg = new Message();
						msg.obj = s;
						msg.what = 1;
						LinkDetectedHandler.sendMessage(msg);
					}
				} catch (IOException e) {
					try {
						mmInStream.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				}
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (MainActivity.serviceOrClient == MainActivity.ServerOrClient.CLIENT) {
			shutdownClient();
		} else if (MainActivity.serviceOrClient == MainActivity.ServerOrClient.SERVICE) {
			shutdownServer();
		}
		MainActivity.isOpen = false;
		MainActivity.serviceOrClient = MainActivity.ServerOrClient.NONE;
	}
}
