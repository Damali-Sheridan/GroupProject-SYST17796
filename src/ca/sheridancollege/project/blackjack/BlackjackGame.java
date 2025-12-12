package ca.sheridancollege.project.blackjack;

import java.util.List;
import java.util.Scanner;

public class BlackjackGame {

    public static void main(String[] args) {
        BlackjackGame game = new BlackjackGame();
        game.run();
    }

    // Declaring/Initializing objects needed for the game
    Scanner inp = new Scanner(System.in);

    private BlackjackPlayer player;
    private BlackjackDealer dealer;
    private BlackjackDeck deck;

    private double currentBet = 0;
    private double insuranceBet = 0;
    private double splitBet = 0;

    private final double normalWinMulti = 2.0;
    private final double bjWinMulti = 2.5;

    private final BlackjackDataHandler dataHandler = new BlackjackDataHandler();
    private BlackjackPlayerData playerData;

    // Main Logic
    public void run() {

        System.out.println("Checking for player data...");
        pause();

        // Check if player data exists
        if (dataHandler.playerFileExists()) {
            System.out.println("Player data found! Loading...");
            BlackjackDataHandler.LoadedPlayer loadedPlayer = dataHandler.loadData(); // Load Player

            if (loadedPlayer == null) { // Player not loaded
                System.out.println("There was an error loading your data.");
                System.out.println("Starting over...");
                createNewPlayer();
            }
            else { // Player loaded
                createGameObjects(loadedPlayer.getData().getName(), loadedPlayer.getBank());
                this.playerData = loadedPlayer.getData();
            }
        }
        else { // Player is new
            System.out.println("You must be new here. Welcome to Blackjack!");
            createNewPlayer();
        }

        System.out.println("Welcome to Blackjack, " + player.getName());

        String menu = """
              Main Menu

                1. Play
                2. View Stats
                3. Exit
               Please select an option:\s
               \s""";

        String choice = "999";

        // Main menu
        do {
            System.out.print(menu);
            choice = inp.nextLine();

            switch (choice) {
                case "1":
                    playRound();
                    break;
                case "2":
                    viewStats();
                    break;
                case "3":
                    System.out.println("Thanks for playing. Bye!");
                    break;
                default:
                    System.out.println("Invalid option, please enter a number from 1-3");
            }

        } while (!choice.equals("3"));

    }

    // Create objects needed for the game [ deck, player, dealer ]
    private void createGameObjects(String playerName, BlackjackBank bank) {
        this.player = new BlackjackPlayer(playerName, bank);
        this.dealer = new BlackjackDealer("Mark");
        this.deck = new BlackjackDeck();
    }

    // Create new player [ creates player file for player data, and
    // loads the player for the game ]
    private void createNewPlayer() {
        System.out.print("Please enter a username to get started: ");
        String username = inp.nextLine();

        System.out.println("Creating player save file...");
        dataHandler.createNewPlayerFile(username);
        BlackjackDataHandler.LoadedPlayer loadedPlayer = dataHandler.loadData();

        createGameObjects(loadedPlayer.getData().getName(), loadedPlayer.getBank());
        this.playerData = loadedPlayer.getData();

        System.out.println("Success!");
    }

