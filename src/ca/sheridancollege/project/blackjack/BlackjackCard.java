package ca.sheridancollege.project.blackjack;

import ca.sheridancollege.project.Card;

public class BlackjackCard extends Card {

	private final Value value;
	private final Suit suit;


    public BlackjackCard(Value value, Suit suit) {
        this.value = value;
        this.suit = suit;
    }

	public Value getValue() {
		return this.value;
	}
    
    public int getNumValue() {
        return value.getNumValue();
    }
    @Override
    public String toString() {
        return value + " of " + suit;
    }

    public enum Suit {
		HEARTS,
		CLUBS,
		SPADES,
		DIAMONDS
	}

	public enum Value {
		TWO(2),
		THREE(3),
		FOUR(4),
		FIVE(5),
		SIX(6),
		SEVEN(7),
		EIGHT(8),
		NINE(9),
		TEN(10),
		JACK(10),
		QUEEN(10),
		KING(10),
		ACE(11);

		private final int numValue;

        Value(int numValue) {
            this.numValue = numValue;
        }

        public int getNumValue() {
            return this.numValue;
        }

	}


}