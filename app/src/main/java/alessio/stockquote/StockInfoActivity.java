package alessio.stockquote;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class StockInfoActivity extends ActionBarActivity {

    private static final String TAG = "STOCKQUOTE";

    TextView companyNameDataTextView;
    TextView yearLowDataTextView;
    TextView yearHighDataTextView;
    TextView daysLowDataTextView;
    TextView daysHighDataTextView;;
    TextView lastTradePriceOnlyDataTextView;
    TextView changeDataTextView;
    TextView daysRangeDataTextView;

    static final String KEY_ITEM = "quote";
    static final String KEY_NAME = "Name";
    static final String KEY_YEAR_LOW = "YearLow";
    static final String KEY_YEAR_HIGH = "YearHigh";
    static final String KEY_DAYS_LOW = "DaysLow";
    static final String KEY_DAYS_HIGH = "DaysHigh";
    static final String KEY_LAST_TRADE_PRICE = "LastTradePriceOnly";
    static final String KEY_CHANGE = "Change";
    static final String KEY_DAYS_RANGE = "DaysRange";

    String companyName;
    String yearLow;
    String yearHigh;
    String daysLow;
    String daysHigh;
    String lastTradePriceOnly;
    String change;
    String daysRange;

    String queryUrl(String symbol) {
        return "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22"
            + symbol
            + "%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_info);

        Intent intent = getIntent();
        String symbol = intent.getStringExtra(MainActivity.STOCK_SYMBOL);

        companyNameDataTextView = (TextView) findViewById(R.id.companyNameDataTextView);
        yearLowDataTextView = (TextView) findViewById(R.id.yearLowDataTextView);
        yearHighDataTextView = (TextView) findViewById(R.id.yearHighDataTextView);
        daysLowDataTextView = (TextView) findViewById(R.id.daysLowDataTextView);
        daysHighDataTextView = (TextView) findViewById(R.id.daysHighDataTextView);
        lastTradePriceOnlyDataTextView = (TextView) findViewById(R.id.lastTradePriceOnlyDataTextView);
        changeDataTextView = (TextView) findViewById(R.id.changeDataTextView);
        daysRangeDataTextView = (TextView) findViewById(R.id.daysRangeDataTextView);

        Log.d(TAG, "Before URL Creation of " + symbol);

        final String url = queryUrl(symbol);
        Log.d(TAG, "URL: " + url);

        new MyAsyncTask().execute(url);
    }

    private class MyAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = connection.getInputStream();
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document dom = db.parse(in);
                    Element docEl = dom.getDocumentElement();
                    NodeList nl = docEl.getElementsByTagName(KEY_ITEM);
                    if (nl != null && nl.getLength() > 0) {
                        for (int i = 0; i < nl.getLength(); ++i) {
                            StockInfo stock = getStockInformation(docEl);
                            companyName = stock.name;
                            yearLow = stock.yearLow;
                            yearHigh = stock.yearHigh;
                            daysLow = stock.daysLow;
                            daysHigh = stock.daysHigh;
                            lastTradePriceOnly = stock.lastTradePriceOnly;
                            change = stock.change;
                            daysRange = stock.daysRange;
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            companyNameDataTextView.setText(companyName);
            yearLowDataTextView.setText(yearLow);
            yearHighDataTextView.setText(yearHigh);
            daysLowDataTextView.setText(daysLow);
            daysHighDataTextView.setText(daysHigh);
            lastTradePriceOnlyDataTextView.setText(lastTradePriceOnly);
            changeDataTextView.setText(change);
            daysRangeDataTextView.setText(daysRange);
        }

        private StockInfo getStockInformation(Element entry) {
            return new StockInfo(
                    getTextValue(entry, KEY_DAYS_LOW),
                    getTextValue(entry, KEY_DAYS_HIGH),
                    getTextValue(entry, KEY_YEAR_LOW),
                    getTextValue(entry, KEY_YEAR_HIGH),
                    getTextValue(entry, KEY_NAME),
                    getTextValue(entry, KEY_LAST_TRADE_PRICE),
                    getTextValue(entry, KEY_CHANGE),
                    getTextValue(entry, KEY_DAYS_RANGE)
            );
        }

        private String getTextValue(Element entry, String tag) {
            NodeList nl = entry.getElementsByTagName(tag);
            if (nl != null && nl.getLength() > 0) {
                Element element = (Element) nl.item(0);
                return element.getFirstChild().getNodeValue();
            }
            else
                return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stock_info, menu);
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
