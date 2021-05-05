// Yassine Lakhmarti S1903349
package org.me.gcu.equakestartercode;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static List<Item> itemList = new ArrayList<>();
    private Button startButton;
    private Button mapButton;
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    private ProgressDialog progressDialog;
    String text;
    String feedRSS = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";

    /*
     onCreate() method performs the basic application start-up logic, we first link the activity to the correct view,
     we initialize the widgets buttons plus recycler view
     we then get the data from the feedRSS, if the saved instance isn't null, we clear the itemList

    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setStartButton(findViewById(R.id.btn_get_all));
        setMapButton(findViewById(R.id.btn_get_map));
        recyclerView = findViewById(R.id.recycler_view);

        getStartButton().setOnClickListener(view -> {
            itemList = new ArrayList<>();
            new AsyncTaskExample(false).execute(feedRSS);
        });

        getMapButton().setOnClickListener(view -> {
            itemList = new ArrayList<>();
            new AsyncTaskExample(true).execute(feedRSS);
        });

        if (savedInstanceState != null) {
            itemList.clear();
            itemList.addAll(savedInstanceState.getParcelableArrayList("key"));
            setItemAdapter(itemList);
        }
    }
/*
    Allow the activity to persist it's UI state
*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("key", new ArrayList<Item>(itemList));

    }

    /*
        Retrieve the previously save instance state
     */

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        itemList.clear();
        itemList.addAll(savedInstanceState.getParcelableArrayList("key"));
    }
/*
    Add items to the actions bar
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }
/*
    Handle Item Selection
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_filter) {
            Intent intent = new Intent(MainActivity.this, DateActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        if (itemAdapter == null) {
            setItemAdapter(itemList);
        }
        super.onResume();

    }

    public void setItemAdapter(List<Item> itemList) {
        itemAdapter = new ItemAdapter(MainActivity.this, itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(itemAdapter);
    }

    public Button getStartButton() {
        return startButton;
    }

    public void setStartButton(Button startButton) {
        this.startButton = startButton;
    }

    public Button getMapButton() {
        return mapButton;
    }

    public void setMapButton(Button mapButton) {
        this.mapButton = mapButton;
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }
/*
    This Async task is responsible for getting the data from the RSSFeed
    It first launches a ProgressDialog while it's running a XMLPullParser to convert the data properly in the background
    It then sorts the data in order of magnitude
 */
    //    create AsyncTaskExample class for getting data from url
    private class AsyncTaskExample extends AsyncTask<String, String, List<Item>> {
        boolean openMap;

        public AsyncTaskExample(boolean openMap) {
            this.openMap = openMap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            add ProgressDialog
            setProgressDialog(new ProgressDialog(MainActivity.this));
            getProgressDialog().setMessage("The Data is loading....");
            getProgressDialog().setIndeterminate(false);
            getProgressDialog().setCancelable(false);
            getProgressDialog().show();
        }

        @Override
        protected List<Item> doInBackground(String... strings) {
            int i = 0;
            Item item = null;
            URL url;
            URLConnection urlConnection;
            BufferedReader in = null;

            try {
                Log.e("MyTag", "in try");
                url = new URL(strings[0]);
                urlConnection = url.openConnection();
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                Log.e("MyTag", "after ready");

                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser parser = factory.newPullParser();
                    parser.setInput(urlConnection.getInputStream(), null);
                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        String tagName = parser.getName();
                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                if (tagName.equalsIgnoreCase("item")) {
                                    item = new Item();
                                }
                                break;

                            case XmlPullParser.TEXT:
                                text = parser.getText();
                                break;

                            case XmlPullParser.END_TAG:
                                if (item != null) {
                                    if (tagName.equalsIgnoreCase("title")) {
                                        item.setTitle(text);
                                    } else if (tagName.equalsIgnoreCase("description")) {
                                        String[] strings1 = text.split(";");
                                        String location = strings1[1].split(":")[1].trim();
                                        String[] latLong = strings1[2].split(":")[1].trim().split(",");
                                        String depth = strings1[3].replaceAll("[^\\d.]", "").replaceAll(":", "");
                                        String magnitude = strings1[4].replaceAll("[^\\d.]", "").replaceAll(":", "");
                                        item.setDescription(text);
                                        item.setLocation(location);
                                        item.setDepth(Double.parseDouble(depth));
                                        item.setMagnitude(Double.parseDouble(magnitude));
                                        item.setLatitude(Double.parseDouble(latLong[0]));
                                        item.setLongitude(Double.parseDouble(latLong[1]));
                                    } else if (tagName.equalsIgnoreCase("link")) {
                                        item.setLink(text);
                                    } else if (tagName.equalsIgnoreCase("pubDate")) {
                                        item.setPubDate(text);
                                    } else if (tagName.equalsIgnoreCase("category")) {
                                        item.setCategory(text);
                                    } else if (tagName.equalsIgnoreCase("item")) {
                                        i++;
                                        item.setId(i);
                                        Log.d("theS", "doInBackground: " + item.toString());
                                        itemList.add(item);
                                    }
                                }

                                break;

                            default:
                                break;
                        }
                        eventType = parser.next();
                    }

                    Collections.sort(itemList, (obj1, obj2) -> {
                        return Double.compare(obj2.getMagnitude(), obj1.getMagnitude());
                    });
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
                in.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception in run");
            }
            return itemList;
        }

        @Override
        protected void onPostExecute(List<Item> itemList) {
            super.onPostExecute(itemList);
            if (itemList != null) {
                getProgressDialog().hide();

                if (openMap) {
//                get to EarthquakeActivity with item model
                    Intent intent = new Intent(MainActivity.this, MapActivity.class);
                    startActivity(intent);
                } else {
//                set ItemAdapter on recyclerView
                    setItemAdapter(itemList);
                }

            } else {
                getProgressDialog().show();
            }
        }
    }
}