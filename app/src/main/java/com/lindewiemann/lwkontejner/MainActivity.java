package com.lindewiemann.lwkontejner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String m_strSelCode = null;
    String m_strSelText = null;

    ContainerDbHelper dbHelper = new ContainerDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadInit();
    }


    private void loadInit() {

        getSupportActionBar().hide();

    }

    public void chybaClick(View view) {
        Button btn = (Button)view;
        int btnId = btn.getId();
        String strTag = btn.getTag().toString();
        if(strTag == m_strSelCode) {
            m_strSelCode = null;
            m_strSelText = null;
        } else {
            m_strSelCode = strTag;
            m_strSelText = btn.getText().toString();
        }

        updateButtonsColor();

        //Toast.makeText(this, m_strSelCode, Toast.LENGTH_LONG).show();
    }

    public void saveClick(View btn){
        if(m_strSelCode == null) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Upozornění");
            builder1.setMessage("Vyberte druh chyby");
            builder1.setCancelable(true);
            builder1.setNeutralButton("Zavřít",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

            return;
        }

        try {
            long newRowId = saveToDb();
            Toast.makeText(getApplicationContext(), "Data byla uložena (id " + newRowId + ")", Toast.LENGTH_SHORT).show();
            m_strSelCode = null;
            m_strSelText = null;
            updateButtonsColor();
        } catch(Exception ex) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Chyba");
            builder1.setMessage("Při ukládání došlo k chybě");
            builder1.setCancelable(true);
            builder1.setNeutralButton("Zavřít",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    private long saveToDb() {
        int iChyba = Integer.parseInt(m_strSelCode);
        String strProject = null;
        if(iChyba < 100) {
            strProject = "NEW BULLI T7";
        } else {
            strProject = "E TRON";
        }

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String strDate = dateFormat.format(date);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_CHYBA_ID, iChyba);
        values.put(LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_CHYBA_NAME, m_strSelText);
        values.put(LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_PROJEKT, strProject);
        values.put(LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_KS, 1);
        values.put(LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_DATUM, strDate);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(LwKontejnerDbDict.OdpadEntry.TABLE_NAME, null, values);

        return newRowId;
    }

    private void updateButtonsColor() {
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        List<Button> btns = find(root, Button.class);
        for(int i=0; i<btns.size(); i++) {
            if(btns.get(i).getTag() == null) {
                continue;
            }
            String strTagBtn = btns.get(i).getTag().toString();
            if(strTagBtn == m_strSelCode) {
                btns.get(i).setBackgroundColor(Color.parseColor("#008b00"));
            } else {
                btns.get(i).setBackgroundColor(Color.parseColor("#6200ee"));
            }
        }
    }

    public static <T extends View> List<T> find(ViewGroup root, Class<T> type) {
        FinderByType<T> finderByType = new FinderByType<T>(type);
        LayoutTraverser.build(finderByType).traverse(root);
        return finderByType.getViews();
    }

    private static class FinderByType<T extends View> implements LayoutTraverser.Processor {
        private final Class<T> type;
        private final List<T> views;

        private FinderByType(Class<T> type) {
            this.type = type;
            views = new ArrayList<T>();
        }

        @Override
        @SuppressWarnings("unchecked")
        public void process(View view) {
            if (type.isInstance(view)) {
                views.add((T) view);
            }
        }

        public List<T> getViews() {
            return views;
        }
    }

    public void errorListClick(View view) {
        Intent intent = new Intent(this, ErrorList.class);
        startActivity(intent);
    }
}


