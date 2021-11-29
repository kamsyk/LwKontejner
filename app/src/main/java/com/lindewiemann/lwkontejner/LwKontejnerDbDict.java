package com.lindewiemann.lwkontejner;

import android.provider.BaseColumns;

public final class LwKontejnerDbDict {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private LwKontejnerDbDict() {}

    /* Inner class that defines the table contents */
    public static class OdpadEntry implements BaseColumns {
        public static final String TABLE_NAME = "odpad";
        public static final String COLUMN_NAME_DATUM = "datum";
        public static final String COLUMN_NAME_PROJEKT = "projekt";
        public static final String COLUMN_NAME_CHYBA_NAME = "chyba_name";
        public static final String COLUMN_NAME_CHYBA_ID = "chyba_id";
        public static final String COLUMN_NAME_KS = "ks";
    }
}
