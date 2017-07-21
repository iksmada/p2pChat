package br.ufscar.p2pchat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.ufscar.p2pchat.db.P2pChatDbHelper;
import br.ufscar.p2pchat.objects.Contact;

public class NewContactActivity extends Activity {

    private static final String TAG = NewContactActivity.class.getSimpleName();
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;
    private WiFiP2pBroadcastReceiver mReceiver;
    private Boolean isWifiP2pEnabled = false;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private PeerListAdapter mAdapter = null;
    private ListView lvItems= null;
    private Context mContext = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);
        mContext = this;
        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        final P2pChatDbHelper dbHelper = new P2pChatDbHelper(this);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        lvItems = (ListView) findViewById(R.id.listview);
// Setup cursor adapter using cursor from last step
        mAdapter = new PeerListAdapter(this, (ArrayList<WifiP2pDevice>) peers);
// Attach cursor adapter to the ListView
        lvItems.setAdapter(mAdapter);

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WifiP2pDevice device = mAdapter.getItem(position);
                dbHelper.deleteContact(device.deviceName);
                dbHelper.addContact(new Contact(device.deviceName,device.deviceAddress));

                Toast.makeText(mContext,"Added " + device.deviceName,Toast.LENGTH_LONG).show();

            }
        });

    }


    private PeerListListener peerListListener = new PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            List<WifiP2pDevice> refreshedPeers = new ArrayList<WifiP2pDevice>(peerList.getDeviceList());
            if (!refreshedPeers.equals(peers)) {
                peers.clear();
                peers.addAll(refreshedPeers);

                // If an AdapterView is backed by this data, notify it
                // of the change.  For instance, if you have a ListView of
                // available peers, trigger an update.
                mAdapter.notifyDataSetChanged();
                // Perform any other updates needed based on the new list of
                // peers connected to the Wi-Fi P2P network.
            }

            if (peers.size() == 0) {
                Log.d(TAG, "No devices found");
                return;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new WiFiP2pBroadcastReceiver();
        registerReceiver(mReceiver, intentFilter);

        if (mManager != null) {
            mManager.requestPeers(mChannel, peerListListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mReceiver);
    }


    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    public class WiFiP2pBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // Determine if Wifi P2P mode is enabled or not, alert
                // the Activity.
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    setIsWifiP2pEnabled(true);
                } else {
                    setIsWifiP2pEnabled(false);
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

                if (mManager != null) {
                    mManager.requestPeers(mChannel, peerListListener);
                }
                Log.d(TAG, "P2P peers changed");

            }
        }
    }

}
