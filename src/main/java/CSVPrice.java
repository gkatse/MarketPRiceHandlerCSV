
public class CSVPrice {
    private final int id;
    private final String name;
    private final float bid;
    private final float ask;
    private final String timestamp;

    public CSVPrice(int id, String name, float bid, float ask, String timestamp) {
        this.id = id;
        this.name = name;
        this.bid = bid;
        this.ask = ask;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getBid() {
        return bid;
    }

    public float getAsk() {
        return ask;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "CSVPrice{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", bid='" + bid + '\'' +
                ", ask='" + ask + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
