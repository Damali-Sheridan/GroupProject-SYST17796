package ca.sheridancollege.project.blackjack;

public class BlackjackBank {

	private double balance = 1000;

	public double getBalance() {
		return this.balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public boolean withdraw(double amount) {
        if (balance >= amount && amount > 0) {
            balance -= amount;
            return true;
        }
        else {
            return false;
        }
	}

	public void deposit(double amount) {
        balance += amount;
	}

}