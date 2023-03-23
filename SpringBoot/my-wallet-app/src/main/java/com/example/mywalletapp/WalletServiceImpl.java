package com.example.mywalletapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService{
    @Autowired
    private WalletJpaRepository walletJpaRepository;

    @Override
    public WalletDto registerWallet(WalletDto newWallet) {


        return this.walletJpaRepository.save(newWallet);
    }

    @Override
    public WalletDto getWalletById(Integer walletId) throws WalletException {
        Optional<WalletDto> walletOptional = this.walletJpaRepository.findById(walletId);
        if(walletOptional.isEmpty())
            throw new WalletException("Wallet could not be found id:"+walletId);

        return walletOptional.get();
    }

    @Override
    public WalletDto updateWallet(WalletDto wallet) throws WalletException{
        Optional<WalletDto> walletOptional = this.walletJpaRepository.findById(wallet.getId());
        if(walletOptional.isEmpty())
            throw new WalletException("Wallet could not be updated, not found id:"+wallet.getId());
        return this.walletJpaRepository.save(wallet);
    }

    @Override
    public WalletDto deleteWalletById(Integer walletId) throws WalletException {
        Optional<WalletDto> walletOptional = this.walletJpaRepository.findById(walletId);
        if(walletOptional.isEmpty())
            throw new WalletException("Wallet could not be Deleted, not found id:"+walletId);
        WalletDto foundWallet = walletOptional.get();
        this.walletJpaRepository.delete(foundWallet);
        return foundWallet;
    }

    @Override
    public Double addFundsToWalletById(Integer walletId, Double amount) throws WalletException {
        WalletDto wallet = this.walletJpaRepository.getWalletById(walletId);
        if(wallet == null)
            throw new WalletException("Wallet doesn't exist to add funds, id:"+ walletId);
        Double newBalance = wallet.getBalance()+amount;
        wallet.setBalance(newBalance);
        this.walletJpaRepository.updateWallet(wallet);
        return newBalance;
    }

    @Override
    public Double withdrawFundsFromWalletById(Integer walletById, Double amount) throws WalletException {
        WalletDto wallet = this.walletJpaRepository.getWalletById(walletById);
        if(wallet == null)
            throw new WalletException("Wallet does not exist to withdraw, try using valid account id");

        Double balance = wallet.getBalance();
        if (balance<amount)
            throw new WalletException("Insufficient balance, current balance: "+ balance);
        balance-=amount;
        wallet.setBalance(balance);

        this.walletJpaRepository.updateWallet(wallet);
        return balance;
    }

    @Override
    public Boolean fundTransfer(Integer fromWalletId, Integer toWalletId, Double amount) throws WalletException {
        WalletDto fromWallet = this.walletJpaRepository.getWalletById(fromWalletId);

        if(fromWallet == null)
            throw new WalletException("from Wallet does not exist, id: "+ fromWalletId);

        WalletDto toWallet = this.walletJpaRepository.getWalletById(toWalletId);
        if(toWallet == null)
            throw new WalletException("to Wallet does not exist, id: "+ toWalletId);

        Double fromBalance = fromWallet.getBalance();
        if(fromBalance<amount)
            throw new WalletException("Insufficient balance , current balance: "+ fromBalance);

        fromWallet.setBalance(fromBalance-amount);

        Double toBalance = toWallet.getBalance();
        toWallet.setBalance(toBalance+amount);

        this.walletJpaRepository.updateWallet(fromWallet);
        this.walletJpaRepository.updateWallet(toWallet);

        return true;
    }

    @Override
    public List<WalletDto> getAllWallets() {
        return this.walletJpaRepository.findAll();
    }
}

