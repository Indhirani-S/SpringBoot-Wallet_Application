package com.example.mywalletapp;


import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users,Integer>{
    Users findByEmail(String email);


}
