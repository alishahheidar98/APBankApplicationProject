/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankapp;

/**
 *
 * @author user
 */
public class Checking extends Account {

    Checking(int accountId, double initialDeposit) {
        super(accountId);
        this.setBalance(initialDeposit);
    }


    @Override
    public AccountType getAccountType() {
      return AccountType.Checking;  
    }
}

