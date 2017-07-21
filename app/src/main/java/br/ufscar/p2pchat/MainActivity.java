package br.ufscar.p2pchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import br.ufscar.p2pchat.db.P2pChatDbHelper;
import br.ufscar.p2pchat.objects.Message;

public class MainActivity extends AppCompatActivity {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    private WiFiConnectionBroadcastReceiver mReceiver;
    private String TAG = MainActivity.class.getSimpleName();
    private P2pChatDbHelper dbHelper = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Intent intent = new Intent(this, WriteMessageActivity.class);
        final Intent intNewContact = new Intent(this, NewContactActivity.class);
        final Intent intMsgReceived = new Intent(this, MessagesReceivedActivity.class);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //COLCOAR SUA AÇÂO AQUI
                startActivity(intent);
            }
        });


        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fabNewContact);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                //COLCOAR SUA AÇÂO AQUI
                startActivity(intNewContact);
            }
        });

        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fabMessagesReceived);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view3) {
                //COLCOAR SUA AÇÂO AQUI
                startActivity(intMsgReceived);
            }
        });

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank.  Code for peer discovery goes in the
                // onReceive method, detailed below.
            }

            @Override
            public void onFailure(int reasonCode) {
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.
            }
        });


        IntentFilter intentFilter = new IntentFilter();
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mReceiver = new WiFiConnectionBroadcastReceiver();
        registerReceiver(mReceiver, intentFilter);

        dbHelper = new P2pChatDbHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    public class ReceiveMessegeAsyncTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                /**
                 * Create a server socket and wait for client connections. This
                 * call blocks until a connection is accepted from a client
                 */
                ServerSocket serverSocket = new ServerSocket(8888);
                Socket client = serverSocket.accept();

                /**
                 * If this code is reached, a client has connected and transferred data
                 * Save the input stream from the client as a JPEG file
                 */
                InputStream inputstream = client.getInputStream();
                serverSocket.close();
                return inputstream.toString();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return null;
            }
        }

        /**
         * Start activity that can handle the JPEG image
         */
        @Override
        protected void onPostExecute(String result) {
            dbHelper.addMessage(new Message(result,"Anonimo"));
        }
    }

    public class WiFiConnectionBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                if (mManager == null) {
                    return;
                }

                NetworkInfo networkInfo = (NetworkInfo) intent
                        .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                if (networkInfo.isConnected()) {

                    // We are connected with the other device, request connection
                    // info to find group owner IP

                    mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                        @Override
                        public void onConnectionInfoAvailable(WifiP2pInfo info) {
                            new ReceiveMessegeAsyncTask().execute();
                        }
                    });
                }

                // Connection state changed!  We should probably do something about
                // that.

            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

                //My device changed

            }
        }
    }


}
