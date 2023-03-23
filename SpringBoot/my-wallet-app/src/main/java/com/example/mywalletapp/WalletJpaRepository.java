package com.example.mywalletapp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WalletJpaRepository extends JpaRepository<WalletDto, Integer> {
    List<WalletDto> findByName(String name);
    List<WalletDto> findByNameContaining(String name);
    List<WalletDto> findBySalaryBetweenOrderByNameDesc(Double minSalary,Double maxSalary);
    List<WalletDto> findBySalaryBetweenOrderByNameAsc(Double minSalary,Double maxSalary);
    List<WalletDto> findBySalaryBetweenOrderBySalaryDesc(Double minSalary,Double maxSalary);
    WalletDto createWallet(WalletDto newWallet);
    WalletDto getWalletById(Integer  walletId);
    WalletDto updateWallet(WalletDto wallet);
    WalletDto deleteWalletById(Integer walletId);
    // By writing JPQL Queries
    @Query("SELECT wallet FROM WalletDto wallet")
    List<WalletDto> getAllWallets();

    @Query("SELECT wallet FROM WalletDto wallet WHERE wallet.name LIKE :name")
    List<WalletDto> getAllWalletsHavingNameLike(String name);
}
