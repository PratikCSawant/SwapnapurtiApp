package com.example.swapnapurtiapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBHandler extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SwapnapurtiDB.db";

    public static final String TABLE_LOGIN = "LoginDetails";
    public static final String TABLE_VILLAGE = "Village";
    static final String TABLE_YESNO = "YesNo";
    static final String TABLE_SEX = "Sex";
    static final String TABLE_PERSONAL_INFO_FORM = "PersonalInfoForm";
    static final String TABLE_ORAL_EXAM_FORM = "OralExamForm";
    static final String TABLE_BREAST_EXAM_FORM = "BreastExamForm";

    public DBHandler(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void CreateTablesIfNotExist()
    {
        SQLiteDatabase db ;

        db  = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+TABLE_VILLAGE+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0)
            {
                cursor.close();
            }
            else
            {

                db.execSQL("create table " + TABLE_LOGIN +
                        " (" +
                        "EmployeeCode TEXT,"+
                        "EmployeeName TEXT,"+
                        "userid TEXT,"+
                        "password TEXT,"+
                        "Active TEXT"+
                        ")"
                );

                String login_value = "('E501','Pratik Sawant','pratik','pratik','1')," +
                        "('E502','Sachin Patil','sachin','sachin','1')," +
                        "('E503','Prithviraj Kadam','prithviraj','prithviraj','1')";

                db.execSQL("insert into " + TABLE_LOGIN + " values " + login_value);


                //Villages
                db.execSQL("create table " + TABLE_VILLAGE +
                        " (" +
                        "VillageNo TEXT,"+
                        "VillageName TEXT"+
                        ")"
                );

                String village_values = "('101','Abanpalli')," +
                        "('102','Aldandi')," +
                        "('114','Zenda')," +
                        "('115','Sironcha')";

                db.execSQL("insert into " + TABLE_VILLAGE + " values " + village_values);

                db.execSQL("create table " + TABLE_YESNO +
                        " (" +
                        "Code TEXT,"+
                        "Value TEXT"+
                        ")"
                );

                String yesno_values = "('1','Yes')," +
                        "('2','No')," +
                        "('9','NA')";

                db.execSQL("insert into " + TABLE_YESNO + " values " + yesno_values);


                db.execSQL("create table " + TABLE_SEX +
                                " (" +
                                "Code TEXT,"+
                                "Value TEXT"+
                                ")"
                        );

                String sex_values = "('1','Male')," +
                        "('2','Female')," +
                        "('3','Other')";

                db.execSQL("insert into " + TABLE_SEX + " values " + sex_values);


                db.execSQL("create table " + TABLE_PERSONAL_INFO_FORM +
                        " (" +
                        "UniqueID TEXT,"+
                        "Village TEXT,"+
                        "Name TEXT,"+
                        "Sex TEXT,"+
                        "Age TEXT,"+
                        "ContactNo TEXT,"+
                        "OralSymptoms TEXT,"+
                        "BreastSymptoms TEXT,"+
                        "CervixSymptoms TEXT,"+
                        "DEOName TEXT,"+
                        "DEODate TEXT,"+
                        "SavedAtServer TEXT"+
                        ")"
                );

                db.execSQL("create table " + TABLE_ORAL_EXAM_FORM +
                        " (" +
                        "UniqueID TEXT,"+
                        "Village TEXT,"+
                        "Name TEXT,"+
                        "Sex TEXT,"+
                        "Age TEXT,"+
                        "ContactNo TEXT,"+
                        "OralHygiene TEXT,"+
                        "ScreenPositive TEXT,"+
                        "LesionType TEXT,"+
                        "DEOName TEXT,"+
                        "DEODate TEXT,"+
                        "SavedAtServer TEXT"+
                        ")"
                );

                db.execSQL("create table " + TABLE_BREAST_EXAM_FORM +
                        " (" +
                        "UniqueID TEXT,"+
                        "Village TEXT,"+
                        "Name TEXT,"+
                        "Sex TEXT,"+
                        "Age TEXT,"+
                        "ContactNo TEXT,"+
                        "BreastLump TEXT,"+
                        "NippleDischarge TEXT,"+
                        "OtherLesion TEXT,"+
                        "DEOName TEXT,"+
                        "DEODate TEXT,"+
                        "SavedAtServer TEXT"+
                        ")"
                );

            }
            cursor.close();
        }
    }

    //for testing
    public void createBreastTable()
    {
        SQLiteDatabase db ;
        db  = this.getWritableDatabase();
        db.execSQL("create table " + TABLE_BREAST_EXAM_FORM +
                " (" +
                "UniqueID TEXT,"+
                "Village TEXT,"+
                "Name TEXT,"+
                "Sex TEXT,"+
                "Age TEXT,"+
                "ContactNo TEXT,"+
                "BreastLump TEXT,"+
                "NippleDischarge TEXT,"+
                "OtherLesion TEXT,"+
                "DEOName TEXT,"+
                "DEODate TEXT,"+
                "SavedAtServer TEXT"+
                ")"
        );
    }


    public List<String> getAllSex()
    {
        List<String> list = new ArrayList<String>();

        String selectQuery = "Select Code||'-'||Value from "+TABLE_SEX;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        list.add("SELECT");

        if(cursor.moveToFirst())
        {
            do {
                list.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return list;
    }

    public List<String> getAllYesNo()
    {
        List<String> list = new ArrayList<String>();

        String selectQuery = "Select Code||'-'||Value from "+TABLE_YESNO;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        list.add("SELECT");

        if(cursor.moveToFirst())
        {
            do {
                list.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return list;
    }

    public List<String> getAllVillages()
    {
        List<String> list = new ArrayList<String>();

        String selectQuery = "Select VillageNo||'-'||VillageName from "+TABLE_VILLAGE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        list.add("SELECT");

        if(cursor.moveToFirst())
        {
            do {
                list.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return list;
    }

    public String GetNewUID()
    {
        try
        {
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            SQLiteDatabase db = this.getWritableDatabase();
            //Cursor res = db.rawQuery("select UniqueID from "+TABLE_PERSONAL_INFO_FORM+" where DEOName = '"+CommonVariables.UserId+"' and DEODate = '"+date+"' ORDER BY UniqueID DESC limit 1" ,null);
            Cursor res = db.rawQuery("select UniqueID from "+TABLE_PERSONAL_INFO_FORM+" where DEOName = '"+CommonVariables.UserId+"' and substr(DEODate,1,10) = '"+date+"' ORDER BY UniqueID DESC limit 1" ,null);
            if (res.getCount() > 0 )
            {
                // UID exists
                String lastUID;

                while(res.moveToNext())
                {
                    lastUID = res.getString(0);
                    date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                    //return CommonVariables.EmpCode+date+ String.format("%03d", Integer.toString(Integer.parseInt(lastUID.substring(lastUID.length() - 3)) + 1));
                    String lastCount = (Integer.toString(Integer.parseInt(lastUID.substring(lastUID.length() - 3)) + 1));
                    return CommonVariables.EmpCode+"M"+date+ ("000" + lastCount).substring(lastCount.length());
                }
            }
            else
            {
                // UID does not exists
                date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                return CommonVariables.EmpCode+"M"+date+ "001";
            }
            return "";
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    //for testing
    public String GetTop1PersonalInfoDataAgainstUID()
    {
        try
        {
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor res = db.rawQuery("select * from "+TABLE_PERSONAL_INFO_FORM+" where DEOName = '"+CommonVariables.UserId+"' and substr(DEODate,1,10) = '"+date+"' ORDER BY UniqueID DESC limit 1 " ,null);
            //Cursor res = db.rawQuery("select DEODate from "+TABLE_PERSONAL_INFO_FORM+" " ,null);
            if (res.getCount() > 0 )
            {
                // data exists

                while(res.moveToNext())
                {
                    return res.getString(0) + "," +
                            res.getString(1)+ "," +
                            res.getString(2)+ "," +
                            res.getString(3)+ "," +
                            res.getString(4)+ "," +
                            res.getString(5)+ "," +
                            res.getString(6)+ "," +
                            res.getString(7)+ "," +
                            res.getString(8)+ "," +
                            res.getString(9)+ "," +
                            res.getString(10)+ "," +
                            res.getString(11);

                    //return res.getString(0);
                }
            }
            else
            {
                // data does not exists
                return "no data";
            }
            return "";
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    public String GetUserNameAndEmpCode(String userid)
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor res = db.rawQuery("select EmployeeName, EmployeeCode from "+TABLE_LOGIN+" where userid = '"+userid+"' " ,null);
            if (res.getCount() > 0 )
            {
                // data exists

                while(res.moveToNext())
                {
                    return res.getString(0) + "," + res.getString(1);
                }
            }
            else
            {
                // data does not exists
                return "";
            }
            return "";
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    public String InsertPersonalInfoForm(String[] data)
    {
        SQLiteDatabase db ;
        db  = this.getWritableDatabase();
        try
        {
            db.beginTransaction();
            String str1 = "INSERT INTO "+TABLE_PERSONAL_INFO_FORM+" VALUES (" +
                    "'" + data[0]+ "'," +
                    "'" + data[1]+ "'," +
                    "'" + data[2]+ "'," +
                    "'" + data[3]+ "'," +
                    "'" + data[4]+ "'," +
                    "'" + data[5]+ "'," +
                    "'" + data[6]+ "'," +
                    "'" + data[7]+ "'," +
                    "'" + data[8]+ "'," +
                    "'" + data[9]+ "'," +
                    "'" + data[10]+ "'," +
                    "'" + data[11]+ "'" +
                    ")";

            db.execSQL(str1);
            db.setTransactionSuccessful();

            return "1"; //success message
        }
        catch (Exception e1)
        {
            return e1.getMessage();
        }
        finally {
            db.endTransaction();
        }
    }

    public String DeleteAndInsertPersonalInfoForm(String[] data)
    {
        SQLiteDatabase db ;
        db  = this.getWritableDatabase();
        try
        {
            db.beginTransaction();

            String deleteQuery = "delete from "+TABLE_PERSONAL_INFO_FORM+" where UniqueID = '" + data[0] +"'";
            db.execSQL(deleteQuery);

            String str1 = "INSERT INTO "+TABLE_PERSONAL_INFO_FORM+" VALUES (" +
                    "'" + data[0]+ "'," +
                    "'" + data[1]+ "'," +
                    "'" + data[2]+ "'," +
                    "'" + data[3]+ "'," +
                    "'" + data[4]+ "'," +
                    "'" + data[5]+ "'," +
                    "'" + data[6]+ "'," +
                    "'" + data[7]+ "'," +
                    "'" + data[8]+ "'," +
                    "'" + data[9]+ "'," +
                    "'" + data[10]+ "'," +
                    "'" + data[11]+ "'" +
                    ")";

            db.execSQL(str1);
            db.setTransactionSuccessful();

            return "1"; //success message
        }
        catch (Exception e1)
        {
            return e1.getMessage();
        }
        finally {
            db.endTransaction();
        }
    }

    public String InsertOralExamForm(String[] data)
    {
        SQLiteDatabase db ;
        db  = this.getWritableDatabase();
        try
        {
            db.beginTransaction();
            String str1 = "INSERT INTO "+TABLE_ORAL_EXAM_FORM+" VALUES (" +
                    "'" + data[0]+ "'," +
                    "'" + data[1]+ "'," +
                    "'" + data[2]+ "'," +
                    "'" + data[3]+ "'," +
                    "'" + data[4]+ "'," +
                    "'" + data[5]+ "'," +
                    "'" + data[6]+ "'," +
                    "'" + data[7]+ "'," +
                    "'" + data[8]+ "'," +
                    "'" + data[9]+ "'," +
                    "'" + data[10]+ "'," +
                    "'" + data[11]+ "'" +
                    ")";

            db.execSQL(str1);
            db.setTransactionSuccessful();

            return "1"; //success message
        }
        catch (Exception e1)
        {
            return e1.getMessage();
        }
        finally {
            db.endTransaction();
        }
    }

    public String InsertBreastExamForm(String[] data)
    {
        SQLiteDatabase db ;
        db  = this.getWritableDatabase();
        try
        {
            db.beginTransaction();
            String str1 = "INSERT INTO "+TABLE_BREAST_EXAM_FORM+" VALUES (" +
                    "'" + data[0]+ "'," +
                    "'" + data[1]+ "'," +
                    "'" + data[2]+ "'," +
                    "'" + data[3]+ "'," +
                    "'" + data[4]+ "'," +
                    "'" + data[5]+ "'," +
                    "'" + data[6]+ "'," +
                    "'" + data[7]+ "'," +
                    "'" + data[8]+ "'," +
                    "'" + data[9]+ "'," +
                    "'" + data[10]+ "'," +
                    "'" + data[11]+ "'" +
                    ")";

            db.execSQL(str1);
            db.setTransactionSuccessful();

            return "1"; //success message
        }
        catch (Exception e1)
        {
            return e1.getMessage();
        }
        finally {
            db.endTransaction();
        }
    }

    public String GetBasicDetailsFromPersonalInfoForm(String uniqueId)
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor res = db.rawQuery("select Village, Name, Sex, ContactNo, Age from "+TABLE_PERSONAL_INFO_FORM+" where UniqueID = '"+uniqueId+"' " ,null);
            if (res.getCount() > 0 )
            {
                // data exists

                while(res.moveToNext())
                {
                    return res.getString(0) + ";" + res.getString(1)+ ";" + res.getString(2)+ ";" + res.getString(3)+ ";" + res.getString(4);
                }
            }
            else
            {
                // data does not exists
                return "";
            }
            return "";
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    public Cursor getAllGetAllPersonalInfoFormAgainstDate(String date)
    {
        String selectQuery = "select * from "+TABLE_PERSONAL_INFO_FORM+" where substr(DEODate,1,10) = '"+date+"' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        return cursor;
    }

    public Cursor getAllGetAllNonUploadedPersonalInfoFormAgainstDate(String date)
    {
        String selectQuery = "select * from "+TABLE_PERSONAL_INFO_FORM+" where substr(DEODate,1,10) = '"+date+"' and SavedAtServer = 2 ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        return cursor;
    }

    public Cursor getAllGetAllOralExamFormAgainstDate(String date)
    {
        String selectQuery = "select * from "+TABLE_ORAL_EXAM_FORM+" where substr(DEODate,1,10) = '"+date+"' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        return cursor;
    }

    public Cursor getAllGetAllNonUploadedOralExamFormAgainstDate(String date)
    {
        String selectQuery = "select * from "+TABLE_ORAL_EXAM_FORM+" where substr(DEODate,1,10) = '"+date+"' and SavedAtServer = 2 ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        return cursor;
    }

    public Cursor getAllGetAllBreastExamFormAgainstDate(String date)
    {
        String selectQuery = "select * from "+TABLE_BREAST_EXAM_FORM+" where substr(DEODate,1,10) = '"+date+"' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        return cursor;
    }

    public Cursor getAllGetAllNonUploadedBreastExamFormAgainstDate(String date)
    {
        String selectQuery = "select * from "+TABLE_BREAST_EXAM_FORM+" where substr(DEODate,1,10) = '"+date+"' and SavedAtServer ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        return cursor;
    }

    public Cursor GetAllPersonalInfoDetailsAgainstUID(String UID)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_PERSONAL_INFO_FORM+" WHERE UniqueID = '"+UID+"'" ,null);
        return res;
    }

    public Cursor GetAllOralExamDetailsAgainstUID(String UID)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_ORAL_EXAM_FORM+" WHERE UniqueID = '"+UID+"'" ,null);
        return res;
    }

    public Cursor GetAllBreastExamDetailsAgainstUID(String UID)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_BREAST_EXAM_FORM+" WHERE UniqueID = '"+UID+"'" ,null);
        return res;
    }

    public Cursor CheckUserIDAndPassword(String userid, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_LOGIN + " WHERE userid = '" + userid + "' and password = '" + password + "'" ,null);
        return res;
    }

    public String UpdateSavedAtServerOfPersonalInfoFormAgainstUID(String UID)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("update " + TABLE_PERSONAL_INFO_FORM + " set SavedAtServer = '1' where UniqueID = '" + UID + "'");
            return "1";
        }
        catch(Exception e)
        {
            return e.getMessage();
        }
    }

    public String UpdateSavedAtServerOfOralExamAgainstUID(String UID)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("update " + TABLE_ORAL_EXAM_FORM + " set SavedAtServer = '1' where UniqueID = '" + UID + "'");
            return "1";
        }
        catch(Exception e)
        {
            return e.getMessage();
        }
    }

    public String UpdateSavedAtServerOfBreastExamAgainstUID(String UID)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("update " + TABLE_BREAST_EXAM_FORM + " set SavedAtServer = '1' where UniqueID = '" + UID + "'");
            return "1";
        }
        catch(Exception e)
        {
            return e.getMessage();
        }
    }
}
