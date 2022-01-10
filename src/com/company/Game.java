package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Game {
    Stack stack;
    int originCountOfPlayers;
    ArrayList<Player> players; // nacte nebo vytvori pocet hracu (nadtrida normal a mpc)
    ArrayList<String> gameNames; // pamatuje si nazvy her, aby neprepsal nejakou, co uz ma a aby mohl pozdeji odstranit (doplnime pozdeji, zatim nefunkcni) souvisi s deleteGame
    int playerIndex = 0;

    public Game(){  // prazdny konstruktor, vse se vytvari az v loadGame nebo createNewGame
        players = new ArrayList<>();
        stack = new Stack();
    }

    public void start()  {
        boolean again = true;
        while (again) {
            try {
                // nacte ze specialniho souboru, ktery se vytvori, pokud neni nalezen, list nazvu her, to se stane vzdy
                System.out.println("Zadejte prosim, co z uvedeneho byste nejradeji: ");
                System.out.println(" UKONCIT HRU      VYTVORIT NOVOU       NACIST HRU      ODSTRANIT HRU");
                System.out.println("    (0)                (1)                (2)               (3)");

                int input = getIntFromTo(0, 3);
                // switch (input)
                switch (input) {
                    case 0:        // konec hry
                        again = false;
                        break;
                    case 1:         // nova hra se vsim vsudy
                        createNewGame();
                        break;
                    case 2:         // nacte existujici hru
                        loadExistingGame();
                        break;
                    case 3:          //vymaze nejakou hru
                        deleteGame();
                        break;
                    default:
                        again = true;
                        break;
                }
                loop();

            } catch (Exception e) {
                System.out.println(e.toString());
                System.out.println("Neco se velice pokazilo, budto dosly karty anebo systemova chyba.");
                System.out.println("Znovu se presuneme se do hlavniho menu.");
            }
        }
    }

    private boolean createNewGame() throws OutOfCardsException {
        int count;
        System.out.println("Zadejte od ktere karty chcete, aby karty zacinaly (2-7, 8, 9,... K, A)");
        stack = new Stack();   // vytvoreni stacku od 2 nebo az od 7 po eso
        stack.fillYourself(getIntFromTo(2,7));
        players = new ArrayList<>();

        int countOfMpc;
        int countOfReals;

        do           // nacteni spravneho poctu hracu od 2 do 7 stejne jako karet omylem ci z logiky veci...
        {
            System.out.println("Zadejte počet opravdových hráčů, maximálně 7 celkem");
            countOfReals = getIntFromTo(0,7);

            System.out.println("Zadejte počet MPC hráčů, maximálně 7 celkem");
            countOfMpc = getIntFromTo(0,7);

            count = countOfMpc + countOfReals;
        }
        while (count < 2 || count > 7);

        this.originCountOfPlayers = count;

        for (int i = 0; i < countOfReals; i++) { // vytvorime hrace, dame mu karty a pridame do listu hracu
            RealPlayer player = new RealPlayer();
            player.drawCard(stack,4);
            player.setName("Zivy Hrac "+ (i+1));
            players.add(player);
        }

        for (int i = 0; i < countOfMpc; i++) { // vytvorime hrace, dame mu karty a pridame do listu hracu
            MpcPlayer player = new MpcPlayer();
            player.drawCard(stack,4);
            player.setName("Pocitacovy Hrac "+ (i+1));
            players.add(player);
        }

        stack.recieveCard(stack.getNewCard()); // symbolicky nakonec vezmeme kartu z dobiraciho balicku a dame ji do odkladaciho

        return false;
    } // pta se na pocet karet (od ktere karty az do esa budou v balicku)

    private boolean saveGame(){
        OutputStream outputStream = null;
        BufferedOutputStream bufferedOutput = null;
        DataOutput dataWriter = null;

        System.out.println("Zadejte nazev hry");
        String filename = getFilename(false);

        try{
            outputStream = new FileOutputStream(filename, false);
            bufferedOutput = new BufferedOutputStream(outputStream);
            dataWriter = new DataOutputStream(bufferedOutput);

            dataWriter.writeUTF(filename);                          // nazev hry pro overeni
            dataWriter.writeInt(originCountOfPlayers);              // puvodni pocet hracu
            dataWriter.writeInt(playersCount(RealPlayer.class));    // aktualni pocet realnych hracu
            dataWriter.writeInt(playersCount(MpcPlayer.class));     // aktualni pocet MPC hracu
            dataWriter.writeInt(playerIndex);                       // index hrace, ktery je na rade

            for (Player player:players                              // prenecha se dalsi ulozeni na hracich (nejdriv jsou realni, pak MPC, vzdy)
            ) {
                player.saveToFile(dataWriter);
            }

            stack.saveToFile(dataWriter);                           // balicek karet se take sam ulozi

            bufferedOutput.flush();
            outputStream.close();
        }
        catch (IOException e){
        }
        return true;
    } // ulozi svoje veci a pak rekne balicku a hracum, aby se ulozili taky

    private boolean loadExistingGame(){
        System.out.println("Zadejte nazev hry, kterou chcete nacist");
        // tady bych doplnil vycet her, teoreticky i vyber pomoci cisla zase...
        String gameName = getFilename(true);

        InputStream inputStream = null;
        BufferedInputStream bufferedInput = null;
        DataInput dataReader = null;

        try  {
            inputStream = new FileInputStream(gameName);
            bufferedInput = new BufferedInputStream(inputStream);
            dataReader = new DataInputStream(bufferedInput);

            boolean rightFile = gameName.equals(dataReader.readUTF()); // nazev hry pro pripadne overeni
            originCountOfPlayers = dataReader.readInt();               // puvodni pocet hracu
            int realPlayersCount = dataReader.readInt();               // aktualni pocet realnych hracu
            int mpcPlayersCount = dataReader.readInt();                // aktualni pocet MPC hracu
            playerIndex = dataReader.readInt();                        // index hrace, ktery je na rade

            players = new ArrayList<Player>();
            for (int i = 0; i <realPlayersCount ; i++) {    // nactou se realni hraci a pridaji do listu hracu
                RealPlayer player = new RealPlayer();
                player.loadFromFile(dataReader);
                players.add(player);
            }

            for (int i = 0; i < mpcPlayersCount; i++) {     // nactou se MPC hraci a pridaji do listu hracu
                MpcPlayer player = new MpcPlayer();
                player.loadFromFile(dataReader);
                players.add(player);
            }

            stack = new Stack();
            stack.loadFromFile(dataReader);                 // balicek karet se take sam ulozi

            bufferedInput.close();
            inputStream.close();
        }
        catch (IOException e) {
        }
        return true;
    } // nacte svoje veci a pak rekne balicku a hracum, aby se nacetli taky

    private boolean deleteGame(){
        // String gameName
        return false;
    } // pripraveno na vylepseni, zatim nefunkcni

    private int playersCount(Class aClass) {
        int count = 0;
        for (Player player :players
        ) {
            if (player.getClass() == aClass)
                count++;
        }
        return count;
    } // zjistuje pocet urcitych hracu ve hre

    private int getIntFromTo(int a, int b) {
        Scanner scan = new Scanner(System.in);
        boolean okInput = true;
        int input = 0;

        try {
            do {
                if (!okInput) {
                    System.out.println("Mimo rozpětí čísel");
                }
                input = scan.nextInt();
                okInput = input >= a && input <= b;
            }
            while (!okInput);
        }
        catch (InputMismatchException ignored) {
            System.out.println("Zadejte prosim cislo od "+a+" do "+(b-1)+" vcetne. Dekujeme!");
            return getIntFromTo(a,b);
        }
        return input;
    } // hojne vyuzivana funkce pro nacteni intu

    private String getFilename(boolean existing) {
        Scanner scanner = new Scanner(System.in);
        boolean okInput = true;
        String input = "";

        do {
            if (!okInput) {
                System.out.println("Spatne nebo jiz existujici jmeno");
            }
            input = scanner.next();
            okInput = input.getClass() == String.class;
        }
        while (!okInput);
        return input;
    }  // neco jako get From To, ale se jmenem souboru, nesmi se prepsat stary, ale zaroven nekdy chceme prave kdyz existuje...

    private void loop() throws InterruptedException  // samotna hra
    {
        int countOfRounds = 0;
        boolean again = true;
        try {
            while (again && players.size() > 1) {
                Player currentPlayer = players.get(playerIndex);
                Card lastCard = stack.getLastCard();

                if (currentPlayer.getHand().size() == 0 && lastCard.getChar() != 7 && lastCard.getSpecial() <= 0) {
                    int place = (originCountOfPlayers - players.size()) + 1;
                    System.out.println();
                    System.out.println("Gratulujeme k " + place + ". mistu hraci jmenem "+ currentPlayer.name);
                    System.out.println("Chcete pokracovat ve hre?");
                    System.out.println("   ANO (1)    NE (0)");

                    players.remove(currentPlayer);
                    playerIndex = playerIndex % players.size();
                    again = getIntFromTo(0, 1) == 1;
                }
                else {
                    System.out.println(System.lineSeparator() + "Na tahu je " + currentPlayer.name);
                    System.out.println(System.lineSeparator() + "    " + lastCard.toString());
                    if (lastCard.getChar() == 12) {
                        System.out.println("Dama ma ted tuto barvu: " + lastCard.getStringColour(lastCard.getSpecial()));
                    }
                    currentPlayer.play(stack); // pak nainstaluju behem vybirani karet se bude dat zastavit hra, zatim vzdy true
                    playerIndex = (playerIndex + 1) % players.size();
                }
                if (playerIndex == 0) {
                    countOfRounds++; // to bylo kvuli debuggovani, ale mohl bych to vypisovat, at vi, ktere kolo uz se odehrava
                }
            }

            if (players.size() > 1) {
                System.out.println("ULOZIT HRU?");
                System.out.println("ANO     NE");
                System.out.println("(1)     (0)");

                if (getIntFromTo(0,1) == 1) {
                    saveGame();
                }
            }
            else {
                System.out.println("Toto je konec hry, sam se sebou hrat asi nebudeme");
            }
        }
        catch (OutOfCardsException e) {
            System.out.println("Dosly karty, coz znamena remizu, presuneme se do hlavniho menu");
            System.out.wait(3000);
        }
    }
}