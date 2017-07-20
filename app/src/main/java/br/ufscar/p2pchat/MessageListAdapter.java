package br.ufscar.p2pchat;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static br.ufscar.p2pchat.db.P2pChatContract.FeedEntry.MESSAGE_CONTENT_COLUMN;
import static br.ufscar.p2pchat.db.P2pChatContract.FeedEntry.MESSAGE_IP_COLUMN;

public class MessageListAdapter extends CursorAdapter {

    private final Context mContext;
    private LayoutInflater mInflater;

    public MessageListAdapter(Context context,Cursor cursor) {
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
        TextView tvContent = (TextView) view.findViewById(R.id.name);
        TextView tvIp = (TextView) view.findViewById(R.id.ip);
        // Extract properties from cursor
        String content = cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_CONTENT_COLUMN));
        String ip = cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_IP_COLUMN));
        // Populate fields with extracted properties
        tvContent.setText(content);
        tvIp.setText(ip);
    }
}
