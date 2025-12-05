package ca.sheridancollege.project.blackjack;
import ca.sheridancollege.project.GroupOfCards;
import ca.sheridancollege.project.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlackjackDeck extends GroupOfCards {

    private List<BlackjackCard> cards;

	public BlackjackDeck() {
        super(52);
        newDeck();
        shuffle();
	}

    @Override
	public void shuffle() {
        Collections.shuffle(cards);
	}

	public BlackjackCard dealCard(Player player) {
        if (cards.isEmpty()) {
            newDeck();
            shuffle();
        }

        return cards.removeFirst();
	}

	public void newDeck() {
        cards = new ArrayList<>();

        for (BlackjackCard.Suit suit : BlackjackCard.Suit.values()) {
            for(BlackjackCard.Value value : BlackjackCard.Value.values()) {
                cards.add(new BlackjackCard(value, suit));
            }

        }
	}

}