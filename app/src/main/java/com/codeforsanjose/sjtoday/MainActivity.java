package com.codeforsanjose.sjtoday;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FetcherException;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SJToday";

    private ATOMParser mAtomParser = ATOMParser.getInstance();

    private TextView mEventList_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup the views
        mEventList_tv = (TextView) findViewById(R.id.serverResponse);

        // populate the views with data
        new SJTodayFeedAsync().execute("http://calagator.org/events.atom");
    }


    private class SJTodayFeedAsync extends AsyncTask<String, Double, SyndFeed> {

        private ProgressDialog mProgressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mEventList_tv == null) {
                Log.e(TAG, "ERROR TV NULL!");
            }
            mEventList_tv.setText((CharSequence) "");
            // set a non-cancellable indeterminate progress dialog
            mProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait...",
                    "Fetching latest events", true, false);
        }

        @Override
        protected SyndFeed doInBackground(String... params) {
            try {
                return retrieveFeed(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FetcherException e) {
                e.printStackTrace();
            } catch (FeedException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Function that actually retrieves the feed from the given URL
         * @param feedURL
         * @return
         * @throws IOException
         * @throws FetcherException
         * @throws FeedException
         */
        private SyndFeed retrieveFeed(String feedURL)
                throws IOException, FetcherException, FeedException {
            FeedFetcher feedFetcher = new HttpURLFeedFetcher();
            return feedFetcher.retrieveFeed(new URL(feedURL));
        }

        @Override
        protected void onPostExecute(SyndFeed syndFeed) {
            super.onPostExecute(syndFeed);

            /*
            Parse the received feed and get a list of events to iterate on
             */
            try {
                List<SJTodayEvent> eventList = mAtomParser.processFeed(syndFeed);
                Log.e(TAG, "Total Number of events downloaded = " + eventList.size());
                Iterator<SJTodayEvent> eventIterator = eventList.iterator();
                while(eventIterator.hasNext()) {

                    StringBuffer buffer = new StringBuffer();
                    buffer.append(mEventList_tv.getText());
                    buffer.append(eventIterator.next().toString());

                    mEventList_tv.setText((CharSequence) buffer.toString());

                }
            } catch (FeedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mProgressDialog != null)
                mProgressDialog.dismiss();
            if (mProgressDialog != null)
                mProgressDialog.cancel();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
