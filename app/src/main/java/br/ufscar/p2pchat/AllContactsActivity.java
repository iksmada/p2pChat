package br.ufscar.p2pchat;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import br.ufscar.p2pchat.db.P2pChatDbHelper;

public class AllContactsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_contacts);

        P2pChatDbHelper handler = new P2pChatDbHelper(this);

// Get access to the underlying writeable database ( ja ta feito dentro do DbHelper)

//SQLiteDatabase db = handler.getWritableDatabase();`

// Query for items from the database and get a cursor back

        Cursor contactsCursor = handler.getAllContactsCursor();

// Find ListView to populate

        ListView lvItems = (ListView) findViewById(R.id.lv_contacts);

// Setup cursor adapter using cursor from last step

        ContactListAdapter contactAdapter = new ContactListAdapter(this, contactsCursor);

// Attach cursor adapter to the ListView

        lvItems.setAdapter(contactAdapter);
    }
}
