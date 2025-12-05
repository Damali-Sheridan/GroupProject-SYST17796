package ca.sheridancollege.project.blackjack;

import ca.sheridancollege.project.Player;

public class BlackjackPlayer extends Player {
    private BlackjackHand hand;
    private BlackjackBank bank;
    private BlackjackHand splitHand;
    
    public BlackjackPlayer(String name, BlackjackBank bank) {
        super(name);
        this.bank = bank;
        this.hand = new BlackjackHand();
    }

    public BlackjackHand getHand() {
        return hand;
    }

    public BlackjackBank getBank() {
        return bank;
    }

	public boolean placeBet(double amount) {
        return bank.withdraw(amount);
	}

    public void resetHand() {
        hand.reset();
    }

    @Override
    public void play() {

    }
}