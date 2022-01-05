package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public abstract class Player {
    protected ArrayList<Card> hand;
    protected String name = "hrac";


    public Player() {
        this.hand = new ArrayList<Card>();
    }

    public ArrayList<Card> getHand(){
        return hand;
    }

    public void printHand() {
        System.out.println();
        if (this.getClass() == RealPlayer.class) {
            System.out.print("Draw Card  ");
        }
        for (Card card : hand) {
            card.print();
            if (card.getChar() == 10)
            {
                System.out.print("  ");
            }
            else {
                System.out.print("   ");
            }
        }
        System.out.println();
        if (this.getClass() == RealPlayer.class) {
            System.out.print("    (0)    ");
        }
        for (int i = 0; i < hand.size(); i++) {
            System.out.print("("+(i+1)+")   ");
        }
        System.out.println();
    }

    public void drawCard(Stack stack, int count) throws OutOfCardsException {
        for (int i = 0; i < count; i++) {
            drawCard(stack);
        }
    }

    public void drawCard(Stack stack) throws OutOfCardsException {
        hand.add(stack.getNewCard());
        hand.sort(Card::compareTo);
    }

    public void toss(Card card, Stack stack) // da kartu na odkladaci balicek
    {
        Card lastCard = stack.getLastCard();
        int special;

        if (lastCard.getChar() == 12) {                    // pokud je to dama, tak jeji special musime nastavit na 0 uz tady, jinak vse stejne
            lastCard.setSpecial(0);
        }

        switch (card.getChar()) {
            case 14 -> special = 1;
            case 12 -> special = whichColour();
            case 7 -> special = 2;
            default -> special = 0;
        }

        card.setSpecial(lastCard.getSpecial() + special);
        lastCard.setSpecial(0);                           //deaktivuje predchozi kartu, to se stane vzdycky, protoze i pri scitani karet u sedmicky aktivni je jen ta posleni
        stack.recieveCard(card);
        hand.remove(card);
    }
    public boolean tryToss(Card playersCard, Card lastCard) // overi, jestli svoji kartu muze na tuhle kartu dat, asi bude osetrovat i specialni karty
    {
        if (lastCard.getSpecial() != 0) // pokud je to aktivni specialni karta, tak jina pravidla nez obvykle
        {
            if (lastCard.getChar() == 12) // kdyz je kralovna, tak misto barvy pouzivame cislo jejiho special cisla
            {
                return playersCard.getColour() == lastCard.getSpecial() || playersCard.getChar() == 12;
            }
            return playersCard.getChar() == lastCard.getChar();
        }
        return playersCard.getChar()== lastCard.getChar() || playersCard.getColour()== lastCard.getColour() || playersCard.getChar() == 12;
    }
    protected int getIntFromTo(int a, int b) {
        Scanner scan = new Scanner(System.in);
        boolean okInput = true;
        int input = 0;

        do {
            if (!okInput) {
                System.out.println("Mimo rozpětí čísel");
            }
            input = scan.nextInt();
            okInput = input >= a && input <= b;
        }
        while (!okInput);
        return input;
    } // metoda pro nacteni int inputu, hojne vyuzivam

    protected boolean finishTurn(Stack stack) throws OutOfCardsException {
        Card lastCard = stack.getLastCard();
        if (lastCard.getSpecial() > 0) {
            switch (lastCard.getChar()) {
                case 7 -> drawCard(stack,lastCard.getSpecial());
                case 14 -> drawCard(stack,0);
                default -> drawCard(stack);
            }
        }
        else {
            drawCard(stack);
        }

        if (lastCard.getChar() != 12) {
            lastCard.setSpecial(0);
        }
        return true;
    } // kdyz nema hrac potrebnou kartu, vykona se toto, zalezi jestli tam byla aktivni nejaka karta nebo si jen jednu kartu dobere

    protected void saveToFile(DataOutput dataWriter) {
        try{
            dataWriter.writeUTF(name);          // jmeno hrace
            dataWriter.writeInt(hand.size());   // aktualni pocet hracu

            for (Card card:hand
                 ) {
                card.saveToFile(dataWriter);
            }
        }
        catch (IOException e){
        }
    } // ulozi se data o hraci a pak kazda karta z jeho ruky

    public void setName(String name) {
        this.name = name;
    }

    protected void loadFromFile(DataInput dataReader) {
        try{
            name =  dataReader.readUTF();          // jmeno hrace
            int handSize = dataReader.readInt();   // aktualni pocet hracu

            for (int i = 0; i < handSize; i++) {
                Card card = new Card(dataReader.readInt(),dataReader.readInt());
                card.setSpecial(dataReader.readInt());
                hand.add(card);
            }
        }
        catch (IOException e){
        }
    } // nactou se data o hraci a pak kazda karta z jeho ruky


    // tyto dve metody jsou jedine, kde se lisi realny a MPC hrac a jsou jine kvuli tomu, ze se vyzaduje nebo nevyzaduje vstup, proto jsou abstraktni
    protected abstract boolean play(Stack stack) throws OutOfCardsException;
    protected abstract int whichColour();
}