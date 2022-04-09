package nl.hva.miw.pirate_bank_setup.repository;

import nl.hva.miw.pirate_bank_setup.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderDAO {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final JdbcTemplate jdbcTemplate;

    public OrderDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Order> findAll() {
        String SQL = "Select * from `order`";
        List<Order> orders = new ArrayList<>();
        try {
            orders = jdbcTemplate.query(SQL, new OrderRowmapper());
        } catch (DataAccessException exception) {
            System.out.println(exception.getMessage());
            log.debug(exception.getMessage());
        }
        return orders;
    }


    public void save(Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> insertOrderStatement(order,connection),keyHolder);
            order.setOrderId(keyHolder.getKey().intValue());
        } catch (DataAccessException exception){
            System.out.println(exception.getMessage());
            log.debug(exception.getMessage());
        }
    }


    public Optional<Order> find(Integer id) {
        String SQL = "SELECT * FROM `order` WHERE orderId = ?";
        List<Order> orders = jdbcTemplate.query(SQL, new OrderRowmapper(), id);
        if (orders.size() != 1) {
            return Optional.empty();
        } else {
            return Optional.of(orders.get(0));
        }
    }


    public void update(Order order) {
        String SQL = "INSERT INTO `order` VALUES (?,?,?,?,?,?,?)";
        try {
            jdbcTemplate.update(SQL, order.getOrderId(),
                    order.isBuy(), order.getUser().getUserId(), order.getAsset().getName(), order.getAmount(),
                    order.getLimitPrice(),
                    order.getTimeOrderPlaced());
        } catch (DataAccessException exception) {
            log.info(exception.getMessage());
        }
    }


    public void delete(Integer id) {
        try {
            String SQL = "DELETE FROM `order` WHERE orderId = ?";
            jdbcTemplate.update(SQL,id);
        } catch (DataAccessException exception) {
            log.debug(exception.getMessage());
        }
    }


    private PreparedStatement insertOrderStatement(Order order, Connection connection) throws SQLException {
        System.out.println(order);

        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO `order`(buy,userId,assetName,amount,limitPrice,timeOrderPlaced) values (?,?,?,?,?,?);"
                , Statement.RETURN_GENERATED_KEYS);
        ps.setBoolean(1,order.isBuy());
        ps.setInt(2,order.getUser().getUserId());
        ps.setString(3,order.getAsset().getName());
        ps.setBigDecimal(4,order.getAmount());
        ps.setBigDecimal(5,order.getLimitPrice());
        ps.setTimestamp(6,order.getTimeOrderPlaced());
        System.out.println(ps.toString());
        return ps;
    }

    private static class OrderRowmapper implements RowMapper<Order> {

        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
           return new Order(
                    rs.getInt("orderId"),
                    rs.getBoolean("buy"),
                    rs.getBigDecimal("amount"),
                    rs.getBigDecimal("limitPrice"),
                    rs.getTimestamp("timeOrderPlaced"));
        }
    }

}
