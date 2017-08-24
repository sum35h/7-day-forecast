package apps.sumesh.android.a7_day_forecast;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Sumesh on 07-05-2017.
 */

public class DetailFragment extends Fragment {

    private static final String LOG_TAG=DetailFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG="#SunshineApp";
    private String mforecastStr;


    public DetailFragment()
    {setHasOptionsMenu(true);}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
     Intent intent=getActivity().getIntent();
        View rootView=inflater.inflate(R.layout.fragment_detail,container,false);
        if(intent!=null&&intent.hasExtra(Intent.EXTRA_TEXT)) {
            mforecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);

            TextView detail = (TextView)rootView.findViewById(R.id.detail_textview);
            detail.setText(mforecastStr);
        }




        return rootView;
    }


    private Intent createShareForecastIntent()
    {
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,mforecastStr+FORECAST_SHARE_HASHTAG);

        return shareIntent;


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment,menu);

        MenuItem menuItem=menu.findItem(R.id.action_share);

        ShareActionProvider mShareActionProvider=(ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(mShareActionProvider!=null)
        {

            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
        else
        {
            Log.d(LOG_TAG,"Share Action provider is null");
        }
    }
}