    private void playRound() {
        BlackjackBank playerBank = player.getBank();

        // Checks if the player's balance is 0 and adds a
        // free $100 for game continuity
        if (playerBank.getBalance() == 0) {
            System.out.println("Your current balance is $0. Adding a complimentary $100.");
            playerBank.deposit(100);
        }

        // Prompt player to enter bet or go back to main menu
        if (!takeBet(playerBank)) {
            return;
        }

        // Reset everything before the round starts
        player.resetHand();
        dealer.resetHand();
        insuranceBet = 0;
        splitBet = 0;
        boolean hasSplit = false;

        BlackjackHand playerHand = player.getHand();
        BlackjackHand dealerHand = dealer.getHand();

        // Shuffle deck
        deck.shuffle();

        System.out.println("\nDealing cards...");
        pause();

        // Handing out cards to player
        BlackjackCard plrCard1 = deck.dealCard();
        BlackjackCard plrCard2 = deck.dealCard();
        playerHand.addCard(plrCard1);
        playerHand.addCard(plrCard2);

        // Handing out cards to dealer
        BlackjackCard dlrCardUp = deck.dealCard();
        BlackjackCard dlrCardDown = deck.dealCard();
        dealerHand.addCard(dlrCardUp);
        dealer.addHiddenCard(dlrCardDown);

        // Display cards
        System.out.println("Your Hand: " + playerHand + "  (Total: " + playerHand.getHandTotal() + ")");
        System.out.println("Dealer Hand: " + dlrCardUp + "  +  [Hidden Card]");
        pause();

        // Check for Blackjack
        if (playerHand.is21()) {
            double winnings = currentBet * bjWinMulti;
            System.out.println("Blackjack! You win $" + winnings);
            pause();
            playerBank.deposit(winnings); // Add winnings to player's account

            // Updating stats
            playerData.setWins(playerData.getWins() + 1);
            dataHandler.saveData(playerData, playerBank);
            return;
        }

        // Insurance Logic
        boolean dealerHasAce = dlrCardUp.getValue() == BlackjackCard.Value.ACE;

        // If the Dealer has an ace, insurance is offered
        if (dealerHasAce) {
            boolean dealerHasBj = handleInsurance(playerBank, dlrCardUp, dlrCardDown); // Handle insurance returns true or false

            if (dealerHasBj) {
                dataHandler.saveData(playerData, playerBank);
                return; // Round ends
            }
        }

        // Split Logic
        BlackjackHand splitHand = null;

        if (canSplit(playerHand)) {
            hasSplit = handleSplit(playerBank, playerHand);

            if (hasSplit) { // If the player splits, it creates a split hand and adds cards to both hands
                splitHand = createSplitHand(playerHand);

                // Deal one extra card to each hand
                playerHand.addCard(deck.dealCard());
                splitHand.addCard(deck.dealCard());

                System.out.println("\nYou chose to split.");
                pause();
                System.out.println("First Hand: " + playerHand + "  (Total: " + playerHand.getHandTotal() + ")");
                System.out.println("Second Hand: " + splitHand + "  (Total: " + splitHand.getHandTotal() + ")");

                if (playerHand.is21() && splitHand.is21()) {
                    System.out.println("Both of your hands are 21! Standing..");
                }
                else if (playerHand.is21()) {
                    System.out.println("Your first hand is 21! Standing..");
                }
                else if (splitHand.is21()) {
                    System.out.println("Your second hand is 21! Standing..");
                }
            }
        }

        // Play first hand
        boolean firstHandBusted = playPlayerHand(playerHand, true, false);

        // Play split hand if it exists
        boolean secondHandBusted = false;
        if (hasSplit && splitHand != null) {
            secondHandBusted = playPlayerHand(splitHand, true, true);
        }

        // Checks if any hand is still in play
        boolean anyHandAlive = !firstHandBusted || (hasSplit && !secondHandBusted);

        if (!anyHandAlive) {
            System.out.println("\nAll your hands busted. Dealer does not need to play.");

            // Loss already updated during player turn
            dataHandler.saveData(playerData, playerBank);
            return;
        }

        System.out.println("\nNow it's the Dealer's turn");
        pause();
        System.out.println("Hidden card: " + dlrCardDown);
        dealer.revealHiddenCard();
        System.out.println("Dealer's Hand: " + dealerHand + "  (Total: " + dealerHand.getHandTotal() + ")");

        // Checks if the dealer has a Blackjack
        if (dealerHand.is21()) {
            System.out.println("Dealer has 21.");
            handleDealerBlackjack(playerBank, playerHand, splitHand, hasSplit);
            dataHandler.saveData(playerData, playerBank);
            return;
        }

        // Dealer hits on 16 or below, stands on 17+
        while (dealerHand.getHandTotal() <= 16) {
            BlackjackCard dlrHitCard = deck.dealCard();
            dealerHand.addCard(dlrHitCard);
            System.out.println("Dealer draws: " + dlrHitCard + "  (Total: " + dealerHand.getHandTotal() + ")");
        }

        if (dealerHand.isBust()) {
            // Dealer busts. Every hand that is not already a bust wins
            System.out.println("Dealer busts!");
            pause();

            boolean playerWon = false;

            if (!playerHand.isBust()) {  // Checks if player's hand is a bust
                double winnings = currentBet * normalWinMulti;
                System.out.println("Your first hand wins $" + winnings);
                playerBank.deposit(winnings); // Adds funds to player account
                playerWon = true;
            }

            if (hasSplit && !splitHand.isBust()) { // Checks if player's hand is a bust
                double winnings = splitBet * normalWinMulti;
                System.out.println("Your second hand wins $" + winnings);
                playerBank.deposit(winnings); // Adds funds to player account
                playerWon = true;
            }

            if (playerWon) {
                playerData.setWins(playerData.getWins() + 1); // Update player data
            }

            dataHandler.saveData(playerData, playerBank);
            return;
        }

        // Compare totals vs dealer
        int dealerTotal = dealerHand.getHandTotal();
        int playerWins = 0;
        int playerLosses = 0;

        if (!playerHand.isBust()) {
            int playerTotal = playerHand.getHandTotal();
            if (playerTotal > dealerTotal) {
                double winnings = currentBet * normalWinMulti;
                System.out.println("\nYour first hand beats the dealer. You win $" + winnings);
                playerBank.deposit(winnings);
                playerWins++;
            }
            else if (playerTotal < dealerTotal) {
                System.out.println("\nDealer beats your first hand. You lose $" + currentBet);
                playerLosses++;
            }
            else {
                System.out.println("\nFirst hand is a push. You get your bet back.");
                playerBank.deposit(currentBet);
            }
        }

        if (hasSplit && !splitHand.isBust()) {
            int splitTotal = splitHand.getHandTotal();
            if (splitTotal > dealerTotal) {
                double winnings = splitBet * normalWinMulti;
                System.out.println("Your second hand beats the dealer. You win $" + winnings);
                playerBank.deposit(winnings);
                playerWins++;
            }
            else if (splitTotal < dealerTotal) {
                System.out.println("Dealer beats your second hand. You lose $" + splitBet);
                playerBank.withdraw(splitBet);
                playerLosses++;
            }
            else {
                System.out.println("Second hand is a push. You get your bet back.");
                playerBank.deposit(splitBet);
            }
        }

        // Updating player data
        if (playerData != null) {
            if (playerWins > 0 && playerLosses == 0) {
                playerData.setWins(playerData.getWins() + 1);
            }
            else if (playerLosses > 0 && playerWins == 0) {
                playerData.setLosses(playerData.getLosses() + 1);
            }

            dataHandler.saveData(playerData, playerBank);
        }

        System.out.println("\nRound over. Your new balance is: $" + playerBank.getBalance());
        pause();

        // Reset hands at end of round
        player.resetHand();
        dealer.resetHand();
    }

