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
        String sql = """
        SELECT t.*, 
               u_source.name AS source_name, 
               u_target.name AS target_name 
        FROM transactions t
        LEFT JOIN accounts a_source ON t.source_account = a_source.account_number
        LEFT JOIN users u_source ON a_source.user_id = u_source.id
        LEFT JOIN accounts a_target ON t.target_account = a_target.account_number
        LEFT JOIN users u_target ON a_target.user_id = u_target.id
        WHERE t.source_account = ? OR t.target_account = ?
        ORDER BY t.timestamp DESC
    """;

        List<String> history = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, accountNumber);
            stmt.setLong(2, accountNumber);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Long sourceAccount = rs.getLong("source_account");
                Long targetAccount = rs.getLong("target_account");
                String sourceName = rs.getString("source_name");
                String targetName = rs.getString("target_name");
                String description = rs.getString("description");
                String formattedTimestamp = rs.getTimestamp("timestamp").toLocalDateTime().format(formatter);
                double amount = rs.getDouble("amount");

                if ("Deposit".equalsIgnoreCase(description)) {
                    history.add(String.format(
                            "%-19s %-15s %-10.2f %-30s",
                            formattedTimestamp,
                            "Deposit",
                            amount,
                            ""
                    ));
                } else if ("Withdrawal".equalsIgnoreCase(description)) {
                    history.add(String.format(
                            "%-19s %-15s %-10.2f %-30s",
                            formattedTimestamp,
                            "Withdrawal",
                            amount,
                            ""
                    ));
                } else if (sourceAccount.equals(accountNumber)) {
                    history.add(String.format(
                            "%-19s %-15s %-10.2f To: %d - %s",
                            formattedTimestamp,
                            "Transferred",
                            amount,
                            targetAccount,
                            targetName != null ? targetName : "Unknown"
                    ));
                } else if (targetAccount.equals(accountNumber)) {
                    history.add(String.format(
                            "%-19s %-15s %-10.2f From: %d - %s",
                            formattedTimestamp,
                            "Received",
                            amount,
                            sourceAccount,
                            sourceName != null ? sourceName : "Unknown"
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }






}
