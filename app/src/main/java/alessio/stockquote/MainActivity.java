package alessio.stockquote;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Arrays;


public class MainActivity extends ActionBarActivity {

    public final static String STOCK_SYMBOL = "alessio.stockquot.STOCK";

    private SharedPreferences stockSymbolsEntered;

    private TableLayout stockTableScrollView;
    private EditText stockSymbolEditText;
    Button stockSymbolButton;
    Button deleteSymbolsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stockSymbolsEntered = getSharedPreferences("stocklist", MODE_PRIVATE);

        stockTableScrollView = (TableLayout) findViewById(R.id.stockTableScrollView);
        stockSymbolEditText = (EditText) findViewById(R.id.stockSymbolEditText);
        stockSymbolButton = (Button) findViewById(R.id.stockSymbolButton);
        deleteSymbolsButton = (Button) findViewById(R.id.deleteSymbolsButton);

        stockSymbolButton.setOnClickListener(stockSymboLButtonListener);
        deleteSymbolsButton.setOnClickListener(deleteSymbolsButtonListener);
        updateSavedStockList(null);
    }

    private void updateSavedStockList(String newStockSymbol) {
        String[] stocks = stockSymbolsEntered.getAll().keySet().toArray(new String[0]);
        Arrays.sort(stocks, String.CASE_INSENSITIVE_ORDER);

        if (newStockSymbol != null) {
            insertStockInScrollView(newStockSymbol, Arrays.binarySearch(stocks, newStockSymbol));
        } else {
            for (int i = 0; i < stocks.length; ++i)
                insertStockInScrollView(stocks[i], i);
        }

    }

    private void saveStockSymbol(String stock) {
        String isNew = stockSymbolsEntered.getString(stock, null);
        SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit();
        preferencesEditor.putString(stock, stock);
        preferencesEditor.apply();
        if (isNew == null)
            updateSavedStockList(stock);
    }

    private void insertStockInScrollView(String stock, int i) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newStockRow = inflater.inflate(R.layout.stock_quote_row, null);
        TextView newStockTextView = (TextView) newStockRow.findViewById(R.id.stockSymbolTextView);
        newStockTextView.setText(stock);

        Button stockQuoteButton = (Button) newStockRow.findViewById(R.id.stockQuoteButton);
        stockQuoteButton.setOnClickListener(getStockActivityListener);

        Button quoteFromWebButtton = (Button) newStockRow.findViewById(R.id.quoteFromWebButton);
        quoteFromWebButtton.setOnClickListener(getStockFromWebsiteListener);

        stockTableScrollView.addView(newStockRow, i);
    }

    public OnClickListener stockSymboLButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (stockSymbolEditText.getText().length() > 0) {
                saveStockSymbol(stockSymbolEditText.getText().toString());
                stockSymbolEditText.setText("");

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(stockSymbolEditText.getWindowToken(), 0);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.invalid_stock_symbol);
                builder.setPositiveButton(R.string.ok, null);
                builder.setMessage(R.string.missing_stock_symbol);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    };

    public OnClickListener deleteSymbolsButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            stockTableScrollView.removeAllViews();
            SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit();
            preferencesEditor.clear();
            preferencesEditor.apply();
        }
    };

    public OnClickListener getStockActivityListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            TableRow row = (TableRow) v.getParent();
            TextView stockTextView  = (TextView) row.findViewById(R.id.stockSymbolTextView);
            String stockSymbol = stockTextView.getText().toString();

            Intent intent = new Intent(MainActivity.this, StockInfoActivity.class);
            intent.putExtra(STOCK_SYMBOL, stockSymbol);
            startActivity(intent);
        }
    };

    public OnClickListener getStockFromWebsiteListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            TableRow row = (TableRow) v.getParent();
            TextView stockTextView  = (TextView) row.findViewById(R.id.stockSymbolTextView);
            String stockSymbol = stockTextView.getText().toString();
            String url = getString(R.string.yahoo_stock_url) + stockSymbol;

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    };

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
