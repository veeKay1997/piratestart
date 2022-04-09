package nl.hva.miw.pirate_bank_setup.service;

import com.github.javafaker.Faker;
import nl.hva.miw.pirate_bank_setup.model.*;
import nl.hva.miw.pirate_bank_setup.repository.*;
import org.iban4j.CountryCode;
import org.iban4j.Iban;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.*;

import com.github.rkumsher.date.RandomDateUtils;
import org.springframework.stereotype.Service;

@Service
public class CreateUsersAndCustomersService {
    
    private final UserDAO userDAO;
    private final CustomerDAO customerDAO;
    Faker faker = new Faker(new Locale("nl"));
    private final BcryptHashService bcrypt;
    public static List<Asset> cryptoCurrencies = new ArrayList<>();
    private final WalletDAO walletDAO;
    private final AccountDAO accountDAO;
    private final WalletHistoryDAO walletHistoryDAO;
    private final OrderDAO orderDAO;
    private BigDecimal startAmountBuy = BigDecimal.valueOf(1000);
    private BigDecimal startAmountSell = BigDecimal.valueOf(300);
    private AssetRateDAO assetRate;


    public CreateUsersAndCustomersService(UserDAO userDAO, CustomerDAO customerDAO, BcryptHashService bcrypt,
                                          WalletDAO walletDAO, AccountDAO accountDAO, WalletHistoryDAO walletHistoryDAO, OrderDAO orderDAO,
                                            AssetRateDAO assetRate) {
        this.userDAO = userDAO;
        this.customerDAO = customerDAO;
        this.bcrypt = bcrypt;
        this.walletDAO = walletDAO;
        this.accountDAO = accountDAO;
        this.walletHistoryDAO = walletHistoryDAO;
        this.orderDAO = orderDAO;
        this.assetRate = assetRate;
    }


    public void createOtherUsersAndAttributes(){
        for (int i = 0; i < 3000 ; i++) {

            //user aanmaken
            PersonalDetails personalDetails = new PersonalDetails(faker.name().firstName(), null, faker.name().lastName());
            String email = personalDetails.getFirstName().toLowerCase()+ "."+ personalDetails.getLastName().toLowerCase()+ randomInt(0,9)
                    + randomInt(0,9)+ randomInt(0,9)+ randomInt(0,9)+ randomInt(0,9)+ randomInt(0,9)+ randomInt(0,9)+ randomInt(0,9)
                    + randomInt(0,9)+ randomInt(0,9)+ randomInt(0,9)+ randomInt(0,9)+ "@" + "piratebank.com";
            User user = new User(email, bcrypt.hash("123456789"));
            userDAO.create(user);

            //customer aanmaken
            String postcode = faker.address().zipCode();
            String spaceReplaced = postcode.replace(" ", "");
            Address address =  new Address(faker.address().streetName(), faker.address().streetAddressNumber(), "", spaceReplaced, faker.address().cityName());
            IdentifyingInformation identifyingInformation = new IdentifyingInformation(
                    bsn(), Iban.random(CountryCode.NL).toString(), randomBirthday());
            Customer customer = new Customer(personalDetails, address, identifyingInformation);
            customer.setUserName(email);
            customer.setUserId(userDAO.getByUsername(customer.getUserName()).getUserId());
            customerDAO.create(customer);

            //wallet maken
            loadCryptoCurrencies();
            Wallet customerWallet = createCustomerWallet(cryptoCurrencies);
            customerWallet.setCustomer(customer);
            walletDAO.create(customerWallet);

            //account maken
            Account account = new Account(customer, BigDecimal.valueOf(randomInt(150000,800000)));
            accountDAO.create(account);

            //wallethistory maken
            WalletHistory customerWalletHistory = generateWalletHistory(customer);
            walletHistoryDAO.create(customerWalletHistory);

            //een buy of sell order plaatsen namens deze customer
            boolean buy = false;
            int buyOrSell = randomInt(0,1);
            if (buyOrSell == 1) {
                buy = true;
            }

            Asset asset = cryptoCurrencies.get(randomInt(0,19));

            if (buy == false) {
                orderDAO.update(new Order(buy, customer, asset, startAmountSell,
                        getCurrentRate(asset.getName()).multiply(BigDecimal.valueOf(randomInRange(1.02, 1.30))).round(new MathContext(4))));
                startAmountSell = startAmountSell.add(BigDecimal.valueOf(randomInt(10,20)));
            }

            if (buy) {
                orderDAO.update(new Order(buy, customer, asset, startAmountBuy,
                        getCurrentRate(asset.getName()).multiply(BigDecimal.valueOf(randomInRange(0.98, 0.80))).round(new MathContext(4))));
               startAmountBuy = startAmountBuy.add(BigDecimal.valueOf(randomInt(10,20)));
            }

            System.out.println("total cycles completed " + i);
        }

    }

