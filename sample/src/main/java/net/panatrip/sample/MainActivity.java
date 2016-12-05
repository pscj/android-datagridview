package net.panatrip.sample;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.panatrip.datagridview.DataGridView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DataGridView dataGridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataGridView = (DataGridView)findViewById(R.id.datagridview);

        loadData();
    }
    private void loadData(){
        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
        Cursor cursor = db.query("nationlist", null, null, null, null, null, null);

        List<List<String>> result = null;
        if(cursor != null && cursor.moveToFirst()){
            result = getTableRows(cursor);
            cursor.close();
        }
        db.close();
        dataGridView.setData(result, true);
    }

    private List<List<String>> getTableRows(Cursor cursor) {

        List<List<String>> rows = new ArrayList<>();
        List<String> header = new ArrayList<>();

        for (int col = 0; col < cursor.getColumnCount(); col++) {
            header.add(cursor.getColumnName(col));
        }
        rows.add(header);

        if (cursor.getCount() == 0) {
            return rows;
        }

        do {
            List<String> row = new ArrayList<>();
            for (int col = 0; col < cursor.getColumnCount(); col++) {
                String strData = cursor.getString(col);;
                row.add(strData);
            }
            rows.add(row);
        } while (cursor.moveToNext());
        return rows;
    }
}
