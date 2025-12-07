package ca.sheridancollege.project.blackjack;

import ca.sheridancollege.project.Player;

import java.util.Scanner;

public class BlackjackGame {

    Scanner inp = new Scanner(System.in);

    private BlackjackPlayer player;
    private BlackjackDealer dealer;
    private BlackjackDeck deck;
    private double currentBet;

    public void run() {


        BlackjackDataHandler dataHandler = new BlackjackDataHandler();

        System.out.println("Checking for player data...");

        if (dataHandler.playerFileExists()) { // Check if player data exists
            System.out.println("PLayer data found! Loading...");
            BlackjackDataHandler.LoadedPlayer loadedPlayer = dataHandler.loadData();

            if (loadedPlayer == null) {
                System.out.println("There was in an error in loading your data");
                System.out.println("Starting over..");

                createNewPlayer(dataHandler);
            }
            else {
                createGameObjects(loadedPlayer.getData().getName(), loadedPlayer.getBank());
            }
        }
        else {
            System.out.println("You must be new here. Welcome to Blackjack!");
            createNewPlayer(dataHandler);

        }


        System.out.println("Welcome to Blackjack, " + player.getName());


        String menu = """
                Menu\n
                1. Play
                2. View Stats
                3. Exit
               Please select an option: 
                """;
        String choice = "999";

        do {
            System.out.print(menu);
            choice = inp.nextLine();

            switch(choice) {
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
                    continue;
            }

        } while (!choice.equals("3"));

    }

    private void createGameObjects(String playerName, BlackjackBank bank) {
        this.player = new BlackjackPlayer(playerName, bank);
        this.dealer = new BlackjackDealer("Mark the Dealer");
        this.deck = new BlackjackDeck();
    }

    private void createNewPlayer(BlackjackDataHandler dataHandler) {
        System.out.println("Please enter a username here to get started: ");
        String username = inp.nextLine();

        System.out.println("Creating player save file...");
        dataHandler.createNewPlayerFile(username);
        BlackjackDataHandler.LoadedPlayer loadedPlayer = dataHandler.loadData();

        createGameObjects(loadedPlayer.getData().getName(), loadedPlayer.getBank());

        System.out.println("Success!");
    }

    private void playRound() {

    }


    private void viewStats() {

    }


}