    public LocalDate randomBirthday(){
        LocalDate today = LocalDate.now();
        return RandomDateUtils.randomLocalDateBefore(today.minusYears(30));
    }


    public static int randomInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public int bsn() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            stringBuilder.append(faker.number().numberBetween(0,9));
        }
        return Integer.parseInt(stringBuilder.toString());
    }

    public Wallet createCustomerWallet (List<Asset> crypto) {
        Wallet bankWallet = new Wallet();
        Map<Asset, BigDecimal> map = new HashMap<>();
        for (Asset asset: crypto) {
            map.put(asset, BigDecimal.valueOf(randomInt(0,1000)) );
        }
        bankWallet.setAssetsInWallet(map);
        return bankWallet;
    }


    public WalletHistory generateWalletHistory(Customer customer) {
        Map<Timestamp, BigDecimal> map = new TreeMap<>();
        for (int i = 0; i < 365; i++) {
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now().minusDays(366).plusDays(i));
            map.put(timestamp, new BigDecimal(randomInt(100000, 300000)).multiply(BigDecimal.valueOf(i)));
        }
        WalletHistory walletHistory = new WalletHistory(customer, map);
       return walletHistory;
    }


    public void loadCryptoCurrencies() {
        cryptoCurrencies.add(new Asset("bitcoin"));
        cryptoCurrencies.add(new Asset("ethereum"));
        cryptoCurrencies.add(new Asset("ripple"));
        cryptoCurrencies.add(new Asset("eos"));
        cryptoCurrencies.add(new Asset("cardano"));
        cryptoCurrencies.add(new Asset("solana"));
        cryptoCurrencies.add(new Asset("avalanche-2"));
        cryptoCurrencies.add(new Asset("polkadot"));
        cryptoCurrencies.add(new Asset("dogecoin"));
        cryptoCurrencies.add(new Asset("monero"));
        cryptoCurrencies.add(new Asset("matic-network"));
        cryptoCurrencies.add(new Asset("crypto-com-chain"));
        cryptoCurrencies.add(new Asset("cosmos"));
        cryptoCurrencies.add(new Asset("litecoin"));
        cryptoCurrencies.add(new Asset("near"));
        cryptoCurrencies.add(new Asset("chainlink"));
        cryptoCurrencies.add(new Asset("uniswap"));
        cryptoCurrencies.add(new Asset("ftx-token"));
        cryptoCurrencies.add(new Asset("algorand"));
        cryptoCurrencies.add(new Asset("tron"));
    }

    public BigDecimal getCurrentRate(String coinname){
        AssetRate assetR = assetRate.get(coinname);
        return assetR.getValue();
    }

    public static double randomInRange(double min, double max) {
        Random random = new Random();
        double range = max - min;
        double scaled = random.nextDouble() * range;
        double shifted = scaled + min;
        return shifted; // == (rand.nextDouble() * (max-min)) + min;
    }

}
