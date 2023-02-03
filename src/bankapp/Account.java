/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankapp;

import java.io.Serializable;

/**
 *
 * @author user
 */
public abstract class Account implements Serializable {
    private double balance = 0;
    private int accountNumber;

    Account(int accountId) {
        accountNumber = accountId;
    }
    
    public abstract AccountType getAccountType();

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public int getAccountNumber() {
        return accountNumber;
    }   

    void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }
       
    @Override
    public String toString() {
        return "Account Type: " + getAccountType() + " Account\n" +
                "Account Number: " + this.getAccountNumber() + "\n" +
                "Balance: " + this.getBalance() + "\n";
    }

}
