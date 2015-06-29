package alessio.stockquote;

/**
 * Created by Alexander on 2015-06-27.
 */
public class StockInfo {
    String daysLow = "";

    public StockInfo(String yearLow, String yearHigh, String daysLow, String daysHigh, String name, String lastTradePriceOnly, String change, String daysRange) {
        this.daysLow = daysLow;
        this.daysHigh = daysHigh;
        this.yearLow = yearLow;
        this.yearHigh = yearHigh;
        this.name = name;
        this.lastTradePriceOnly = lastTradePriceOnly;
        this.change = change;
        this.daysRange = daysRange;
    }

    String daysHigh = "";
    String yearLow = "";
    String yearHigh = "";
    String name = "";
    String lastTradePriceOnly = "";
    String change = "";
    String daysRange = "";
}
