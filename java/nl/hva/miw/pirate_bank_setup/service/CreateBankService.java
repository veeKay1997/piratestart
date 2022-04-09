package nl.hva.miw.pirate_bank_setup.service;

import nl.hva.miw.pirate_bank_setup.model.Asset;
import nl.hva.miw.pirate_bank_setup.model.Customer;
import nl.hva.miw.pirate_bank_setup.model.Wallet;
import nl.hva.miw.pirate_bank_setup .repository.BankDAO;
import nl.hva.miw.pirate_bank_setup.repository.WalletDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


@Service
public class CreateBankService {

    private BankDAO bankDAO;
    private BcryptHashService bcrypt;
    private WalletDAO walletDAO;


    private static final int MIN_NUMBER=1000;
    private static final int MAX_NUMBER=5000;
    public static List<Asset> cryptoCurrencies = new ArrayList<>();


    @Autowired
    CreateBankService(BankDAO bankDAO, BcryptHashService bcryptHashService, WalletDAO walletDAO) {
        this.bankDAO = bankDAO;
        this.bcrypt = bcryptHashService;
        this.walletDAO = walletDAO;
    }

    public boolean createBank() {
        bankDAO.createBankUser(bcrypt.hash("welcomepirates01"));
        bankDAO.createBankCustomer();
        bankDAO.createBankAccount();
        loadCryptoCurrencies();
        Wallet wallet = createBankWallet(cryptoCurrencies);
        Customer bank = new Customer();
        bank.setUserId(1000);
        wallet.setCustomer(bank);
        for (Map.Entry entry: wallet.getAssetsInWallet().entrySet()) {
            Asset asset = (Asset) entry.getKey();
            BigDecimal bigDecimal = (BigDecimal) entry.getValue();
            walletDAO.addSingleAssetToWallet(wallet.getCustomer().getUserId(), asset.getName(), bigDecimal);
        }
        return true;
    }


        public static int randomInt(int min, int max) {
            Random rand = new Random();
            int randomNum = rand.nextInt((max - min) + 1) + min;
            return randomNum;
        }



    public static void loadCryptoCurrencies() {
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


    public Wallet createBankWallet (List<Asset> crypto) {
        Wallet bankWallet = new Wallet();
        Map<Asset, BigDecimal> map = new HashMap<>();
        for (Asset asset: crypto) {
            map.put(asset, BigDecimal.valueOf(randomInt(MIN_NUMBER,MAX_NUMBER)) );
        }
        bankWallet.setAssetsInWallet(map);
        return bankWallet;
    }


}


