package com.example.mywalletapp;

import org.springframework.beans.factory.annotation.Value;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Entity
public class WalletDto {


    @Id
    @GeneratedValue
    private Integer id;

    //@NotNull(message = "Name cant be null")
    @NotBlank(message = "Name cant be null, it should contain chars")
    @Pattern(regexp = "[a-zA-Z ]{3,16}", message = "Name should contain min 3 & max 16 chars , no digits and special chars allowed.")
    private String name;

    @Email(message = "Please provide valid email. e.g name@ford.com")
    private String email;
    @Pattern(regexp = "[0-9]{3}",message = "Pin code should contain 3 digits")
    private String pin;
    @Pattern(regexp = "[0-9]{10}",message = "Tel no should contain only 10 digits")
    private String phoneNumber;
    //@Future

    @FutureOrPresent(message = "Join data cant be in pas")
    //@Past
    //@PastOrPresent

    private LocalDate dateOfJoining;
    //@Value("${application.salary}")
    private Double balance;

    //private String password;
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(LocalDate dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }

    public WalletDto(Integer id, String name, String email, String pin, String phoneNumber, LocalDate dateOfJoining, Double balance) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.pin = pin;
        this.phoneNumber = phoneNumber;
        this.dateOfJoining = dateOfJoining;
        this.balance = balance;
    }

    public WalletDto() {

    }

    public WalletDto(Integer id, String name, Double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double salary) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}

