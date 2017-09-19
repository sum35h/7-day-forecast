package apps.sumesh.android.a7_day_forecast;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Debug;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Map;
import java.util.Set;

import apps.sumesh.android.a7_day_forecast.WeatherContract.LocationEntry;
/**
 * Created by Sumesh on 13-05-2017.
 */

public class TestDb extends AndroidTestCase{

    private static final String LOG_TAG=TestDb.class.getSimpleName();
    public void testCreateDb() throws Throwable
    {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db=new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true,db.isOpen());
        db.close();
    }

    String TEST_CITY_NAME="North pole";

    ContentValues getLocationContentValues()
    {

        String testLocationSetting="99209";
        double testLatitude=33.3;
        double testLongitude=-232.1;

        ContentValues values=new ContentValues();

        values.put(LocationEntry.COLUMN_CITY_NAME,TEST_CITY_NAME);
        values.put(LocationEntry.COLUMN_COORD_LAT,testLatitude);
        values.put(LocationEntry.COLUMN_COORD_LONG,testLongitude);
        values.put(LocationEntry.COLUMN_LOCATION_SETTING,testLocationSetting);
        return values;
    }

    public static void validateCursor(ContentValues expectedValues,Cursor valueCursor)
    {
        Set<Map.Entry<String,Object>> valueSet=expectedValues.valueSet();

           for(Map.Entry<String,Object>entry:valueSet)
           {
               String columnName=entry.getKey();
               int idx=valueCursor.getColumnIndex(columnName);
               assertFalse(idx==-1);

               String expectedValue=entry.getValue().toString();
               Log.d(LOG_TAG,expectedValue+" "+valueCursor.getString(idx));
               assertEquals(expectedValue,valueCursor.getString(idx));
           }
    }
    ContentValues getWeatherContentValues(long locationRowId)
    {
        long testDate=19952606;
        String testDescreption="some stuf bla bla";
        int testWeatherId=902;
        double testMintemp=21.1;
        double testMaxtemp=91.1;

        double testHumidity=99.1;
        double testPressure=39.1;
        double testWind=30.2;
        double testDegrees=40.2;
        ContentValues values=new ContentValues();
        values.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY,locationRowId);
        values.put(WeatherContract.WeatherEntry.COLUMN_DATE,testDate);
        values.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,testDescreption);
        values.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,testWeatherId);
        values.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,testMintemp);
        values.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,testMaxtemp);
        values.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY,testHumidity);
        values.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE,testPressure);
        values.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,testWind);
        values.put(WeatherContract.WeatherEntry.COLUMN_DEGREES,testDegrees);
        return values;
    }




public void testInsertReadDb()
{
    WeatherDbHelper dbHelper=new WeatherDbHelper(mContext);
    SQLiteDatabase db=dbHelper.getWritableDatabase();

    ContentValues values=getLocationContentValues();

//   String[] columns={LocationEntry._ID,
//                      LocationEntry.COLUMN_LOCATION_SETTING,
//                       LocationEntry.COLUMN_CITY_NAME,LocationEntry.COLUMN_COORD_LAT,
//                        LocationEntry.COLUMN_COORD_LONG};

    long locationRowId;
    locationRowId=db.insert(LocationEntry.TABLE_NAME,null,values);
    Cursor cursor=db.query(LocationEntry.TABLE_NAME, null,null,null,null,null,null);
    assertTrue(locationRowId!=-1);
    Log.d(LOG_TAG,"New row id: "+locationRowId);
    if(cursor.moveToFirst()){
//        int locationIndex=cursor.getColumnIndex(LocationEntry.COLUMN_LOCATION_SETTING);
//        String location=cursor.getString(locationIndex);
//
//        int nameIndex=cursor.getColumnIndex(LocationEntry.COLUMN_CITY_NAME);
//        String name=cursor.getString(nameIndex);
//
//        int latIndex=cursor.getColumnIndex(LocationEntry.COLUMN_COORD_LAT);
//        double latitude=cursor.getDouble(latIndex);
//
//        int longIndex=cursor.getColumnIndex(LocationEntry.COLUMN_COORD_LONG);
//         double longitude=cursor.getDouble(longIndex);
//        Log.d(LOG_TAG,"Row: "+location+" "+location+" "+latitude+" "+longitude);
//        assertEquals(testName,name);
//        assertEquals(testLocationSetting,location);
//        assertEquals(testLatitude,latitude);
//        assertEquals(testLongitude,longitude);
        validateCursor(values,cursor);

    }
    else
    {
        fail("No values returned :");
    }

    ContentValues weatherValues =getWeatherContentValues(locationRowId);


    long locationRowId2;

//    String[] columns_weather={WeatherContract.WeatherEntry._ID,WeatherContract.WeatherEntry.COLUMN_LOC_KEY,
//            WeatherContract.WeatherEntry.COLUMN_DATE, WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
//            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
//            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,WeatherContract.WeatherEntry.COLUMN_PRESSURE, WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
//            WeatherContract.WeatherEntry.COLUMN_DEGREES};

    locationRowId2=db.insert(WeatherContract.WeatherEntry.TABLE_NAME,null,weatherValues);
    assertTrue(locationRowId!=-1);
    Log.d(LOG_TAG,"row id weather:"+locationRowId2);

    Cursor weatherCursor=db.query(WeatherContract.WeatherEntry.TABLE_NAME,null,null,null,null,null,null);
    assertTrue(locationRowId2!=-1);

    if(weatherCursor.moveToFirst())
    {
//        int dateIndex=cursor2.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
//        long date=cursor2.getLong(dateIndex);
//
//        int descIndex=cursor2.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);
//        String descreption=cursor2.getString(descIndex);
//
//        int  weatherIdIndex=cursor2.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID);
//        int weatherId=cursor2.getInt(weatherIdIndex);
//
//        int minTempIndex=cursor2.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
//        double minTemp=cursor2.getDouble(minTempIndex);
//
//        int maxTempIndex=cursor2.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
//        double maxTemp=cursor2.getDouble(maxTempIndex);
//
//        int humidityIndex=cursor2.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY);
//        double humidity=cursor2.getDouble(humidityIndex);
//
//        int pressureIndex=cursor2.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE);
//        double pressure=cursor2.getDouble(pressureIndex);
//
//        int windIndex=cursor2.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED);
//        double wind=cursor2.getDouble(windIndex);
//
//        int degreeIndex=cursor2.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES);
//        double degrees=cursor2.getDouble(degreeIndex);
//
//        assertEquals(testDate,date);
//        assertEquals(testDescreption,descreption);
//        assertEquals(testWeatherId,weatherId);
//        assertEquals(testMintemp,minTemp);
//        assertEquals(testMaxtemp,maxTemp);
//        assertEquals(testHumidity,humidity);
//        assertEquals(testPressure,pressure);
//        assertEquals(testWind,wind);
//        assertEquals(testDegrees,degrees);

      validateCursor(weatherValues,weatherCursor);


    }else
    {
        fail("Weather Table values not found");
    }






}

}
