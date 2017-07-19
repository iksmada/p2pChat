package br.ufscar.p2pchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by adamski on 18/07/2017.
 */

class Message{
    private String content;
    private String IP;

    Message(String content,String IP){
        this.content = content;
        this.IP = IP;
    }

    public String getContent() {
        return content;
    }

    public String getIP() {
        return IP;
    }
}

class MessageViewHolder {

    final TextView tvContent;
    final TextView tvIp;

    public MessageViewHolder(View view) {
        tvContent = (TextView) view.findViewById(R.id.name);
        tvIp = (TextView) view.findViewById(R.id.ip);
    }

}

public class MessageListAdapter extends BaseAdapter {

    private final Context mContext;
    private ArrayList<Message> mData = new ArrayList<Message>();
    private LayoutInflater mInflater;

    public MessageListAdapter(Context context, ArrayList<Message> messages) {
        super();
        mData = messages;
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final Message item) {
        mData.add(item);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Message getItem(int position) {
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
        MessageViewHolder holder;

        if( convertView == null) {
            view = mInflater.inflate(R.layout.contact_row, parent, false);
            holder = new MessageViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (MessageViewHolder) view.getTag();
        }
        Message message = (Message) getItem(position);
        holder.tvContent.setText(message.getContent());
        holder.tvIp.setText(message.getIP());

        return view;
    }
}
