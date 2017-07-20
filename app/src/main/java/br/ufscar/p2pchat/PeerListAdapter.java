package br.ufscar.p2pchat;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adamski on 18/07/2017.
 */

class PeerViewHolder {

    final TextView tvName;
    final TextView tvIp;

    public PeerViewHolder(View view) {
        tvName = (TextView) view.findViewById(R.id.name);
        tvIp = (TextView) view.findViewById(R.id.ip);
    }

}

public class PeerListAdapter extends BaseAdapter {

    private final Context mContext;
    private List<WifiP2pDevice> mData = new ArrayList<WifiP2pDevice>();
    private LayoutInflater mInflater;

    public PeerListAdapter(Context context, ArrayList<WifiP2pDevice> peers) {
        super();
        mData = peers;
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public WifiP2pDevice getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position,
                        View convertView, ViewGroup parent) {

        View view;
        PeerViewHolder holder;

        if( convertView == null) {
            view = mInflater.inflate(R.layout.contact_row, parent, false);
            holder = new PeerViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (PeerViewHolder) view.getTag();
        }
        WifiP2pDevice wifiP2pDevice = (WifiP2pDevice) getItem(position);
        holder.tvName.setText(wifiP2pDevice.deviceName);
        holder.tvIp.setText(wifiP2pDevice.deviceAddress);

        return view;
    }
}