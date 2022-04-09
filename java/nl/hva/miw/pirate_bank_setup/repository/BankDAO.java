package nl.hva.miw.pirate_bank_setup.repository;

import nl.hva.miw.pirate_bank_setup.model.*;

import nl.hva.miw.pirate_bank_setup.service.BcryptHashService;
import nl.hva.miw.pirate_bank_setup.service.PepperService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Repository
public class BankDAO {

    private JdbcTemplate jdbcTemplate;

    public BankDAO(JdbcTemplate jbdcTemplate) {
        super();
        this.jdbcTemplate = jbdcTemplate;
    }

    public void createBankUser(String hashedPassword) {
        String sql = "Insert into user(user_id, username, password) values (?,?,?)";
        jdbcTemplate.update(sql, 1000, "piratebank@piratebank.nl", hashedPassword);
    }

    public void createBankCustomer() {
        String sql = "INSERT INTO customer VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql, 1000, "Pirate", "", "Bank", "1999-01-01", "123456789", "1234XY", "1", "", "PirateStreet",
                "Pirate City", "NL67ABNA7984823820");
    }

    public void createBankAccount() {
        String sql = "Insert INTO account values (?,?)";
        jdbcTemplate.update(sql, 1000, new BigDecimal(5000000));
    }


    public void insertWallet (int id, String assetname,int amount) {
        String sql = "INSERT INTO wallet VALUES (?, ?, ?);";
        jdbcTemplate.update(sql,id,assetname, amount);
    }


    public static int randomInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }




//    public void prepareRandomUser() {
//        //createuser
//        String firstName = Names.nextFirstName();
//        String lastName = Names.nextSurname();
//
//        String userEmail = firstName+lastName+randomInt(0,3000)+"@thepiratebank.nl";
//        String hashedpassword = "$2a$10$7yFU3x8ODYNPJ3is/H0cO.B7rZe9zj/.bs3/kRkK.ytx73.wJ/u9.";
//        User user = new User(id,userEmail,hashedpassword);
//        createUser(user);
//
//        /// create customer
//        Customer customer = new Customer(id, userEmail, hashedpassword, new PersonalDetails(firstName, getRandomInfix(),lastName),
//                getRandomAdress(),
//                new IdentifyingInformation(getRandomBSN(),getRandomIBAN(),getRandomDateOfBirth()));
//
//        createCustomer(customer);
//
//
//        /// create account
//        createAccount(user);
//        // create wallet
//        createWallet(user);
//
//        id = id+1;
//    }
//
//    public String getRandomInfix() {
//        return "";
//    }
//
//
//    public int getRandomBSN() {
//        int bsn = 999999999;
//        return bsn;
//    }
//
//    public String getRandomIBAN() {
//        return "NL38INGB0008900832";
//    }
//
//    public LocalDate getRandomDateOfBirth () {
//        LocalDate localDate = LocalDate.now();
//        return localDate;
//    }
//
//    public Address getRandomAdress () {
//        String street ="";
//        String houseNumber = "";
//        String houseNumberAddition="";
//        String postalCode = "";
//        String city= "" ;
//
//        Address address = new Address(street,houseNumber,houseNumberAddition,postalCode,city);
//        return address;
//    }
//
//
//
//    public void createUser(User user) {
//        String sql = "Insert into user( user_id, username, password) values (?,?,?)";
//        jdbcTemplate.update(sql,  user.getUserId(),user.getUserName(), user.getPassword());
//    }
//
//
//    public void createCustomer(Customer customer) {
//        String sql = "INSERT INTO customer VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
//
//        jdbcTemplate.update(sql, customer.getUserId(),
//                customer.getPersonalDetails().getFirstName(),
//                customer.getPersonalDetails().getInFix(),
//                customer.getPersonalDetails().getLastName(),
//                customer.getIdentifyingInformation().getDateOfBirth(),
//                customer.getIdentifyingInformation().getBsnNumber(),
//                customer.getAddress().getPostalCode(),
//                customer.getAddress().getHouseNumber(),
//                customer.getAddress().getHouseNumberAddition(),
//                customer.getAddress().getStreet(),
//                customer.getAddress().getCity(),
//                customer.getIdentifyingInformation().getIbanNumber());
//    }
//
//    public void createAccount(User user) {
//        String sql = "Insert INTO account values (?,?)";
//        jdbcTemplate.update(sql, user.getUserId(), new BigDecimal(100000));
//    }
//
//    public void createWallet (User user) {
//        cryptoCurrencies.keySet().stream().forEach( key -> makeWallet(user.getUserId(), key, randomInt(100,500)));
//    }

    // to repeat the method 20 times trying to insert new value everytime





}

