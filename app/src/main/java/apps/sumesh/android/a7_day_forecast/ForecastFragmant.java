package apps.sumesh.android.a7_day_forecast;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sumesh on 09-02-2016.
        */
public class ForecastFragmant extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private SimpleCursorAdapter mForecastAdapter;
    private static final int FORECAST_LOADER = 0;
    //private ForecastAdapter mForecastAdapter;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 2;
    static final int COL_WEATHER_DESC = 3;
    static final int COL_WEATHER_MAX_TEMP = 5;
    static final int COL_WEATHER_MIN_TEMP = 6;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    public ForecastFragmant() {

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.fragmentactivity, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_refresh) {
            //ForecastFragmant.FetchWeatherTask weatherTask = new ForecastFragmant.FetchWeatherTask();
            // weatherTask.execute("Bangalore,IN");

            updateWeather();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask(getContext());
        // String location=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        String location = Utility.getPreferredLocation(getContext());
        weatherTask.execute(location);
        getLoaderManager().restartLoader(0, null, this);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        String[] forecastArray = {"loading...", "loading...", "loading...", "loading...", "loading...", "loading...", "loading..."};

        List<String> weekforcast = new ArrayList<String>(Arrays.asList(forecastArray));
        //mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forcast, R.id.list_item_forecast_textview, weekforcast);
        mForecastAdapter=new SimpleCursorAdapter(getActivity(),R.layout.list_item_forcast,null,
                new String[]{WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP},
                new int[]{R.id.list_item_date_textview,
                        R.id.list_item_forecast_textview,
                        R.id.list_item_high_textview,
                        R.id.list_item_low_textview},
                0);
        //mForecastAdapter.notifyDataSetChanged();



        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){


            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                SimpleCursorAdapter adapter=(SimpleCursorAdapter)adapterView.getAdapter();
                Cursor cursor=adapter.getCursor();
                if(cursor!=null&&cursor.moveToPosition(i))
                {
                    String forecast=String.format("%s - %s - %s / %s",cursor.getString(COL_WEATHER_DATE),cursor.getString(COL_WEATHER_DESC),
                            cursor.getDouble(COL_WEATHER_MAX_TEMP
                            ),cursor.getDouble(COL_WEATHER_MIN_TEMP));
                    Intent intent=new Intent(getContext(),DetailActivity.class).putExtra(Intent.EXTRA_TEXT,forecast);
                    startActivity(intent);
                }
            }

        });


        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, WeatherContract.getDateDB());
        Log.d("foreccast fragment",""+weatherForLocationUri);
        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d("loader","finish");
        mForecastAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mForecastAdapter.swapCursor(null);
    }

}


    ////////////////////////////////////////////////////////////////////////////
