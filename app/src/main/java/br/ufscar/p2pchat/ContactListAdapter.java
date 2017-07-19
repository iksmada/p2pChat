package br.ufscar.p2pchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufscar.p2pchat.objects.Contact;

class ContactViewHolder {

    final TextView tvName;
    final TextView tvIp;

    public ContactViewHolder(View view) {
        tvName = (TextView) view.findViewById(R.id.name);
        tvIp = (TextView) view.findViewById(R.id.ip);
    }

}

public class ContactListAdapter extends BaseAdapter {

    private final Context mContext;
    private ArrayList<Contact> mData = new ArrayList<Contact>();
    private LayoutInflater mInflater;

    public ContactListAdapter(Context context, ArrayList<Contact> contacts) {
        super();
        mData = contacts;
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final Contact item) {
        mData.add(item);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Contact getItem(int position) {
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
        ContactViewHolder holder;

        if( convertView == null) {
            view = mInflater.inflate(R.layout.contact_row, parent, false);
            holder = new ContactViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ContactViewHolder) view.getTag();
        }
        Contact contact = (Contact) getItem(position);
        holder.tvName.setText(contact.getName());
        holder.tvIp.setText(contact.getIP());

        return view;
    }
}
