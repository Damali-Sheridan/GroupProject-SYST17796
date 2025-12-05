package ca.sheridancollege.project.blackjack;

import ca.sheridancollege.project.Player;

public class BlackjackDealer extends Player {
    private BlackjackHand hand;
    private  BlackjackCard hiddenCard;

    public BlackjackDealer(String name) {
        super(name);
        this.hand = new BlackjackHand();
    }

    public BlackjackHand getHand() {
        return hand;
    }

    public void resetHand() {
        hand.reset();
        hiddenCard = null;
    }

    public void addHiddenCard(BlackjackCard card) {
        hiddenCard = card;
    }
    public void revealHiddenCard() {
		if (hiddenCard != null) {
            hand.addCard(hiddenCard);
            hiddenCard = null;
        }
	}


    @Override
    public void play() {

    }
}