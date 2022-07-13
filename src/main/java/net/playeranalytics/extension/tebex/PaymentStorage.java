package net.playeranalytics.extension.tebex;

import com.djrapitops.plan.query.QueryService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaymentStorage {

    private final QueryService queryService;

    public PaymentStorage() {
        queryService = QueryService.getInstance();
        createTable();
        queryService.subscribeDataClearEvent(this::recreateTable);
    }

    private void createTable() {
        String dbType = queryService.getDBType();
        boolean sqlite = dbType.equalsIgnoreCase("SQLITE");

        String sql = "CREATE TABLE IF NOT EXISTS plan_tebex_payments (" +
                "id int " + (sqlite ? "PRIMARY KEY" : "NOT NULL AUTO_INCREMENT") + ',' +
                "tebex_id bigint NOT NULL UNIQUE," +
                "player_name varchar(256) NOT NULL," +
                "uuid varchar(36) NOT NULL," +
                "date bigint NOT NULL," +
                "amount double NOT NULL," +
                "currency_iso_4217 varchar(50) NOT NULL," +
                "packages varchar(256) NOT NULL" +
                (sqlite ? "" : ",PRIMARY KEY (id)") +
                ')';

        queryService.execute(sql, PreparedStatement::execute);
    }

    private void dropTable() {
        queryService.execute("DROP TABLE IF EXISTS plan_tebex_payments", PreparedStatement::execute);
    }

    private void recreateTable() {
        dropTable();
        createTable();
    }

    public int fetchLargestPaymentId() {
        return queryService.query("SELECT COALESCE(MAX(tebex_id), 0) as max_id FROM plan_tebex_payments", statement -> {
            try (ResultSet set = statement.executeQuery()) {
                return set.next() ? set.getInt("max_id") : 0;
            }
        });
    }

    public void storePayments(List<StoredPayment> toStore) {
        String insertStatement = "INSERT INTO plan_tebex_payments " +
                "(tebex_id, player_name, uuid, date, amount, currency_iso_4217, packages)" +
                "VALUES " +
                "(?, ?, ?, ?, ?, ?, ?)";

        queryService.execute(insertStatement, statement -> {
            for (StoredPayment payment : toStore) {
                statement.setLong(1, payment.getTebexId());
                statement.setString(2, payment.getTruncatedPlayerName(256));
                statement.setString(3, payment.getUuid().toString());
                statement.setLong(4, payment.getDate());
                statement.setDouble(5, payment.getAmount());
                statement.setString(6, payment.getCurrency());
                statement.setString(7, payment.getTruncatedPackages(256));
                statement.addBatch();
            }
            statement.executeBatch();
        });
    }

    public List<StoredPayment> fetchPayments() {
        String sql = "SELECT * FROM plan_tebex_payments";
        return queryService.query(sql, statement -> {
            try (ResultSet set = statement.executeQuery()) {
                List<StoredPayment> payments = new ArrayList<>();
                while (set.next()) {
                    long tebexId = set.getLong("tebex_id");
                    double amount = set.getDouble("amount");
                    String currency = set.getString("currency_iso_4217");
                    UUID uuid = UUID.fromString(set.getString("uuid"));
                    String playerName = set.getString("player_name");
                    long date = set.getLong("date");
                    String packages = set.getString("packages");

                    payments.add(new StoredPayment(tebexId, amount, currency, uuid, playerName, date, packages));
                }
                return payments;
            }
        });
    }

    public List<StoredPayment> fetchPayments(UUID playerUUID) {
        String sql = "SELECT * FROM plan_tebex_payments WHERE uuid=?";
        return queryService.query(sql, statement -> {
            statement.setString(1, playerUUID.toString());

            try (ResultSet set = statement.executeQuery()) {
                List<StoredPayment> payments = new ArrayList<>();
                while (set.next()) {
                    long tebexId = set.getLong("tebex_id");
                    double amount = set.getDouble("amount");
                    String currency = set.getString("currency_iso_4217");
                    UUID uuid = UUID.fromString(set.getString("uuid"));
                    String playerName = set.getString("player_name");
                    long date = set.getLong("date");
                    String packages = set.getString("packages");

                    payments.add(new StoredPayment(tebexId, amount, currency, uuid, playerName, date, packages));
                }
                return payments;
            }
        });
    }
}