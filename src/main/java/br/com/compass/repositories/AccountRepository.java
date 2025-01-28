package br.com.compass.repositories;

import br.com.compass.models.Account;

import java.sql.*;
import java.util.Optional;

public class AccountRepository {
    private Connection connection;

    public AccountRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Account account) {
        String getUserIdSql = "SELECT id FROM users WHERE cpf = ?";
        String insertAccountSql = "INSERT INTO accounts (account_number, account_type, balance, user_id) VALUES (?, ?, ?, ?)";
        
        try (
            PreparedStatement getUserIdStmt = connection.prepareStatement(getUserIdSql);
            PreparedStatement insertAccountStmt = connection.prepareStatement(insertAccountSql)
        ) {
            getUserIdStmt.setString(1, account.getUserCpf());
            ResultSet rs = getUserIdStmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("id");
                
                insertAccountStmt.setLong(1, account.getAccountNumber());
                insertAccountStmt.setString(2, account.getAccountType());
                insertAccountStmt.setBigDecimal(3, account.getBalance());
                insertAccountStmt.setInt(4, userId);
                insertAccountStmt.executeUpdate();
            } else {
                throw new IllegalArgumentException("User not found for CPF: " + account.getUserCpf());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Optional<Account> findByUserCpf(String cpf) {
        String sql = "SELECT * FROM accounts a JOIN users u ON a.user_id = u.id WHERE u.cpf = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapAccount(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Account> findByAccountNumber(Long accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapAccount(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Account mapAccount(ResultSet rs) throws SQLException {
        String userCpf = getCpfFromUserId(rs.getInt("user_id"));
        return new Account(
            rs.getLong("account_number"),
            rs.getString("account_type"),
            rs.getBigDecimal("balance"),
            userCpf
        );
    }
    
    private String getCpfFromUserId(int userId) {
        String sql = "SELECT cpf FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("cpf");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("CPF not found for user ID: " + userId);
    }

    public void updateAccountBalance(Account account) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, account.getBalance());
            stmt.setLong(2, account.getAccountNumber());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
