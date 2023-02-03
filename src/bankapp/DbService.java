/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class DbService {
    String url = "jdbc:mysql://localhost:3306/bankdb";
    String user = "bank";
    String password = "securepassword";
    
    private Connection connect() {
        Connection connection;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            Logger.getLogger(DbService.class.getName()).log(Level.SEVERE, null, ex);
            connection = null;
        }
        return connection;
    }
    
    int AddAccount(String firstName, String lastName, String ssn, AccountType accountType, Double balance) {
        int userId = -1;
        int accountId = -1;
        Connection connection = connect();
        try {
            connection.setAutoCommit(false);
            String addUserSql = "insrt into users(FirstName, LastName, SSN) values(?,?,?)";
            PreparedStatement addUser = connection.prepareStatement(addUserSql, Statement.RETURN_GENERATED_KEYS);
            addUser.setString(1, firstName);
            addUser.setString(2, lastName);
            addUser.setString(3, ssn);
            addUser.executeUpdate();
            ResultSet addUserResults = addUser.getGeneratedKeys();
            if(addUserResults.next()) {
                userId = addUserResults.getInt(1);
            }
            String addAccountSql = "insert into accounts(Type, Balance) values(?,?)";
            PreparedStatement addAccount = connection.prepareStatement(addAccountSql, Statement.RETURN_GENERATED_KEYS);
            addAccount.setString(1, accountType.name());
            addAccount.setDouble(2, balance);
            addAccount.executeUpdate();
            ResultSet addAccountResults = addAccount.getGeneratedKeys();
            if(addAccountResults.next()) {
                accountId = addAccountResults.getInt(1);
            }
            if(userId > 0 && accountId > 0) {
                String linkAccountSql = "insert into mappings(UserId, AccountId) values(?,?)";
                PreparedStatement linkAccount = connection.prepareStatement(linkAccountSql);
                linkAccount.setInt(1, userId);
                linkAccount.setInt(2, accountId);
                linkAccount.executeUpdate();
                connection.commit();
            }
            else {
                connection.rollback();
            }
            connection.close();
        } catch (SQLException ex) {
            System.err.println("An error has occurred: " + ex.getMessage());
        }
        return accountId;
    }    
    
    Customer GetAccount(int accountId) {
        Customer customer = null;
        Connection connection = connect();
        try {
            String findUserSql = "select FirstName, LastName, SSN, Type, Balance "
                + "from Users a join Mappings b on a.ID = UserId "
                + "join Accounts c on c.ID = b.Account"
                + " where c.ID = ?";
        PreparedStatement findUser = connection.prepareStatement(findUserSql);
        findUser.setInt(1, accountId);
        ResultSet findUserResults = findUser.executeQuery();
        if(findUserResults.next()){
            String firstName = findUserResults.getString("FirstName");
            String lastName = findUserResults.getString("LastName");
            String ssn = findUserResults.getString("SSN");
            AccountType accountType = AccountType.valueOf(findUserResults.getString("Type"));
            double balance = findUserResults.getDouble("Balance");
            Account account;
            if(accountType == AccountType.Checking) {
                account = new Checking(accountId, balance);
            }
            else if(accountType == AccountType.Saving){
                account = new Saving(accountId, balance);
            }
            else {
                throw new Exception("Unknown account type");
            }
            customer = new Customer(firstName, lastName, ssn, account);
        }
        else {
            System.err.println("No user account was found for ID " + accountId);
            }
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
        return customer;
        
    }
    
    boolean UpdateAccount(int accountId, double balance) {
        boolean success = false;
        Connection connection = connect();
        try {
            String updateSql ="UPDATE Accounts SET Balance = ? WHERE ID = ?";
            PreparedStatement updateBalance = connection.prepareStatement(updateSql);
            updateBalance.setDouble(1, balance);
            updateBalance.setInt(2, accountId);
            updateBalance.executeUpdate();
            success = true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return success;
    }
    
    boolean DeleteAccount(int accountId) {
        boolean success = false;
        Connection connection = connect();
        try {
            String deleteSql = "DELETE Users,Accounts "
                + "from Users join Mappings on Users.ID = Mappings.UserId "
                + "join Accounts on Accounts.ID = Mappings.Account "
                + " where Accounts.ID = ?";
            PreparedStatement deleteAccount = connection.prepareStatement(deleteSql);
            deleteAccount.setInt(1, accountId);
            deleteAccount.executeUpdate();
            success = true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return success;
    }
    
    ArrayList<Customer> GetAllAccounts() {
        ArrayList<Customer> customers = new ArrayList<>();
        Connection connection = connect();
        try {
        String findAllUsersSql = "select AccountId, FirstName, LastName, SSN, Type, Balance "
                + "from Users a join Mappings b on a.ID = UserId "
                + "join Accounts c on c.ID = b.Account";
        PreparedStatement findAllUsers = connection.prepareStatement(findAllUsersSql);
        ResultSet findUserResults = findAllUsers.executeQuery();
        while(findUserResults.next()){
            String firstName = findUserResults.getString("FirstName");
            String lastName = findUserResults.getString("LastName");
            String ssn = findUserResults.getString("SSN");
            AccountType accountType = AccountType.valueOf(findUserResults.getString("Type"));
            double balance = findUserResults.getDouble("Balance");
            int accountId = findUserResults.getInt("AccountId");
            Account account;
            if(accountType == AccountType.Checking) {
                account = new Checking(accountId, balance);
            }
            else if(accountType == AccountType.Saving){
                account = new Saving(accountId, balance);
            }
            else {
                throw new Exception("Unknown account type");
            }
            customers.add(new Customer(firstName, lastName, ssn, account));
        }
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
        return customers;
        
    }

}
