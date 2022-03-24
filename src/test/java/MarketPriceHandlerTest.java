import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.text.ParseException;

public class MarketPriceHandlerTest {

    @BeforeAll
    static void runBeforeAll() throws IOException {
        MarketPriceHandler.convertCSVtoJsonAndApplyCommission();
        MarketPriceHandler.getAdjustedBidAskPrices();
    }


    @Test
    public void validateAllDataFromCSVBeenRead() throws IOException {
        Assertions.assertEquals(MarketPriceHandler.getAllPrices(), MarketPriceHandler.getAmountOfPricesFromCsv());
    }

    @Test
    public void checkCommissionIsAppliedCorrectly() throws ParseException {
        MarketPriceHandler.checkCommissionIsAppliedCorrectly();
    }

    @Test
    public void AllPricesAreProcessed() throws IOException, ParseException {
        Assertions.assertEquals(MarketPriceHandler.getAmountOfPricesFromCsv(), MarketPriceHandler.getAmountOfProcessedPrices());
    }

    @ParameterizedTest
    @ValueSource(strings = {"EUR/USD","EUR/JPY","GBP/USD"})
    public void validateOnlyLatestPriceCanBeSeen(String name) throws ParseException {
        MarketPriceHandler.getLatestPrice(name);
    }

    @Test
    public void validateAllPricesBidLessAsk() throws ParseException {
        MarketPriceHandler.checkAdjustedBidIsLowerAsk();
    }

    @Test
    public void validateCommissionIsAppliedToAllPrices() throws ParseException {
        MarketPriceHandler.checkCommissionIsAppliedToAllPrices();
    }
}
