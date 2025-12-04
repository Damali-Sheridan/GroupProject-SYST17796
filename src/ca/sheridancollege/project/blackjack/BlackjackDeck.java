package ca.sheridancollege.project.blackjack;

import ca.sheridancollege.project.Card;
import ca.sheridancollege.project.GroupOfCards;
import ca.sheridancollege.project.Player;

import java.util.ArrayList;

public class BlackjackDeck extends GroupOfCards {

	public BlackjackDeck() {
        super(52);
        newDeck();
	}

	public void shuffle() {

	}

	public void dealCard(Player player) {

	}

	public void newDeck() {
        ArrayList<Card> cards = new ArrayList<>();

        for (BlackjackCard.Suit suit : BlackjackCard.Suit.values()) {
            for(BlackjackCard.Value value : BlackjackCard.Value.values()) {
                cards.add(new BlackjackCard(value, suit));
            }

        }
	}

}