package br.ufscar.p2pchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

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
    }

    public void startMessagesActivity(View v){
        //clicaram em mensagens, devo abrir a activity de mensagens
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
}
