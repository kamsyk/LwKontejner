package com.lindewiemann.lwkontejner.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import com.lindewiemann.lwkontejner.LwKontejnerDbDict;
import com.lindewiemann.lwkontejner.R;

public class OdpadItemAdapter extends CursorAdapter {

    public OdpadItemAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }


    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.odpad_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvDatum = (TextView) view.findViewById(R.id.tvDatum);
        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
        TextView tvProjekt = (TextView) view.findViewById(R.id.tvProjekt);
        TextView tvChyba = (TextView) view.findViewById(R.id.tvChyba);
        TextView tvKs = (TextView) view.findViewById(R.id.tvKs);

        // Extract properties from cursor
        String strDateTime = cursor.getString(cursor.getColumnIndexOrThrow(LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_DATUM));
        String strProject = cursor.getString(cursor.getColumnIndexOrThrow(LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_PROJEKT));
        String strChyba = cursor.getString(cursor.getColumnIndexOrThrow(LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_CHYBA_NAME));
        int iChyba = cursor.getInt(cursor.getColumnIndexOrThrow(LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_CHYBA_ID));
        int iKs = cursor.getInt(cursor.getColumnIndexOrThrow(LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_KS));

        String strKs = String.valueOf(iKs) + " ks";
        String[] strDateItems = strDateTime.split(" ");
        String strDate = strDateItems[0];
        String strTime = strDateItems[1];
        strChyba += " (Kód chyby: " + String.valueOf(iChyba) + ")";

        // Populate fields with extracted properties
        tvDatum.setText(strDate);
        tvTime.setText(strTime);
        tvProjekt.setText(strProject);
        tvChyba.setText(strChyba);
        tvKs.setText(strKs);
    }
}