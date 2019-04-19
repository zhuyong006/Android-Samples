package com.dzt.btcommunication;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.dzt.btcommunication.adapter.FragmentAdapter;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
	enum ServerOrClient {
		NONE,
		SERVICE,
		CLIENT
	}

	private static final String TAG = "MainActivity";
	private DrawerLayout dlContent;
	private TabLayout tabLayout2;
	private Toolbar toolbar;
	private ViewPager viewPager;
	static String BlueToothAddress = "null";
	static ServerOrClient serviceOrClient = ServerOrClient.NONE;
	static boolean isOpen = false;
	private DeviceFragment deviceFragment;
	private ChatFragment chatFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initWidgets();
	}

	private void initWidgets() {
		tabLayout2 = (TabLayout) findViewById(R.id.t2);
		tabLayout2.addOnTabSelectedListener(this);

		toolbar = (Toolbar) findViewById(R.id.tb);
		toolbar.setTitle(R.string.app_name);
		//toolbar.setLogo(R.mipmap.ic_launcher);
		setSupportActionBar(toolbar);
		//toolbar.setNavigationIcon(R.drawable.selector_menu);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				drawerLayoutSwitch(true);
			}
		});


		viewPager = (ViewPager) findViewById(R.id.viewpager);
		FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
		deviceFragment = new DeviceFragment();
		chatFragment = new ChatFragment();
        adapter.addFragment(chatFragment, "对话列表");
		adapter.addFragment(deviceFragment, "设备列表");
		viewPager.setAdapter(adapter);
		tabLayout2.setupWithViewPager(viewPager);

		dlContent = (DrawerLayout) findViewById(R.id.dl_content);
	}

	public void setCurrentTab(int index) {
		viewPager.setCurrentItem(index);
	}

	@Override
	public void onTabSelected(TabLayout.Tab tab) {
		if (tab.getText().equals("对话列表")) {
			if (isOpen) {
				Toast.makeText(this, "连接已经打开，可以通信。如果要再建立连接，请先断开！", Toast.LENGTH_SHORT).show();
				return;
			}
			if (serviceOrClient == ServerOrClient.CLIENT) {
				Log.i(TAG, "---------------------onResume CLIENT");
				String address = BlueToothAddress;
				if (!TextUtils.isEmpty(address)) {
					chatFragment.startClientThread(address);
					isOpen = true;
				} else {
					Toast.makeText(this, "address is null !", Toast.LENGTH_SHORT).show();
				}
			} else if (serviceOrClient == ServerOrClient.SERVICE) {
				chatFragment.startServerThread();
				isOpen = true;
			}
		}
	}

	@Override
	public void onTabUnselected(TabLayout.Tab tab) {

	}

	@Override
	public void onTabReselected(TabLayout.Tab tab) {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		tabLayout2.removeOnTabSelectedListener(this);
	}

	@Override
	public void onBackPressed() {
		if (dlContent.isDrawerOpen(Gravity.START)) {
			drawerLayoutSwitch(false);
		} else {
			super.onBackPressed();
		}
	}

	private void drawerLayoutSwitch(boolean isOpen) {
		if (isOpen) {
			dlContent.openDrawer(Gravity.START);
		} else {
			dlContent.closeDrawer(Gravity.START);
		}
	}
}
