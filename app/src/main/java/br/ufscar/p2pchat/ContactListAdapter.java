package br.ufscar.p2pchat;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static br.ufscar.p2pchat.db.P2pChatContract.FeedEntry.CONTACT_IP_COLUMN;
import static br.ufscar.p2pchat.db.P2pChatContract.FeedEntry.CONTACT_NAME_COLUMN;


public class ContactListAdapter extends CursorAdapter {

    private final Context mContext;
    private LayoutInflater mInflater;

    public ContactListAdapter(Context context,Cursor cursor) {
        super(context,cursor,true);
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.contact_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvName = (TextView) view.findViewById(R.id.name);
        TextView tvIp = (TextView) view.findViewById(R.id.ip);
        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_NAME_COLUMN));
        String ip = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_IP_COLUMN));
        // Populate fields with extracted properties
        tvName.setText(name);
        tvIp.setText(ip);
    }
}
