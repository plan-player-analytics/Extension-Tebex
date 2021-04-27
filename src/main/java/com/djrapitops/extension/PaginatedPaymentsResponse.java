package com.djrapitops.extension;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PaginatedPaymentsResponse {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private final int total;
    private final int per_page;
    private final int current_page;
    private final int last_page;
    private final int from;
    private final int to;
    private final List<Payment> data;

    public PaginatedPaymentsResponse(int total, int perPage, int currentPage, int lastPage, int from, int to, List<Payment> data) {
        this.total = total;
        this.per_page = perPage;
        this.current_page = currentPage;
        this.last_page = lastPage;
        this.from = from;
        this.to = to;
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public int getPerPage() {
        return per_page;
    }

    public int getCurrentPage() {
        return current_page;
    }

    public int getLastPage() {
        return last_page;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public List<Payment> getData() {
        data.sort(Comparator.comparingInt(Payment::getId).reversed());
        return data;
    }

    @Override
    public String toString() {
        return "PaginatedPaymentsResponse{" +
                "total=" + total +
                ", per_page=" + per_page +
                ", current_page=" + current_page +
                ", last_page=" + last_page +
                ", from=" + from +
                ", to=" + to +
                ", data=" + data +
                '}';
    }

    public static class Payment {
        private int id;
        private String amount;
        private String date;
        private Currency currency;
        private TebexPlayer player;
        private List<TebexPackage> packages;

        public int getId() {
            return id;
        }

        public String getAmount() {
            return amount;
        }

        public long getDate() {
            return DATE_FORMAT.parse(date, new ParsePosition(0)).getTime();
        }

        public Currency getCurrency() {
            return currency;
        }

        public TebexPlayer getPlayer() {
            return player;
        }

        public String getPackages() {
            String asString = packages.stream().map(TebexPackage::getName)
                    .sorted()
                    .collect(Collectors.toList()).toString();
            return asString.substring(1, asString.length() - 1);
        }

        @Override
        public String toString() {
            return "Payment{" +
                    "id=" + id +
                    ", amount='" + amount + '\'' +
                    ", date='" + date + '\'' +
                    ", currency=" + currency +
                    ", player=" + player +
                    ", packages=" + packages +
                    '}';
        }
    }

    public static class Currency {
        private String iso_4217;
        private String symbol;

        public String getIso4217() {
            return iso_4217;
        }

        public String getSymbol() {
            return symbol;
        }

        @Override
        public String toString() {
            return "Currency{" +
                    "iso_4217='" + iso_4217 + '\'' +
                    ", symbol='" + symbol + '\'' +
                    '}';
        }
    }

    public static class TebexPlayer {
        private String name;
        private String uuid;

        public String getName() {
            return name;
        }

        public UUID getUUID() {
            // https://stackoverflow.com/a/19399768
            // Adds the dashes to the UUID.
            return UUID.fromString(uuid.replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                    "$1-$2-$3-$4-$5"
            ));
        }

        @Override
        public String toString() {
            return "TebexPlayer{" +
                    "name='" + name + '\'' +
                    ", uuid='" + uuid + '\'' +
                    '}';
        }
    }

    public static class TebexPackage {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "TebexPackage{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
