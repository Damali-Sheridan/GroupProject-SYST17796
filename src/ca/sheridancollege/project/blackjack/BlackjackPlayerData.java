package ca.sheridancollege.project.blackjack;

public class BlackjackPlayerData {

	private String name;
	private int wins;
	private int losses;

	public int getWins() {
		return this.wins;
	}

	/**
	 * 
	 * @param wins
	 */
	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getLosses() {
		return this.losses;
	}

	/**
	 * 
	 * @param losses
	 */
	public void setLosses(int losses) {
		this.losses = losses;
	}

}