    // Allows player to enter their bet
    private boolean takeBet(BlackjackBank playerBank) {
        while (true) {
            System.out.println("\nCurrent balance: $" + playerBank.getBalance());
            System.out.println("Please enter your bet amount (or -999 to go back to the main menu)");
            System.out.print("Bet amount: ");
            String betInp = inp.nextLine();

            if (betInp.equals("-999")) {
                return false;
            }

            try {
                double bet = Double.parseDouble(betInp);

                // Check if bet is valid
                if (bet > 0 && bet <= playerBank.getBalance()) {
                    boolean betIsValid = player.placeBet(bet);
                    if (betIsValid) {
                        currentBet = bet;
                        System.out.println("Starting round...");
                        return true;
                    }
                    else {
                        System.out.println("Error withdrawing bet from your balance. Try again.");
                    }
                }
                else {
                    System.out.println("Invalid bet. Bet must be greater than 0 and less than or equal to your balance.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Error. Bet amount must be a number.");
            }
        }
    }

    private boolean handleInsurance(BlackjackBank bank, BlackjackCard upCard, BlackjackCard downCard) {
        System.out.println("\nDealer is showing an Ace.");
        System.out.print("Do you want to take insurance? (y/n): "); // Prompt player to take insurance
        String resp = inp.nextLine().trim().toLowerCase();

        if (!resp.equals("y")) {
            System.out.println("You chose not to take insurance.");
            return false;
        }

        double maxInsurance = currentBet / 2;
        insuranceBet = 0;
        int attempts = 0;

        while (attempts < 3) { // User is only allowed 3 attempts to enter a correct amount
            System.out.print("Enter your insurance bet amount (MAX: (" + maxInsurance + "): "); // Prompt user to enter amount
            String insuranceAmt = inp.nextLine();
            attempts++;

            try {
                insuranceBet = Double.parseDouble(insuranceAmt);
            } catch (NumberFormatException e) {
                System.out.println("Error. Insurance bet must be a number.");
                continue;
            }

            if (!(insuranceBet > 0 && insuranceBet <= maxInsurance)) {
                System.out.println("Invalid insurance amount. Must be more than 0 and less than or equal to " + maxInsurance);
                continue;
            }

            if (!bank.withdraw(insuranceBet)) { // Withdraw money from player bank, if failed, the player is reprompted to enter an insurance bet.
                System.out.println("Failed to withdraw insurance bet from your balance.");
                continue;
            }

            System.out.println("Continuing...");
            break;
        }

        if (insuranceBet == 0) {
            System.out.println("Too many invalid attempts. Continuing without insurance bet.");
            return false;
        }

        if (isTwoCardBlackjack(upCard, downCard)) { // Check if dealer has blackjack
            System.out.println("Dealer has blackjack!");
            pause();

            // Paying out insurance bet
            double insuranceWinnings = insuranceBet * 2;

            System.out.println("You won $" + insuranceWinnings + " from your insurance bet.");
            System.out.println("However, you lost your main bet of $" + currentBet);

            bank.deposit(insuranceWinnings); // Win insurance bet
            bank.withdraw(currentBet); // Lose main bet

            return true; // Round ends | Dealer has blackjack
        }
        else {
            System.out.println("Dealer does not have a Blackjack. You lose your insurance bet.");
            pause();

            return false; // Go back to main bet | Dealer doesn't have blackjack
        }
    }

    private boolean canSplit(BlackjackHand hand) { // Checks if the player has two cards of the same value
        List<BlackjackCard> cards = hand.getCards();
        if (cards.size() != 2) return false;

        BlackjackCard c1 = cards.get(0);
        BlackjackCard c2 = cards.get(1);

        return c1.getNumValue() == c2.getNumValue(); // Returns true if both cards are the same value
    }

    private boolean handleSplit(BlackjackBank bank, BlackjackHand playerHand) { // Handles split logic
        System.out.print("\nYour first two cards have the same value. Do you wish to split? (y/n): ");
        String resp = inp.nextLine().trim().toLowerCase();

        if (!resp.equals("y")) {
            return false;
        }

        if (bank.getBalance() < currentBet) { // Checks if the player has enough to split
            System.out.println("Error. Insufficient balance to split");
            return false;
        }

        if (!bank.withdraw(currentBet)) { // If withdrawing the bet from the bank is not successful, it displays an error message
            System.out.println("There was an error in withdrawing the second bet from your balance. Split cancelled.");
            return false;
        }

        splitBet = currentBet; // Sets split bet

        return true;
    }

    private BlackjackHand createSplitHand(BlackjackHand originalHand) {
        List<BlackjackCard> originalCards = originalHand.getCards();
        BlackjackCard splitCard = originalCards.remove(1); // Get splitCard while also removing an extra card from the original hand

        // Update original hand total
        originalHand.updateHandTotal();

        BlackjackHand splitHand = new BlackjackHand();
        splitHand.addCard(splitCard);
        splitHand.updateHandTotal();
        return splitHand; // Return split hand
    }

    // Handles player hands
    private boolean playPlayerHand(BlackjackHand hand, boolean doubleEnabled, boolean isSplitHand) {
        double betForHand = isSplitHand ? splitBet : currentBet;
        BlackjackBank playerBank = player.getBank();

        boolean isPlayerTurn = true;

        while (isPlayerTurn && !hand.isBust()) {
            System.out.println("\nYour Hand: " + hand + "  (Total: " + hand.getHandTotal() + ")");
            System.out.println("Choose an action:\n1. Hit\n2. Stand");

            if (doubleEnabled) {
                System.out.println("3. Double");
            }

            System.out.print("Choice: ");
            String choice = inp.nextLine();

            switch (choice) {
                case "1": // Hit
                    System.out.println("You chose hit.");
                    BlackjackCard hitCard = deck.dealCard();
                    hand.addCard(hitCard);

                    System.out.println("You received a " + hitCard);
                    System.out.println("New Hand Total: " + hand.getHandTotal());

                    if (hand.isBust()) {
                        double lossAmt = isSplitHand ? splitBet : currentBet;
                        System.out.println("Bust! You lose $" + lossAmt);
                        return true;
                    }

                    if (hand.is21()) {
                        System.out.println("Your hand total is 21! Standing..");
                        isPlayerTurn = false;
                    }

                    doubleEnabled = false; // Disables double option after hitting
                    break;

                case "2": // Stand
                    System.out.println("You chose to stand.");
                    isPlayerTurn = false;
                    break;

                case "3": // Double
                    if (!doubleEnabled) {
                        System.out.println("You're not allowed to double this hand.");
                        break;
                    }

                    if (!playerBank.withdraw(betForHand)) {
                        System.out.println("Error. Insufficient balance to double.");
                        doubleEnabled = false;
                        break;
                    }

                    // Double the split bet if it's the split hand
                    if (isSplitHand) {
                        splitBet *= 2;
                    }
                    else {
                        currentBet *= 2;
                    }

                    System.out.println("Double successful!");

                    // Deal one card
                    BlackjackCard doubleCard = deck.dealCard();
                    hand.addCard(doubleCard);

                    System.out.println("You received a " + doubleCard);
                    System.out.println("New Hand Total: " + hand.getHandTotal());

                    // Player loses if they bust
                    if (hand.isBust()) {
                        double lossAmt = isSplitHand ? splitBet : currentBet;
                        System.out.println("Bust! You lose $" + lossAmt);
                        return true;
                    }

                    // Forced stand
                    isPlayerTurn = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid number.");
            }
        }

        return hand.isBust(); // Player hand busted. Returns true
    }


    // Handles the Blackjack logic for the dealer
    private void handleDealerBlackjack(BlackjackBank bank, BlackjackHand playerHand, BlackjackHand splitHand, boolean hasSplit) {
        int playerLosses = 0;

        if (!playerHand.isBust()) {
            if (playerHand.is21()) {
                System.out.println("Push. You get your money back.");
                bank.deposit(currentBet);
            }
            else {
                System.out.println("Dealer beats your first hand. You lose $" + currentBet);
                bank.withdraw(currentBet);
                playerLosses++;
            }
        }
        if (hasSplit && !splitHand.isBust()) {
            if (splitHand.is21()) {
                System.out.println("Your second hand also has 21. It's a push. You get your bet back.");
                bank.deposit(splitBet); // Refund the second-hand bet
            }
            else {
                System.out.println("Dealer beats your second hand. You lose $" + splitBet);
                bank.withdraw(splitBet);
                playerLosses++;
            }
        }

        if (playerData != null) {
            if (playerLosses > 0) {
                playerData.setLosses(playerData.getLosses() + 1);
            }
        }
    }

    private void viewStats() {
        if (playerData == null) {
            System.out.println("No statistics available.");
            return;
        }

        double balance = player.getBank().getBalance();
        System.out.println("\n--- Player Stats ---");
        System.out.println("Name: " + playerData.getName());
        System.out.println("Wins: " + playerData.getWins());
        System.out.println("Losses: " + playerData.getLosses());
        System.out.println("Current Balance: $" + balance);
        System.out.println("--------------------\n");
    }

    private boolean isTwoCardBlackjack(BlackjackCard c1, BlackjackCard c2) {
        boolean c1IsAce = c1.getValue() == BlackjackCard.Value.ACE;
        boolean c2IsAce = c2.getValue() == BlackjackCard.Value.ACE;

        boolean c1IsTen = c1.getNumValue() == 10;
        boolean c2IsTen = c2.getNumValue() == 10;

        return (c1IsAce && c2IsTen) || (c2IsAce && c1IsTen);
    }

    private void pause() {
        try {
            Thread.sleep(1300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}