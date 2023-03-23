package com.example.mywalletapp;


import java.util.List;

public interface WalletService {
    WalletDto registerWallet(WalletDto newWallet);
    WalletDto getWalletById(Integer walletId)throws WalletException;
    WalletDto updateWallet(WalletDto wallet)throws WalletException;
    WalletDto deleteWalletById(Integer walletId) throws WalletException;
    List<WalletDto> getAllWallets();

    Double addFundsToWalletById(Integer walletId, Double amount)throws WalletException;
    Double withdrawFundsFromWalletById(Integer walletById,Double amount) throws WalletException;
    Boolean fundTransfer(Integer fromWalletId,Integer toWalletId,Double amount)throws WalletException;
}
