package com.example.mywalletapp;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("auth")
@CrossOrigin(value = "http://localhost:4200/")
public class Controller {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("user")
    public String registerNewUser(@RequestBody Users user){
        // check for email already exists throw exception
        this.userRepository.save(user);
        return "User registration success.";
    }

    @PostMapping("login")
    public Users login(@RequestBody LoginDto loginDto, HttpServletResponse response) throws Exception {

        // Create a user service and log in method
        Users user = this.userRepository.findByEmail(loginDto.getEmail());
        if(user == null) throw new Exception("User does not exists");
        if(! user.getPassword().equals(loginDto.getPassword()))
            throw new Exception("User password does not match");

        // JWT util
        String issuer = loginDto.getEmail();
        Date expiry= new Date(System.currentTimeMillis() + (1000 * 60 * 60 ));
        String jwt = Jwts.builder().setIssuer(issuer).setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS256,"secretKey").compact();

        Cookie cookie = new Cookie("jwt",jwt);
        user.setJwt(jwt);
        response.addCookie(cookie);
        //return jwt;
        return user;
    }
    @PostMapping("logout")
    public String logout(HttpServletResponse response){
        Cookie cookie = new Cookie("jwt","");
        response.addCookie(cookie);
        return "Logout Success !";
    }

    @GetMapping("user")
    public Users getUser(@CookieValue("jwt") String jwt) throws Exception {
        if(jwt == null)
            throw new Exception("Unauthenticated !");
        // Jwt Util class
        Claims claim=null;
        String email=null;
        try{
            claim = Jwts.parser().setSigningKey("secretKey").parseClaimsJws(jwt).getBody();
            email = claim.getIssuer();

        }
        catch (ExpiredJwtException e){
            throw new Exception("JWT got Expired please log in again.");

        }
        catch (SignatureException e){
            throw new Exception("JWT Signature Exception.");
        }
        catch (Exception e){
            throw  new Exception("Unauthenticated !");
        }

        return this.userRepository.findByEmail(email);

    }



    @GetMapping("userinfo")
    public Users getUserInfo(@RequestHeader("Authorization") String bearerToken ) throws Exception {
        String jwt = bearerToken.substring(7);
        String email = jwtUtil.validateJwtAndGetUserEmail(jwt);
        return this.userRepository.findByEmail(email);

    }

    @Autowired
    private WalletService walletService;

    @GetMapping("/wallet/{id}")
    public WalletDto getWalletById(@PathVariable Integer id) throws WalletException{


        return walletService.getWalletById(id);
    }

    @PostMapping("/wallet") // bind incoming JSON data and parameter
    @ResponseStatus(value = HttpStatus.CREATED)
    public WalletDto addResource(@Valid @RequestBody WalletDto wallet){

        return walletService.registerWallet(wallet);
    }


    @PutMapping("/wallet") // update employee
    public WalletDto replaceResource(@Valid @RequestBody WalletDto wallet) throws WalletException {

        return walletService.updateWallet(wallet);
    }

    @DeleteMapping("/wallet/{walletId}")// URl template
    public WalletDto deleteResource(@PathVariable("walletId") Integer employeeId ) throws WalletException {
        //return "Delete !"+employeeId;
        return walletService.deleteWalletById(employeeId);
    }
    //localhost:8090/employee/1/name/India
    @PatchMapping("/wallet/{id}/name/{walletName}")// partial update od resource
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public String updateResourceName(@PathVariable("id") Integer walletId,@PathVariable("walletName") String walletName){
        return "Patch !"+walletId+":"+walletName;
    }

    private Optional<WalletDto> getWalletDto(Integer walletId) throws WalletException {
        Optional<WalletDto> walletOptional = this.walletJpaRepository.findById(walletId);
        if(walletOptional.isEmpty())
            throw new WalletException("Wallet id could not be found");
        return walletOptional;
    }


    public WalletDto addFundsToWalletById(Integer walletId, Double amount) throws WalletException {
        Optional<WalletDto> walletOptional = getWalletDto(walletId);
        WalletDto addWallet = walletOptional.get();
        Double newBalance = addWallet.getBalance() + amount;
        addWallet.setBalance(newBalance);
        return walletJpaRepository.save(addWallet);
    }


    public Double withdrawFundsFromWalletById(Integer walletById, Double amount) throws WalletException {
        Optional<WalletDto> walletDtoOptional1 = getWalletDto(walletById);
        if(walletDtoOptional1.get().getBalance()<amount){
            throw new WalletException("Insufficient Balance");
        }
        updateBalance(walletDtoOptional1.get().getBalance() - amount, walletDtoOptional1);
        return walletDtoOptional1.get().getBalance();
    }

    private void updateBalance(Double walletDtoOptional1, Optional<WalletDto> walletDtoOptional){
        Double balance = walletDtoOptional1;
        walletDtoOptional.get().setBalance(balance);
        walletJpaRepository.save(walletDtoOptional.get());
    }


    public Boolean fundTransfer(Integer fromWalletId, Integer toWalletId, Double amount) throws WalletException {

        Optional<WalletDto> walletDtoOptional1 = getWalletDto(fromWalletId);
        Optional<WalletDto> walletDtoOptional2 = getWalletDto(toWalletId);

        if(walletDtoOptional1 == walletDtoOptional2){
            throw new WalletException("Same wallet transfer cannot be permitted");
        }
        else if(walletDtoOptional1.get().getBalance()<amount){
            throw new WalletException("Insufficient Balance");
        }
        updateBalance(walletDtoOptional1.get().getBalance() - amount, walletDtoOptional1);
        updateBalance(walletDtoOptional2.get().getBalance() + amount, walletDtoOptional2);
        return true;
    }


    @GetMapping("wallets")
    public List<WalletDto> getAllWallets(){
        return this.walletService.getAllWallets();
    }

    @Autowired
    private WalletJpaRepository walletJpaRepository;
    @GetMapping("wallets/name/{name}")
    public List<WalletDto> getAllWalletsHavingName(@PathVariable("name") String name){
        return this.walletJpaRepository.findByName(name);
    }
    @GetMapping("wallets/contain/{name}")
    public List<WalletDto> getAllWalletsContainingName(@PathVariable("name") String name){
        return this.walletJpaRepository.findByNameContaining(name);
    }

    @GetMapping("wallets/salary/{minSalary}/{maxSalary}")
    public List<WalletDto> findAllWalletsHavingSalaryBetween(@PathVariable("minSalary") Double minSalary,
                                                                 @PathVariable("maxSalary")Double maxSalary){
        return this.walletJpaRepository.findBySalaryBetweenOrderBySalaryDesc(minSalary,maxSalary);

    }

    @GetMapping("custom/wallets")
    public List<WalletDto> findAllEmployees(){
        return this.walletJpaRepository.getAllWallets();
    }

    @GetMapping("custom/wallets/{name}")
    public List<WalletDto> findAllWalletsHavingName(@PathVariable("name") String name){
        return this.walletJpaRepository.getAllWalletsHavingNameLike("%"+name+"%");
    }





}




