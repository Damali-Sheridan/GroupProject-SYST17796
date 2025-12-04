package ca.sheridancollege.project.blackjack;

public class BlackjackBank {

	private double balance = 1000;

	public double getBalance() {
		return this.balance;
	}

	/**
	 * 
	 * @param balance
	 */
	public void setBalance(double balance) {
		this.balance = balance;
	}

	/**
     *
     * @param amount
     * @return
     */
	public boolean withdraw(double amount) {
		// TODO - implement Bank.withdraw
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param amount
	 */
	public void deposit(double amount) {
		// TODO - implement Bank.deposit
		throw new UnsupportedOperationException();
	}

}