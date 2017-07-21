package br.ufscar.p2pchat;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import br.ufscar.p2pchat.db.P2pChatDbHelper;

public class MessagesReceivedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_received);
        // TodoDatabaseHandler is a SQLiteOpenHelper class connecting to SQLite

        P2pChatDbHelper handler = new P2pChatDbHelper(this);

// Get access to the underlying writeable database ( ja ta feito dentro do DbHelper)

//SQLiteDatabase db = handler.getWritableDatabase();`

// Query for items from the database and get a cursor back

        Cursor messageCursor = handler.getAllMessagesCursor();

// Find ListView to populate

        ListView lvItems = (ListView) findViewById(R.id.lvMessagesReceived);

// Setup cursor adapter using cursor from last step

        MessageListAdapter messageAdapter = new MessageListAdapter(this, messageCursor);

// Attach cursor adapter to the ListView

        lvItems.setAdapter(messageAdapter);
    }
}
