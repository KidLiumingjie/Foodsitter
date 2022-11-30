package com.example.loginregister.home;

import static androidx.fragment.app.FragmentManager.TAG;

import static com.example.loginregister.home.HomeActivity.GetHomeFood;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.loginregister.R;
import com.example.loginregister.datasets.FoodInfo;
import com.example.loginregister.insert_food_DB;

import java.util.Calendar;
import java.util.List;


public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private static SQLiteDatabase dbread;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        //textdate=rootView.findViewById(R.id.TextDate);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }


    @SuppressLint("RestrictedApi")
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        month+=1;
        Log.d(TAG,"onDataSet : yyyy/mm/dd: " + year + "/"+month+"/"+day );
        String datestring = year + "-"+month+"-"+day;
        TextView textdate = getActivity().findViewById(R.id.TextDate);
        textdate.setText(datestring);
        dbread = new insert_food_DB(getActivity(), "editFoodDB", null, 6).getWritableDatabase();

        Cursor cursor = dbread.rawQuery("select * from myFoodTable", null);

        TextView fat = getActivity().findViewById(R.id.fatnum);
        TextView protein = getActivity().findViewById(R.id.pornum);
        TextView carb = getActivity().findViewById(R.id.carbnum);
        TextView kal = getActivity().findViewById(R.id.kcal);
        int total_fat=0;
        int total_pro=0;
        int total_carb=0;
        int total_kal=0;

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                total_kal+=cursor.getInt(1);
                total_fat+=cursor.getInt(3);
                total_pro+=cursor.getInt(2);
                total_carb+=cursor.getInt(4);
            }
            cursor.close();
        }
        String f=Integer.toString(total_fat);
        fat.setText(f);
        String p=Integer.toString(total_pro);
        protein.setText(p);
        String c=Integer.toString(total_carb);
        carb.setText(c);
        String k=Integer.toString(total_kal);
        kal.setText(k);

        dbread.close();
    }

}
