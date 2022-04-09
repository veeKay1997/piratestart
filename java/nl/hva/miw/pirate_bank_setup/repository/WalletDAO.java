package nl.hva.miw.pirate_bank_setup.repository;

import nl.hva.miw.pirate_bank_setup.model.Asset;
import nl.hva.miw.pirate_bank_setup.model.Wallet;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class WalletDAO  {
    private JdbcTemplate jdbcTemplate;

    public WalletDAO(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Wallet> getAll() {
        return null;
    }


    @Transactional
    public void create(Wallet wallet) {
        String sql = "INSERT INTO wallet VALUES (?, ?, ?);";
        List<Asset> assets = new ArrayList<>();
        List<BigDecimal> bigDecimals = new ArrayList<>();

        for (Map.Entry entry: wallet.getAssetsInWallet().entrySet()) {
            Asset asset = (Asset) entry.getKey();
            BigDecimal bigDecimal = (BigDecimal) entry.getValue();
            assets.add(asset);
            bigDecimals.add(bigDecimal);
        }

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, wallet.getCustomer().getUserId());
                ps.setString(2, assets.get(i).getName());
                ps.setBigDecimal(3, bigDecimals.get(i));
            }
            @Override
            public int getBatchSize() {
                return wallet.getAssetsInWallet().size();
            }
        });
    }

    public void addSingleAssetToWallet(int userId, String assetname, BigDecimal amount) {
        String sql = "INSERT INTO wallet VALUES (?, ?, ?);";
        jdbcTemplate.update(sql, userId, assetname, amount );
    }


    public Wallet get(Integer id) {
        String sql = "SELECT * FROM wallet WHERE user_id = ?";
        return jdbcTemplate.query(sql, new WalletResultSetExtractor(), id);
    }


    public void update(Wallet wallet) {
        String sql = "UPDATE wallet SET amount = ? WHERE user_id = ? AND name = ?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetterWalletUpdate(wallet));
    }

    //careful, deletes the whole wallet of the user

    public void delete(Integer id) {
        String sql = "DELETE FROM wallet WHERE user_id = ?";
        jdbcTemplate.update(sql, id);

    }


    public void updateWalletAfterTransaction(int userId, String nameAsset, BigDecimal amount) {
        String sql = "UPDATE wallet SET amount = ? WHERE name = ? AND user_id = ?;";
        try {
            jdbcTemplate.update(sql, amount, nameAsset, userId);
        } catch (DataAccessException dataAccessException) {
            return;
        }
    }

    private class WalletResultSetExtractor implements ResultSetExtractor<Wallet> {
        public Wallet extractData(ResultSet rs) throws SQLException, DataAccessException {
            Wallet wallet = new Wallet();
            Map<Asset, BigDecimal> map = new HashMap<>();
            if (rs.next()) {
                map.put(new Asset(rs.getString("name")), rs.getBigDecimal("amount"));
                while (rs.next()) {
                    map.put(new Asset(rs.getString("name")), rs.getBigDecimal("amount"));
                }
                wallet.setAssetsInWallet(map);
            }
            return wallet;
        }
    }

    private class BatchPreparedStatementSetterWalletUpdate implements BatchPreparedStatementSetter {
        private Wallet wallet;
        private List<Asset> assetList = new ArrayList();
        private List<BigDecimal> amountList = new ArrayList();

        public BatchPreparedStatementSetterWalletUpdate(Wallet wallet) {
            this.wallet = wallet;
            for (Map.Entry entry: wallet.getAssetsInWallet().entrySet()) {
                assetList.add((Asset) entry.getKey());
                amountList.add((BigDecimal) entry.getValue());
            }
        }

        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            ps.setBigDecimal(1 , amountList.get(i));
            ps.setInt(2, wallet.getCustomer().getUserId());
            ps.setString(3, assetList.get(i).getName());
        }

        @Override
        public int getBatchSize() {
            return wallet.getAssetsInWallet().size();
        }
    }
}