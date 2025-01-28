package br.com.compass.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
    private final Connection connection;

    public TransactionRepository(Connection connection) {
        this.connection = connection;
    }

    public void saveTransaction(LocalDateTime timestamp, double amount, String description, Long sourceAccount, Long targetAccount) {
        String sql = "INSERT INTO transactions (timestamp, amount, description, source_account, target_account) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, timestamp);
            stmt.setDouble(2, amount);
            stmt.setString(3, description);
            stmt.setLong(4, sourceAccount);
            if (targetAccount != null) {
                stmt.setLong(5, targetAccount);
            } else {
                stmt.setNull(5, java.sql.Types.BIGINT);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getTransactionHistory(Long accountNumber) {
        String sql = "SELECT * FROM transactions WHERE source_account = ? OR target_account = ? ORDER BY timestamp DESC";
        List<String> history = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); 

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, accountNumber);
            stmt.setLong(2, accountNumber);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long sourceAccount = rs.getLong("source_account");
                Long targetAccount = rs.getLong("target_account");
                String description = rs.getString("description");
                String formattedTimestamp = rs.getTimestamp("timestamp").toLocalDateTime().format(formatter);

                if (description.equalsIgnoreCase("Deposit")) {
                    history.add(String.format(
                        "%-19s %-15s %-10.2f %-30s",
                        formattedTimestamp,
                        "Deposit",
                        rs.getDouble("amount"),
                        ""
                    ));
                } else if (description.equalsIgnoreCase("Withdrawal")) {
                    history.add(String.format(
                        "%-19s %-15s %-10.2f %-30s",
                        formattedTimestamp,
                        "Withdrawal",
                        rs.getDouble("amount"),
                        ""
                    ));
                } else if (sourceAccount.equals(accountNumber)) {
                    history.add(String.format(
                        "%-19s %-15s %-10.2f %-30s",
                        formattedTimestamp,
                        "Transferred",
                        rs.getDouble("amount"),
                        String.format("To: %d", targetAccount)
                    ));
                } else if (targetAccount.equals(accountNumber)) {
                    history.add(String.format(
                        "%-19s %-15s %-10.2f %-30s",
                        formattedTimestamp,
                        "Received",
                        rs.getDouble("amount"),
                        String.format("From: %d", sourceAccount)
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }





}
