package com.ihm.shifumeredahibba;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.ihm.shifumeredahibba.DeviceListFragment.DeviceActionListener;


public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

	protected static final int CHOOSE_FILE_RESULT_CODE = 20;
	private View mContentView = null;
	private WifiP2pDevice device;
	private WifiP2pInfo info;
	ProgressDialog progressDialog = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mContentView = inflater.inflate(R.layout.device_detail, null);
		mContentView.findViewById(R.id.btn_connect).setOnClickListener(v -> {
			WifiP2pConfig config = new WifiP2pConfig();
			config.deviceAddress = device.deviceAddress;
			config.wps.setup = WpsInfo.PBC;
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
					"Connecting to :" + device.deviceAddress, true, true
			);
			((DeviceActionListener) getActivity()).connect(config);


		});

		mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
				v -> ((DeviceActionListener) getActivity()).disconnect());

		mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
				v -> {
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");
					startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
				});

		return mContentView;
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(requestCode == GameActivity.DISCONNECTED){
			((DeviceActionListener) getActivity()).disconnect();
			this.getActivity().finishAffinity();
		}
	}

	@Override
	public void onConnectionInfoAvailable(final WifiP2pInfo info) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		this.info = info;
		this.getView().setVisibility(View.VISIBLE);

		TextView view = mContentView.findViewById(R.id.group_owner);
		view.setText(getResources().getString(R.string.group_owner_text)
				+ ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
				: getResources().getString(R.string.no)));

		view = mContentView.findViewById(R.id.device_info);
		view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());


			Intent intent = new Intent(DeviceDetailFragment.this.getActivity(), GameActivity.class);
			intent.putExtra("deviceInfo", info);
			startActivityForResult(intent, GameActivity.DISCONNECTED);

			Toast.makeText(DeviceDetailFragment.this.getActivity(), "GAME STARTED", Toast.LENGTH_LONG).show();

		mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);

	}

	public void showDetails(WifiP2pDevice device) {
		this.device = device;
		this.getView().setVisibility(View.VISIBLE);
		TextView view = mContentView.findViewById(R.id.device_address);
		view.setText(device.deviceAddress);
		view = mContentView.findViewById(R.id.device_info);
		view.setText(device.toString());

	}

	public void resetViews() {
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
		TextView view = mContentView.findViewById(R.id.device_address);
		view.setText(R.string.empty);
		view = mContentView.findViewById(R.id.device_info);
		view.setText(R.string.empty);
		view = mContentView.findViewById(R.id.group_owner);
		view.setText(R.string.empty);
		view = mContentView.findViewById(R.id.status_text);
		view.setText(R.string.empty);
		mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
		this.getView().setVisibility(View.GONE);
	}


}