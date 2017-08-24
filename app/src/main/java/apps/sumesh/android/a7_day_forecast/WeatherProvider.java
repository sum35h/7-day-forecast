package apps.sumesh.android.a7_day_forecast;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Sumesh on 14-05-2017.
 */

public class WeatherProvider extends ContentProvider{

        private static final int WEATHER=100;
        private static final int WEATHER_WITH_LOCATION=101;
    private static final int WEATHER_WITH_LOCATION_AND_DATE=102;
    private static final int LOCATION=300;
    private static final int LOCATION_ID=301;

    private static final UriMatcher sUriMatcher=buildUriMatcher();
   private WeatherDbHelper mOpenHelper;

private static UriMatcher buildUriMatcher()
{
     final UriMatcher matcher =new UriMatcher(UriMatcher.NO_MATCH);
    final String authority=WeatherContract.CONTENT_AUTHORITY;

    matcher.addURI(authority,WeatherContract.PATH_WEATHER,WEATHER);
    matcher.addURI(authority,WeatherContract.PATH_WEATHER+"/*",WEATHER_WITH_LOCATION);
    matcher.addURI(authority,WeatherContract.PATH_WEATHER+"/*/*",WEATHER_WITH_LOCATION_AND_DATE);

     matcher.addURI(authority,WeatherContract.PATH_LOCATION,LOCATION);
    matcher.addURI(authority,WeatherContract.PATH_LOCATION+"/#",LOCATION_ID);

    return matcher;

}

     @Override
     public boolean onCreate()
     {
         mOpenHelper=new WeatherDbHelper(getContext());
         return true;

     }



    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
