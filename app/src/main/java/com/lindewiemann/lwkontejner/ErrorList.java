package com.lindewiemann.lwkontejner;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.view.View;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.lindewiemann.lwkontejner.adapters.OdpadItemAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ErrorList extends AppCompatActivity {
    ContainerDbHelper dbHelper = new ContainerDbHelper(this);
    private LinearProgressIndicator progressBar = null;
    File folder = null;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        setContentView(R.layout.activity_error_list);

        getOdpadList();
    }

    public void addClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void getOdpadList() {
        Cursor cursor = getOdpadCursor();

        ListView lvItems = findViewById(R.id.lvOdpad);
        // Setup cursor adapter using cursor from last step
        OdpadItemAdapter todoAdapter = new OdpadItemAdapter(this, cursor);
        // Attach cursor adapter to the ListView
        lvItems.setAdapter(todoAdapter);
        todoAdapter.changeCursor(cursor);
    }



    private Cursor getOdpadCursor() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

      return db.query(
                LwKontejnerDbDict.OdpadEntry.TABLE_NAME,    // The table to query
                null,                               // The array of columns to return (pass null to get all)
                null,                               // The columns for the WHERE clause
                null,                           // The values for the WHERE clause
                null,                               // don't group the rows
                null,                               // don't filter by row groups
                null );                               // The sort order

    }


    public void exportToFile(View v) {
        try {
            progressBar = findViewById(R.id.pgbExport);
            if (isExportFolderExist(v.getContext())) {
                progressBar.setVisibility(View.VISIBLE);
                new ExportAsyncTask().execute();
            }
        } catch(Exception ex) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setTitle("Do??lo k chyb??");
            builder1.setMessage("Export dat selhal");
            builder1.setCancelable(true);
            builder1.setNeutralButton("Zav????t",
                    (DialogInterface dialog, int id) ->
                        dialog.cancel()
                    );

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    private boolean isExportFolderExist(Context context) {
        //folder = new File(Environment.getExternalStorageDirectory() + "/LwKontejnerExport");

        /*PackageManager m = getPackageManager();
        String strPackName = getPackageName();
        PackageInfo pInfo = m.getPackageInfo(strPackName, 0);
        folder = new File(pInfo.applicationInfo.dataDir);*/

        folder = context.getExternalFilesDir(null);

        boolean isFolderExist = true;
        if (!folder.exists()) {
            //Toast.makeText(MainActivity.this, "Directory Does Not Exist, Create It", Toast.LENGTH_SHORT).show();
            isFolderExist = folder.mkdir();
        }
        if (!isFolderExist) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Do??lo k chyb??");
            builder1.setMessage("Zkontrolujte zda m?? aplikace opr??vn??n?? pro z??pis do ??lo??i??t??");
            builder1.setCancelable(true);
            builder1.setNeutralButton("Zav????t",
                    (DialogInterface dialog, int id) ->
                        dialog.cancel()
                    );

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return false;
        }

        return true;
    }


    class ExportAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        String fileName = null;

        public ExportAsyncTask() {
            super();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                exportThread();
                return true;
            } catch (IOException | InterruptedException e) {
                return false;
            }

        }
        @Override
        protected void onPostExecute(Boolean result) {
            progressBar.setVisibility(View.GONE);
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            if(result) {

                builder1.setTitle("Export byl dokon??en");
                builder1.setMessage("Data byla ulo??ena do souboru " + folder.getAbsolutePath() + "/" + fileName);
                builder1.setCancelable(true);
                builder1.setNeutralButton("Zav????t",
                        (DialogInterface dialog, int id) ->
                            dialog.cancel()
                        );

                AlertDialog alert11 = builder1.create();
                alert11.show();
            } else {

                builder1.setTitle("Do??lo k chyb??");
                builder1.setMessage("Export dat selhal");
                builder1.setCancelable(true);
                builder1.setNeutralButton("Zav????t",
                        (DialogInterface dialog, int id) ->
                            dialog.cancel()
                        );

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        }
        @Override
        protected void onPreExecute() {
            progressBar.setProgress(0);
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            //Log.d("Progress", String.valueOf(values[0]));
            progressBar.setProgress(values[0]);
        }

        private void exportThread() throws IOException, InterruptedException {

            try {
                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmsss", Locale.US);
                String strDate = dateFormat.format(date);

                fileName = "Lwe" + strDate + ".csv";
                File exportFile = new File(folder, fileName);
                exportFile.createNewFile();

                FileOutputStream fOut = new FileOutputStream(exportFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut, StandardCharsets.UTF_8);

                myOutWriter.write("\ufeff"); // Add BOM to UTF-8 necessary for displaying Czech chars in excel in windows

                String strHeader = "Datum,"
                        + "Projekt,"
                        + "K??d u??ivatele,"
                        + "K??d chyby,"
                        + "Popis chyby,"
                        + "Po??et kus??"
                        + System.lineSeparator();
                myOutWriter.append(strHeader);

                Cursor cursor = getOdpadCursor();
                int iRowIndex = 0;

                while (cursor.moveToNext()) {
                    String strLine = getExportLine(cursor);
                    myOutWriter.append(strLine);
                    publishProgress(iRowIndex);
                    iRowIndex++;

                }
                myOutWriter.close();
                fOut.close();
            } catch(Exception ex) {
                throw ex;
            }
        }

        private String getExportLine(Cursor cursor) {
            String strDateTime = cursor.getString(cursor.getColumnIndexOrThrow(LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_DATUM));
            String strProject = cursor.getString(cursor.getColumnIndexOrThrow(LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_PROJEKT));
            String strChyba = cursor.getString(cursor.getColumnIndexOrThrow(LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_CHYBA_NAME));
            String strUserCode = cursor.getString(cursor.getColumnIndexOrThrow(LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_USER_CODE));
            int iChyba = cursor.getInt(cursor.getColumnIndexOrThrow(LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_CHYBA_ID));
            int iKs = cursor.getInt(cursor.getColumnIndexOrThrow(LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_KS));

            String strKs = String.valueOf(iKs);
            String strKodChyby = String.valueOf(iChyba);

            String exportLine = strDateTime + ","
                    + strProject + ","
                    + strUserCode + ","
                    + strKodChyby + ","
                    + strChyba + ","
                    + strKs;
            exportLine += System.lineSeparator();

            return exportLine;
        }
    }

}