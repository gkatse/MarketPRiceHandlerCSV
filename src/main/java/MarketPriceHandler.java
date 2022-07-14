import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class MarketPriceHandler {

    public static void main(String[] args) throws ParseException {


        System.out.println(getLatestPrice("EUR/USD"));
        System.out.println(getLatestPrice("EUR/JPY"));
        System.out.println(getLatestPrice("GBP/USD"));

    }

    static StringBuilder messageList = new StringBuilder();
    static StringBuilder originalBidPriceList = new StringBuilder();
    static StringBuilder originalAskPriceList = new StringBuilder();

    static StringBuilder adjustedBidPriceList = new StringBuilder();
    static StringBuilder adjustedAskPriceList = new StringBuilder();

    static float commission = (float) 0.001;
    static String CSV_FILE_PATH = "src/main/resources/MarketPriceFeed.csv";


    public static String convertCSVtoJsonAndApplyCommission() {
        messageList.setLength(0);
        originalBidPriceList.setLength(0);
        originalAskPriceList.setLength(0);

        // Reading the CSV file
        Pattern pattern = Pattern.compile(",");
        try (BufferedReader in = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            List<CSVPrice> players = in.lines().map(line -> {
                String[] x = pattern.split(line);
                messageList.append(x[0]);
                messageList.append(",");
                originalBidPriceList.append(x[2]);
                originalBidPriceList.append(",");
                originalAskPriceList.append(x[3]);
                originalAskPriceList.append(",");

                //Applying the commission to the bid and ask prices
                return new CSVPrice(Integer.parseInt(x[0]), x[1], Float.parseFloat(x[2]) - Float.parseFloat(x[2]) *
                        commission, Float.parseFloat(x[3]) + Float.parseFloat(x[3]) * commission, x[4]);
            }).collect(Collectors.toList());
            // Generating Json with adjusted bid and ask prices
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(players);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getAllPrices() {
        return messageList.toString().split(",").length;
    }

    public static String getOriginalBidPriceList() {
        return originalBidPriceList.toString();
    }

    public static String getOriginalAskPriceList() {
        return originalAskPriceList.toString();
    }

    public static int getAmountOfPricesFromCsv() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(CSV_FILE_PATH));
        int count = 0;
        while(bufferedReader.readLine() != null)
        {
            count++;
        }
        //System.out.println("Count : "+count);
        return count;
    }

    public static void getAdjustedBidAskPrices() throws IOException {
        adjustedBidPriceList.setLength(0);
        adjustedAskPriceList.setLength(0);
        try (BufferedReader in = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] cols = line.split(",");
                cols[2] = String.valueOf((Float.parseFloat(cols[2]) - (commission * Float.parseFloat(cols[2]))));
                cols[3] = String.valueOf((Float.parseFloat(cols[3]) + (commission * Float.parseFloat(cols[3]))));
                adjustedBidPriceList.append(cols[2]);
                adjustedBidPriceList.append(",");
                adjustedAskPriceList.append(cols[3]);
                adjustedAskPriceList.append(",");
            }

        }
    }

    public static String getAdjustedBidPriceList() {
        return adjustedBidPriceList.toString();
    }

    public static String getAdjustedAskPriceList() {
        return adjustedAskPriceList.toString();
    }

    public static List<Map<String, Object>> parseAndReadJson() {

        String json = convertCSVtoJsonAndApplyCommission();
        System.out.println(json);

        List<Map<String, Object>> d = JsonPath.parse(json).read("$[?(@)]");
        System.out.println(d);
        return d;

    }

    public static void checkAdjustedBidIsLowerAsk() {
        List<Map<String, Object>> d = parseAndReadJson();
        for (Map<String, Object> resultMap : d) {
            System.out.println(resultMap.get("bid").toString());
            System.out.println(resultMap.get("ask").toString());
            /*Method returns:
            -1 – if a < b)
            0 – if a == b
            1 – if a > b */
            Assertions.assertEquals(-1, Float.compare(Float.parseFloat(resultMap.get("bid").toString()), Float.parseFloat(resultMap.get("ask").toString())));
        }

    }

    public static void checkCommissionIsAppliedToAllPrices() {

        List<Map<String, Object>> d = parseAndReadJson();
        int i = 0;
        for (Map<String, Object> resultMap : d) {
            String[] strArrayOriginalBidPrice = getOriginalBidPriceList().replace(" ", "")
                    .split(",");
            String[] strArrayOriginalAskPrice = getOriginalAskPriceList().replace(" ", "")
                    .split(",");

                System.out.println(i);
                System.out.println(resultMap.get("ask").toString());
                System.out.println(strArrayOriginalAskPrice[i]);
                Assertions.assertNotEquals(resultMap.get("bid"), strArrayOriginalBidPrice[i]);
                Assertions.assertNotEquals(resultMap.get("ask"), strArrayOriginalAskPrice[i]);

            i += 1;
        }
    }


    public static void checkCommissionIsAppliedCorrectly() {

        List<Map<String, Object>> d = parseAndReadJson();
        int i = 0;
        for (Map<String, Object> resultMap : d) {
            String[] strArrayAdjustedBidPrice = getAdjustedBidPriceList().replace(" ", "")
                    .split(",");
            String[] strArrayAdjustedAskPrice = getAdjustedAskPriceList().replace(" ", "")
                    .split(",");

            System.out.println(i);
            System.out.println(resultMap.get("ask").toString());
            System.out.println(strArrayAdjustedAskPrice[i]);
            Assertions.assertEquals(resultMap.get("bid"), Double.parseDouble(strArrayAdjustedBidPrice[i]));
            Assertions.assertEquals(resultMap.get("ask"), Double.parseDouble(strArrayAdjustedAskPrice[i]));

            i += 1;
        }
    }


    public static int getAmountOfProcessedPrices() {
        List<Map<String, Object>> d = parseAndReadJson();
        int sizeOfMessages = d.size();
        System.out.println(sizeOfMessages);
        return sizeOfMessages;


    }


    // This is a comment
    // This is a comment 1
    // This is a comment 2
    // This is a comment 3

    // Change made from feature branch


    public static Object getLatestPrice(String name) throws ParseException {

    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS");
    String json = convertCSVtoJsonAndApplyCommission();

    //Filter Json data by timestamp, getting the latest timestamp
    List<Map<String, Object>> d = JsonPath.parse(json).read("$[?(@.name==' "+ name + "')]");
    Map<String, Object> mostRecentTimestampItem = new HashMap<>();
    long mostRecentTimestamp = 0;
    for (Map<String, Object> resultMap : d) {
        long milliSeconds = Long.parseLong(String.valueOf(df.parse((String) resultMap.get("timestamp")).getTime()));
//                System.out.println(milliSeconds);
        if (mostRecentTimestamp < milliSeconds) {
            mostRecentTimestamp = milliSeconds;
            mostRecentTimestampItem = resultMap;
        }
    }

    for (Map<String, Object> resultMap1 : d) {
        long milliSeconds = Long.parseLong(String.valueOf(df.parse((String) resultMap1.get("timestamp")).getTime()));
        Assertions.assertTrue(mostRecentTimestamp >= milliSeconds);
    }

        Assertions.assertEquals(5,mostRecentTimestampItem.size());
        System.out.println("Most recent price for " + name + " = bid: " + mostRecentTimestampItem.get("bid") + " ask: " +
            mostRecentTimestampItem.get("ask"));
    return mostRecentTimestampItem;

    }
}

