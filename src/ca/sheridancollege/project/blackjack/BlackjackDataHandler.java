package ca.sheridancollege.project.blackjack;

import java.io.*;

public class BlackjackDataHandler {

    private static final String FILE_PATH = "player.txt";

    public boolean playerFileExists() {
        File file = new File(FILE_PATH);
        return file.exists();
    }

    public LoadedPlayer loadData() {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return null;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            br.close();

            if (line == null) return null;

            String[] parts = line.split(",");

            String name = parts[0];
            int wins = Integer.parseInt(parts[1]);
            int losses = Integer.parseInt(parts[2]);
            double balance = Double.parseDouble(parts[3]);

            BlackjackPlayerData data = new BlackjackPlayerData();
            data.setName(name);
            data.setWins(wins);
            data.setLosses(losses);

            BlackjackBank bank = new BlackjackBank();
            bank.setBalance(balance);

            return new LoadedPlayer(data, bank);

        } catch (IOException e) {
            System.out.println("Error loading player data.");
            return null;
        }
    }

    public void createNewPlayerFile(String name) {
        BlackjackPlayerData data = new BlackjackPlayerData();
        data.setName(name);
        data.setWins(0);
        data.setLosses(0);
        BlackjackBank bank = new BlackjackBank();

        saveData(data, bank);
    }

    public void saveData(BlackjackPlayerData data, BlackjackBank bank) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH));
            bw.write(
                    data.getName() + "," +
                            data.getWins() + "," +
                            data.getLosses() + "," +
                            bank.getBalance()
            );
            bw.close();

        } catch (IOException e) {
            System.out.println("Error saving player data.");
        }
    }

    public static class LoadedPlayer {
        public BlackjackPlayerData data;
        public BlackjackBank bank;

        public LoadedPlayer(BlackjackPlayerData d, BlackjackBank b) {
            this.data = d;
            this.bank = b;
        }
    }
}
