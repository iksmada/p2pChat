package br.ufscar.p2pchat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import br.ufscar.p2pchat.db.P2pChatDbHelper;
import br.ufscar.p2pchat.objects.Contact;

public class WriteMessageActivity extends Activity {

    private EditText editText;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private String TAG = WriteMessageActivity.class.getSimpleName();
    private WiFiConnectionBroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_message);
        mReceiver = new WiFiConnectionBroadcastReceiver();

        editText = (EditText) findViewById(R.id.write_message);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
    }

    public void SendMessage(View v){
        P2pChatDbHelper handler = new P2pChatDbHelper(this);
        ArrayList<Contact> contacts = handler.getAllContacts();

        for(Contact contact: contacts) {
            final String name = contact.getName();

            final WifiP2pConfig config = new WifiP2pConfig();
            config.groupOwnerIntent = 15;
            config.deviceAddress = contact.getIP();
            config.wps.setup = WpsInfo.PBC;
            mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo info) {
                    if (info.groupFormed) {
                        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "RemoveGroup successful");
                            }

                            @Override
                            public void onFailure(int reason) {

                                Log.d(TAG, "RemoveGroup fail");

                            }
                        });
                    }
                }
            });

            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    // WiFiP2pBroadcastReceiver will notify us. Ignore for now.
                    Log.d(TAG, "connect successful");
                    mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                        @Override
                        public void onConnectionInfoAvailable(final WifiP2pInfo info) {

                            // InetAddress from WifiP2pInfo struct.
                            InetAddress groupOwnerAddress = info.groupOwnerAddress;
                            new SendMessageAsyncTask().execute(groupOwnerAddress);
                        }});

                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(WriteMessageActivity.this, "Connect failed. Retry.",
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG,  "Connect failed. Retry.");
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mReceiver = new WiFiConnectionBroadcastReceiver();
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private class SendMessageAsyncTask extends AsyncTask<InetAddress,Void,Boolean> {
        InetAddress host;
        String message = "default message";
        int port = 8888;
        Boolean isSucessfull = false;
        Socket socket = new Socket();

        @Override
        protected Boolean doInBackground(InetAddress... params) {
            Log.d(TAG,"SendMessageAsyncTask");
            try {
                host = params[0];
                message = "Teste";
                /**
                 * Create a client socket with the host,
                 * port, and timeout information.
                 */
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), 5000);

                /**
                 * Create a byte stream from a JPEG file and pipe it to the output stream
                 * of the socket. This data will be retrieved by the server device.
                 */
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(message.getBytes());
                outputStream.close();
                isSucessfull = true;
            } catch (FileNotFoundException e) {
                //catch logic
            } catch (IOException e) {
                //catch logic
            }

/**
 * Clean up any open sockets when done
 * transferring or if an exception occurred.
 */
            finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            //catch logic
                        }
                    }
                }
            }
            return isSucessfull;
        }

        /**
         * Start activity that can handle the JPEG image
         */
        @Override
        protected void onPostExecute(Boolean success) {
            if (success){
                Toast.makeText(WriteMessageActivity.this, "Successfull sent: "+message+" to " + host,Toast.LENGTH_LONG).show();
                Log.d(TAG, "Successfull sent: "+message+" to " + host);
            } else {
                Toast.makeText(WriteMessageActivity.this,"Fail to send Message to "+host, Toast.LENGTH_LONG).show();
                Log.d(TAG, "Fail to send Message to "+host);
            }
        }
    }

    public class WiFiConnectionBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)||
                    WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                if (mManager == null) {
                    return;
                }

                NetworkInfo networkInfo = (NetworkInfo) intent
                        .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                if (networkInfo!= null && networkInfo.isConnected()) {

                    // We are connected with the other device, request connection
                    // info to find group owner IP

                    mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                        @Override
                        public void onConnectionInfoAvailable(final WifiP2pInfo info) {

                            // InetAddress from WifiP2pInfo struct.
                            InetAddress groupOwnerAddress = info.groupOwnerAddress;

                            // After the group negotiation, we can determine the group owner
                            // (server).
                            if (info.groupFormed && info.isGroupOwner) {
                                Log.d(TAG,"im owner, but im sending");
                                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                                    @Override
                                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                                        if (info.groupFormed) {
                                            mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                                                @Override
                                                public void onSuccess() {
                                                    Log.d(TAG, "RemoveGroup successful");
                                                }

                                                @Override
                                                public void onFailure(int reason) {

                                                    Log.d(TAG, "RemoveGroup fail");

                                                }
                                            });
                                        }
                                    }
                                });
                                // Do whatever tasks are specific to the group owner.
                                // One common case is creating a group owner thread and accepting
                                // incoming connections.
                            } else if (info.groupFormed) {
                                Log.d(TAG,"im not owner and sending, that fine");
                                final String message = editText.getText().toString();
                                new SendMessageAsyncTask().execute(groupOwnerAddress);
                                // The other device acts as the peer (client). In this case,
                                // you'll want to create a peer thread that connects
                                // to the group owner.
                            }
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