//    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
//        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
//
//        ////////////////////////////////////////////////////////////
//        ////////////////////////////////////////////////////////////
//   /*
//        * Prepare the weather high/lows for presentation
//        * */
//        private String formatHighLows(double high, double low) {
//            // For presentation, assume the user doesn't care about tenths of a degree.
//
//            SharedPreferences sharedPrefs=PreferenceManager.getDefaultSharedPreferences(getActivity());
//            String unitType=sharedPrefs.getString(
//                    getString(R.string.pref_units_key),
//                    getString(R.string.pref_units_metric)//default :mertic->celcius
//            );
//            if(unitType.equals(getString(R.string.pref_units_imperial)))
//            {
//                high=(high*1.8)+32;
//                low=(low*1.8)+32;
//
//            }
//            else if(unitType.equals(getString(R.string.pref_units_metric)))
//            {
//                //nothing
//            }
//            else
//            Log.d(LOG_TAG,"unit type not found :"+unitType);
//
//
//            long roundedHigh = Math.round(high);
//            long roundedLow = Math.round(low);
//
//
//
//
//            String highLowStr = roundedHigh + "/" + roundedLow;
//            return highLowStr;
//        }
//
//        /**
//         * Take the String representing the complete forecast in JSON Format and
//         * pull out the data we need to construct the Strings needed for the wireframes.
//         * <p/>
//         * Fortunately parsing is easy: constructor takes the JSON string and converts
//         * it into an Object hierarchy for us.
//         */
//
//        //////////////////////JSON/////////////////////////////////////
//       /* {
//            "city":{
//            "id":1277333,
//                    "name":"Bangalore",
//                    "coord":{
//                "lon":77.6033,
//                        "lat":12.9762
//            },
//            "country":"IN",
//                    "population":0
//        },
//            "cod":"200",
//                "message":0.4294216,
//                "cnt":7,
//                "list":[
//            {
//                "dt":1494136800,
//                    "temp":{
//                "day":33.53,
//                        "min":21.53,
//                        "max":33.53,
//                        "night":21.53,
//                        "eve":32,
//                        "morn":33.53
//            },
//                "pressure":925.89,
//                    "humidity":62,
//                    "weather":[
//                {
//                    "id":501,
//                        "main":"Rain",
//                        "description":"moderate rain",
//                        "icon":"10d"
//                }
//                ],
//                "speed":2.02,
//                    "deg":35,
//                    "clouds":0,
//                    "rain":3.14
//            },
//            {
//                "dt":1494223200,
//                    "temp":{
//                "day":29.31,
//                        "min":19.07,
//                        "max":32.38,
//                        "night":20.78,
//                        "eve":27.07,
//                        "morn":19.07
//            },
//                "pressure":928.3,
//                    "humidity":67,
//                    "weather":[
//                {
//                    "id":501,
//                        "main":"Rain",
//                        "description":"moderate rain",
//                        "icon":"10d"
//                }
//                ],
//                "speed":1.97,
//                    "deg":273,
//                    "clouds":88,
//                    "rain":8.09
//            },
//            {
//                "dt":1494309600,
//                    "temp":{
//                "day":31.17,
//                        "min":18.57,
//                        "max":33.17,
//                        "night":18.7,
//                        "eve":22.44,
//                        "morn":18.57
//            },
//                "pressure":926.61,
//                    "humidity":72,
//                    "weather":[
//                {
//                    "id":501,
//                        "main":"Rain",
//                        "description":"moderate rain",
//                        "icon":"10d"
//                }
//                ],
//                "speed":2.29,
//                    "deg":234,
//                    "clouds":0,
//                    "rain":8.26
//            },
//            {
//                "dt":1494396000,
//                    "temp":{
//                "day":34.13,
//                        "min":21.35,
//                        "max":34.13,
//                        "night":21.35,
//                        "eve":30.61,
//                        "morn":21.69
//            },
//                "pressure":938.54,
//                    "humidity":0,
//                    "weather":[
//                {
//                    "id":501,
//                        "main":"Rain",
//                        "description":"moderate rain",
//                        "icon":"10d"
//                }
//                ],
//                "speed":4.62,
//                    "deg":320,
//                    "clouds":0,
//                    "rain":3.57
//            },
//            {
//                "dt":1494482400,
//                    "temp":{
//                "day":33.2,
//                        "min":19.8,
//                        "max":33.2,
//                        "night":23.82,
//                        "eve":29.93,
//                        "morn":19.8
//            },
//                "pressure":938.69,
//                    "humidity":0,
//                    "weather":[
//                {
//                    "id":500,
//                        "main":"Rain",
//                        "description":"light rain",
//                        "icon":"10d"
//                }
//                ],
//                "speed":1.91,
//                    "deg":305,
//                    "clouds":0,
//                    "rain":2.94
//            },
//            {
//                "dt":1494568800,
//                    "temp":{
//                "day":32.86,
//                        "min":20.02,
//                        "max":32.86,
//                        "night":22.55,
//                        "eve":26.89,
//                        "morn":20.02
//            },
//                "pressure":938.1,
//                    "humidity":0,
//                    "weather":[
//                {
//                    "id":501,
//                        "main":"Rain",
//                        "description":"moderate rain",
//                        "icon":"10d"
//                }
//                ],
//                "speed":1.78,
//                    "deg":285,
//                    "clouds":2,
//                    "rain":5.57
//            },
//            {
//                "dt":1494655200,
//                    "temp":{
//                "day":32.56,
//                        "min":19.64,
//                        "max":32.56,
//                        "night":22.7,
//                        "eve":27.98,
//                        "morn":19.64
//            },
//                "pressure":936.29,
//                    "humidity":0,
//                    "weather":[
//                {
//                    "id":501,
//                        "main":"Rain",
//                        "description":"moderate rain",
//                        "icon":"10d"
//                }
//                ],
//                "speed":3.23,
//                    "deg":289,
//                    "clouds":0,
//                    "rain":5.14
//            }
//            ]
//        }*/
//        /////////////////////////////////////////////////////
//
//        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
//                throws JSONException {
//
//            // These are the names of the JSON objects that need to be extracted.
//            final String OWN_LIST = "list";
//            final String OWN_WEATHER = "weather";
//            final String OWN_TEMPERATURE = "temp";
//            final String OWN_MAX = "max";
//            final String OWN_MIN = "min";
//            final String OWN_DESCRIPTION = "main";
//
//            JSONObject forecastJson = new JSONObject(forecastJsonStr);
//            JSONArray weatherArray = forecastJson.getJSONArray(OWN_LIST);
//
//
//            String[] resultStrs = new String[numDays];
//            for (int i = 0; i < weatherArray.length(); i++) {
//                // For now, using the form  at "Day, description, hi/low"
//                String day;
//                String description;
//                String highAndLow;
//
//                // Get the JSON object representing the day.
//                JSONObject dayForecast = weatherArray.getJSONObject(i);
//
//                // Create Gregorian Calender, which is in current date
//                GregorianCalendar gc = new GregorianCalendar();
//                // add i dates to current date of calendar
//                gc.add(GregorianCalendar.DATE, i);
//                // get the date, format it, and "save" it on variable day
//                Date time = gc.getTime();
//                SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
//                day = shortenedDateFormat.format(time);
//
//                // description is in a child array called "weather", which is 1 element long.
//                JSONObject weatherObject = dayForecast.getJSONArray(OWN_WEATHER).getJSONObject(0);
//                description = weatherObject.getString(OWN_DESCRIPTION);
//
//                // Temperatures are in a child object called "temp". Try not to name variables
//                // "temp" when working with temperature. It confuses everybody.
//                JSONObject temperatureObject = dayForecast.getJSONObject(OWN_TEMPERATURE);
//                double high = temperatureObject.getDouble(OWN_MAX);
//                double low = temperatureObject.getDouble(OWN_MIN);
//
//                highAndLow = formatHighLows(high, low);
//                resultStrs[i] = day + " - " + description + " - " + highAndLow;
//            }
//
//            for (String s : resultStrs) {
//                Log.v(LOG_TAG, "Forecast entry: " + s);
//            }
//            return resultStrs;
//        }
//
//        //////////////////////////////////////////////////////////
//        ////////////////////////////////////////////////////////
//        @Override
//        protected String[] doInBackground(String... params) {
//            // These two need to be declared outside the try/catch
//// so that they can be closed in the finally block.
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            // Will contain the raw JSON response as a string.
//            String forecastJsonStr = null;
//            String format = "json";
//            String units = "metric";
//            int numDays = 7;
//
//            try {
//                // Construct the URL for the OpenWeatherMap query
//                // Possible parameters are avaiable at OWM's forecast API page, at
//                // http://openweathermap.org/API#forecast
//                // URL url = new URL("http://api.openweathermap.org/data/2.5/weather?id=1277333&appid=44db6a862fba0b067b1930da0d769e98");
//                // http://api.openweathermap.org/data/2.5/forecast/city?id=524901&APPID=db27f1ceb2162a8b418260d289e62344
//
//                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=Darjeeling,in&mode=xml&units=metric&cnt=7&appid=db27f1ceb2162a8b418260d289e62344");
//                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
//                final String QUERY_PARAM = "q";
//                final String FORMAT_PARAM = "mode";
//                final String UNIT_PARAM = "units";
//                final String DAYS_PARAM = "cnt";
//                final String APPID = "appid";
//                final String key = "db27f1ceb2162a8b418260d289e62344";
//                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
//                        .appendQueryParameter(QUERY_PARAM, params[0])
//                        .appendQueryParameter(FORMAT_PARAM, format)
//                        .appendQueryParameter(UNIT_PARAM, units)
//                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
//                        .appendQueryParameter(APPID, key)
//                        .build();
//                URL url = new URL(builtUri.toString());
//                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
//                // Create the request to OpenWeatherMap, and open the connection
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                // Read the input stream into a String
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                if (inputStream == null) {
//                    // Nothing to do.
//                    forecastJsonStr = null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                    // But it does make debugging a *lot* easier if you print out the completed
//                    // buffer for debugging.
//                    buffer.append(line + "\n");
//                }
//
//                if (buffer.length() == 0) {
//                    // Stream was empty.  No point in parsing.
//                    forecastJsonStr = null;
//                }
//                forecastJsonStr = buffer.toString();
//
//                Log.v(LOG_TAG, "Forecast Json Strings: " + forecastJsonStr);
//            } catch (IOException e) {
//                Log.e("PlaceholderFragment", "Error ", e);
//                // If the code didn't successfully get the weather data, there's no point in attemping
//                // to parse it.
//                forecastJsonStr = null;
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e("PlaceholderFragment", "Error closing stream", e);
//                    }
//                }
//            }
//            try {
//               return getWeatherDataFromJson(forecastJsonStr, numDays);
//            } catch (JSONException e) {
//                Log.e(LOG_TAG, e.getMessage(), e);
//                e.printStackTrace();
//            }
//            return null;
//        }
//        @Override
//        protected  void onPostExecute(String[] result)
//        {                Log.e(LOG_TAG, "onpostexec!!!!"+result);
//
//            if(result!=null)
//            {
//                mForecastAdapter.clear();
//                for(String dayForecastStr :result){
//                    mForecastAdapter.add(dayForecastStr);
//                }}
//
//        }
//        }
//    }
   //}
//}
