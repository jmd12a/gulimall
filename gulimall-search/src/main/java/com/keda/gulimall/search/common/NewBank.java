package com.keda.gulimall.search.common;

import org.springframework.stereotype.Controller;

/**
 * @author Jmd
 * @create 2024-04-2024/4/18-16:51
 * @Description：
 */

public class NewBank {

    private int account_number;
    private int balance;
    private String firstname;
    private String lastname;
    private int age;
    private String gender;
    private String address;
    private String employer;
    private String email;
    private String city;
    private String state;
    public void setAccount_number(int account_number) {
        this.account_number = account_number;
    }
    public int getAccount_number() {
        return account_number;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
    public int getBalance() {
        return balance;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    public String getFirstname() {
        return firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getLastname() {
        return lastname;
    }

    public void setAge(int age) {
        this.age = age;
    }
    public int getAge() {
        return age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getGender() {
        return gender;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddress() {
        return address;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }
    public String getEmployer() {
        return employer;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public String getCity() {
        return city;
    }

    public void setState(String state) {
        this.state = state;
    }
    public String getState() {
        return state;
    }

}
