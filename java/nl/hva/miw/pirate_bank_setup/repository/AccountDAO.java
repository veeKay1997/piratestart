package nl.hva.miw.pirate_bank_setup.repository;

import nl.hva.miw.pirate_bank_setup.model.Account;
import nl.hva.miw.pirate_bank_setup.model.Customer;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AccountDAO  {
    private JdbcTemplate jdbcTemplate;

    public AccountDAO(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Account> getAll() {
        String sql = "SELECT * from account;";
        try {
            return jdbcTemplate.query(sql, new AccountRowMapper());
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return null;
        }
    }


    public void create(Account account) {
        String sql = "INSERT INTO account VALUES (?,?);";
        jdbcTemplate.update(sql, account.getCustomer().getUserId(), account.getBalance());
    }


    public Account get(Integer id) {
        Account account;
        try {
            String sql = "SELECT * FROM account WHERE user_id = ?;";
            account = jdbcTemplate.queryForObject(sql, new AccountRowMapper(), id);
        } catch (DataAccessException dataAccessException){
            return null;
        } return account;
    }

    public void update(Account account) {
        try {
            String sql = "UPDATE account SET balance = ? where user_id = ?;";
            jdbcTemplate.update(sql, account.getBalance(), account.getCustomer().getUserId());
        } catch (DataAccessException dataAccessException) {
            return;
        }
    }

    // this method has no exception handling. Please use exception handling or check method when calling this method

    public void delete(Integer id) {
        String sql = "DELETE FROM account Where user_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public class AccountRowMapper implements RowMapper<Account> {
        @Override
        public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
           Customer customer = new Customer();
           customer.setUserId(rs.getInt("user_id"));
           return new Account (customer, rs.getBigDecimal("balance"));
        }
    }
}
