package br.ufscar.p2pchat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import br.ufscar.p2pchat.db.P2pChatDbHelper;
import br.ufscar.p2pchat.objects.Contact;

public class WriteMessageActivity extends Activity {

    private EditText editText;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_message);

        editText = (EditText) findViewById(R.id.write_message);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
    }

    public void SendMessage(View v){
        P2pChatDbHelper handler = new P2pChatDbHelper(this);
        ArrayList<Contact> contacts = handler.getAllContacts();
        final String message = editText.getText().toString();

        for(Contact contact: contacts) {
            final String name = contact.getName();

            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = contact.getIP();
            config.wps.setup = WpsInfo.PBC;

            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    // WiFiP2pBroadcastReceiver will notify us. Ignore for now.
                    new SendMessageAsyncTask().execute(name,message);
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(WriteMessageActivity.this, "Connect failed. Retry.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class SendMessageAsyncTask extends AsyncTask<String,Void,Boolean> {

        Context context;
        String host = "default host";
        String message = "default message";
        int port = 8888;
        Boolean isSucessfull = false;
        Socket socket = new Socket();

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                host = params[0];
                message = params[1];
                /**
                 * Create a client socket with the host,
                 * port, and timeout information.
                 */
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), 500);

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
            } else {
                Toast.makeText(WriteMessageActivity.this,"Fail to send Message to "+host, Toast.LENGTH_LONG).show();
            }
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
