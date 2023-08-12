package com.npcreation.internshipapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    
    SharedPreferences sp;

    Button logout,updateProfile;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    EditText name,email,contact,dob;

    RadioButton male,female;
    RadioGroup gender;

    Spinner city;
    ArrayList<String> arrayList;
    Calendar calendar;

    String sCity;
    String sGender;

    SQLiteDatabase db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);
        
        /*name = findViewById(R.id.home_name);
        name.setText(sp.getString(ConstantSp.ID,"")+"\n"+
                sp.getString(ConstantSp.NAME,"")+"\n"+
                sp.getString(ConstantSp.EMAIL,"")+"\n"+
                sp.getString(ConstantSp.CONTACT,"")+"\n"+
                sp.getString(ConstantSp.PASSWORD,"")+"\n"+
                sp.getString(ConstantSp.GENDER,"")+"\n"+
                sp.getString(ConstantSp.CITY,"")+"\n"+
                sp.getString(ConstantSp.DOB,""));*/

        db = openOrCreateDatabase("InternshipApp",MODE_PRIVATE,null);
        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(USERID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(100),EMAIL VARCHAR(100),CONTACT INT(10),PASSWORD VARCHAR(20),GENDER VARCHAR(6),CITY VARCHAR(50),DOB VARCHAR(10))";
        db.execSQL(tableQuery);

        name = findViewById(R.id.home_name);
        email = findViewById(R.id.home_email);
        contact = findViewById(R.id.home_contact);

        dob = findViewById(R.id.home_dob);

        calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateClick = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR,i);
                calendar.set(Calendar.MONTH,i1);
                calendar.set(Calendar.DAY_OF_MONTH,i2);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                dob.setText(sdf.format(calendar.getTime()));

            }
        };

        dob.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(HomeActivity.this,dateClick,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

                //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                datePickerDialog.show();
                return true;
            }
        });

        /*dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/




        city = findViewById(R.id.home_city);

        arrayList = new ArrayList<>();

        arrayList.add("Select City");
        arrayList.add("Gandhinagar");
        arrayList.add("Rajkot");
        arrayList.add("Ahmedabad");
        arrayList.add("Demo");
        arrayList.add("XYZ");
        arrayList.add("Surat");

        arrayList.remove(3);
        arrayList.set(3,"Vadodara");


        ArrayAdapter adapter = new ArrayAdapter(HomeActivity.this,android.R.layout.simple_list_item_1,arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);
        city.setAdapter(adapter);

        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0){
                    sCity = "";
                }
                else {
                    sCity =  arrayList.get(i);
                    new CommonMethod(HomeActivity.this,sCity);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





        gender = findViewById(R.id.home_gender);
        male = findViewById(R.id.home_male);
        female = findViewById(R.id.home_female);

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i);
                sGender = radioButton.getText().toString();
                new CommonMethod(HomeActivity.this,sGender);
            }
        });




        updateProfile = findViewById(R.id.home_update_profile);

        logout = findViewById(R.id.home_logout);

        /*logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });*/


        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (name.getText().toString().trim().equals("")){
                    name.setError("Name Required");
                }
                else if (email.getText().toString().trim().equals("")){
                    email.setError("Email Id Required");
                }
                else if (!email.getText().toString().trim().matches(emailPattern)) {
                    email.setError("Valid Email Id Required");
                }
                else if (contact.getText().toString().trim().equals("")) {
                    contact.setError("Contact No. Required");
                }
                else if (contact.getText().toString().trim().length()<10) {
                    contact.setError("Valid Contact No. Required");
                }
                else if (gender.getCheckedRadioButtonId() == -1) {
                    new CommonMethod(HomeActivity.this,"Please Select Gender");
                }
                else if (sCity.equals("")) {
                    new CommonMethod(HomeActivity.this,"Please Select City");
                }
                else if (dob.getText().toString().trim().equals("")) {
                    dob.setError("Please Select DOB");
                }
                else {

                    String selectQuery = "SELECT * FROM USERS WHERE EMAIL='"+email.getText().toString()+"' OR CONTACT='"+contact.getText().toString()+"'";
                    Cursor cursor = db.rawQuery(selectQuery,null);
                    if (cursor.getCount()>0){
                        new CommonMethod(HomeActivity.this,"Email Id/Contact No. Already Registered");
                    } 
                    else {
                   /* String insertQuery = "INSERT INTO USERS VALUES(NULL,'"+name.getText().toString()+"','"+email.getText().toString()+"','"+contact.getText().toString()+"','"+sGender+"','"+sCity+"','"+dob.getText().toString()+"')";
                    db.execSQL(insertQuery);
                    new CommonMethod(HomeActivity.this,"Signup Successfully");
                    new CommonMethod(view,"Signup Successfully");
                    onBackPressed(); */
                    }
                }
            }
        });

        setData();
        
    }

    private void setData() {
        name.setText(sp.getString(ConstantSp.NAME,""));
        email.setText(sp.getString(ConstantSp.EMAIL,""));
        contact.setText(sp.getString(ConstantSp.CONTACT,""));
        dob.setText(sp.getString(ConstantSp.DOB,""));

        //male.setChecked(true);
        sGender = sp.getString(ConstantSp.GENDER,"");
        if (sGender.equalsIgnoreCase("Male")){
            male.setChecked(true);
        }
        else if (sGender.equalsIgnoreCase("Female")){
            female.setChecked(true);
        }
        else{

        }

        //city.setSelection(2);
        sCity = sp.getString(ConstantSp.CITY,"");
        int iCityPosition = 0;
        for (int i=0;i<arrayList.size();i++){
            if (sCity.equalsIgnoreCase(arrayList.get(i))){
                iCityPosition = i;
            }
        }
        city.setSelection(iCityPosition);
    }
}