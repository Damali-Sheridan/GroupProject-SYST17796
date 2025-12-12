package ca.sheridancollege.project.blackjack;

import java.util.ArrayList;
import java.util.List;

public class BlackjackHand {

	private List<BlackjackCard> cards = new ArrayList<BlackjackCard>();
	private int handTotal;

	public void addCard(BlackjackCard card) {
		cards.add(card);
        updateHandTotal();
	}

    public boolean isBust() {
        return handTotal > 21;
    }

    public boolean is21() {
        return handTotal == 21;
    }

	public void reset() {
		handTotal = 0;
        cards.clear();
	}

    public void updateHandTotal() {
        handTotal = 0;
        int numAces = 0;

        for (BlackjackCard c : cards) {

            handTotal += c.getNumValue();

            if (c.getValue() == BlackjackCard.Value.ACE) {
                numAces += 1;
            }
        }

        while (handTotal > 21 && numAces > 0) {
            numAces -= 1;
            handTotal -= 10;
        }

    }

    public List<BlackjackCard> getCards() {
        return cards;
    }

	public int getHandTotal() {
		return this.handTotal;
	}

    public String toString() {
        String handString = "";

        for (BlackjackCard c : cards) {
            handString += c + "\n";
        }

        return handString;
    }

}