package bank;

import java.util.*;

import common.constants.Opcode;
import common.constants.Status;

public class Bank {

    // keep track of accounts created so account number can be allocated accordingly
    private static int accounts_count = 1000;

    // List of all the accounts created at runtime
    private ArrayList<Account> accounts;

    public Bank() {
        this.accounts = new ArrayList<Account>();
        ;
    }

    // ------------------Class Methods-----------------------------

    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    // create account object with given info and add it to the list of accounts
    public int createAccount(String name, String password, Currency type, double balance) {
        int account_no = generateAccountNo();
        Account acc = new Account(account_no, name, password, type, balance);
        this.accounts.add(acc);
       
        return acc.getAccountNo();
    }

    // remove account from the list of accounts if accountno matches
    public void closeAccount(int accountNo) {
        for (int i = 0; i < this.accounts.size(); i++) {

            if (this.accounts.get(i).getAccountNo() == accountNo) {
                this.accounts.remove(i);
                return;

            }
        }

    }

    private static int generateAccountNo() {

        return ++accounts_count;
    }

    public Account getAccount(int accountNo) {
        for (Account acc : accounts) {
            if (acc.getAccountNo() == accountNo) {
                return acc;
            }
        }
        return null;
    }

 

    private void printAccounts() {
        for (Account acc : accounts) {
            System.out.println(acc);
        }
    }

}
