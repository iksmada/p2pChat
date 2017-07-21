package br.ufscar.p2pchat.db;

import android.provider.BaseColumns;

/**
 * Created by rapha on 18/07/2017.
 */

public class P2pChatContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private P2pChatContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String CONTACT_TABLE = "contacts";
        public static final String CONTACT_NAME_COLUMN = "name";
        public static final String CONTACT_IP_COLUMN = "ip";

        public static final String MESSAGE_TABLE = "messages";
        public static final String MESSAGE_CONTENT_COLUMN = "content";
        public static final String MESSAGE_IP_COLUMN = "ip";
    }
}
