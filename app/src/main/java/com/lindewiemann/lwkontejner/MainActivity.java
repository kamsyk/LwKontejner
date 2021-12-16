package com.lindewiemann.lwkontejner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import android.text.Editable;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String m_strSelCode = null;
    String m_strSelText = null;
    Editable m_strUserCode = null;

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

        EditText txtUserCode = (EditText) findViewById(R.id.txtUserCode);

        if(strTag == m_strSelCode) {
            m_strSelCode = null;
            m_strSelText = null;
            m_strUserCode = null;
            txtUserCode.setText("");
        } else {

            m_strSelCode = strTag;
            m_strSelText = btn.getText().toString();
            m_strUserCode = txtUserCode.getText();
        }

        updateButtonsColor();

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

        if(m_strUserCode == null || m_strUserCode.length() == 0) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Upozornění");
            builder1.setMessage("Zadejte Kód uživatele");
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
            m_strUserCode = null;
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
        values.put(LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_USER_CODE, m_strUserCode.toString());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(LwKontejnerDbDict.OdpadEntry.TABLE_NAME, null, values);

        return newRowId;
    }

    private void updateButtonsColor() {
        Boolean isButtonSelected = false;
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        EditText txtUserCode = (EditText) findViewById(R.id.txtUserCode);
        LinearLayout llBottom = (LinearLayout) findViewById(R.id.llBottom);
        List<Button> btns = find(root, Button.class);
        for(int i=0; i<btns.size(); i++) {
            if(btns.get(i).getTag() == null) {
                continue;
            }
            String strTagBtn = btns.get(i).getTag().toString();
            if(strTagBtn == m_strSelCode) {
                btns.get(i).setBackgroundColor(Color.parseColor("#008b00"));
                txtUserCode.setVisibility(View.VISIBLE);

                ViewGroup.LayoutParams params = llBottom.getLayoutParams();
                params.height = 220;
                llBottom.setLayoutParams(params);

                isButtonSelected = true;
            } else {
                btns.get(i).setBackgroundColor(Color.parseColor("#6200ee"));
            }
        }

        if(isButtonSelected) {
            txtUserCode.requestFocus();
            showKeyboard(this);
        } else {
            hideUserCode();
        }
    }

    private void hideUserCode() {
        EditText txtUserCode = (EditText) findViewById(R.id.txtUserCode);
        LinearLayout llBottom = (LinearLayout) findViewById(R.id.llBottom);
        txtUserCode.setText("");
        m_strUserCode = null;
        txtUserCode.setVisibility(View.GONE);

        ViewGroup.LayoutParams params = llBottom.getLayoutParams();
        params.height = 130;
        llBottom.setLayoutParams(params);


        hideKeyboard(this);
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

    public void hideKeyboard(Activity activity) {
        /*InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);*/

        EditText txtUserCode = (EditText) findViewById(R.id.txtUserCode);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtUserCode.getWindowToken(), 0);
    }

    public void showKeyboard(Activity activity) {

        EditText txtUserCode = (EditText) findViewById(R.id.txtUserCode);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(txtUserCode, InputMethodManager.SHOW_IMPLICIT);
    }
}


