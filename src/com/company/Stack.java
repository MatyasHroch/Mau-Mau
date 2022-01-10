package com.company;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Random;

public class Stack {
    private Card[] outStack;
    int outHead;

    private Card[] inStack;
    int inHead;

public Stack ()  // udame od ktere karty zaciname
    {
        this.outStack = new Card[52];
        this.inStack = new Card[52];
        this.outHead = -1;
        this.inHead = -1;
    }

    public void fillYourself(int firstCardInEachColour) {
        for (int i = 1; i < 5; i++) {    //naplnime pole predstavujici dobiraci balicek, kdyz budeme chtit pridat vice karet, tak to jde
            for (int j = firstCardInEachColour; j < 15; j++) {
                outHead ++;
                outStack[outHead] = new Card(j,i);
            }
        }
    }

    public Card getNewCard() throws OutOfCardsException  // da nahodnou kartu z balicku a z balicku ji odstrani (vymeni ji s posledni kartou, k ni se ale nedostaneme protoze abstraktne se nnam
                                                        // balicek prave zmensil
    {
        int random = getRandomNumber(0,outHead+1); // tady random from 0 to head
        Card newCard = new Card (outStack[random].getChar(),outStack[random].getColour());
        outStack[random] = outStack[outHead];
        outHead --;
        if (outHead < 0) // ve chvili, kdy dojdou karty, staci prehodit balicky, az na posledni kartu z odkladaciho
        {
            if (inHead < 2) {
                System.out.println("Tak tohle se mi jeste nestalo. Dosly karty");
                throw new OutOfCardsException(); // vlastni vyjimka, aby se dala zachytit potom v loopu v Game
            }

            Card[] help = outStack;
            outStack = inStack;
            inStack = help;
            outHead = inHead;

            inHead = 0;
            inStack[inHead] = outStack[outHead];
            outHead --;

            // tady vymena balicku, proste Swap in a out Stacku
            // ale zaroven posledni, co byla navrchu, tak musi zustat v inStacku, vyuziju reprezentace, staci pak dat
            // posledni z noveho outStacku do prvniho prvku inStacku
        }
        return newCard;
    }

    public Card getLastCard()   //z odkladaciho balicku vrati vrchni kartu
    {
        return inStack[inHead];
    }

    public void recieveCard(Card playersCard) // prijme kartu do odkladaciho balicku
    {
        inHead++;
        Card tossedCard = new Card(playersCard.getChar(),  playersCard.getColour());
        tossedCard.setSpecial(playersCard.getSpecial());
        inStack[inHead] = tossedCard;
    }

    public static int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    } // random mi pomaha s

    public void saveToFile(DataOutput dataWriter) {
        try {
            dataWriter.writeInt(outHead);   // aktualni pocet hracu
            for (int i = 0; i < outHead + 1; i++) {
                outStack[i].saveToFile(dataWriter);
            }

            dataWriter.writeInt(inHead);   // aktualni pocet hracu
            for (int i = 0; i < inHead + 1; i++) {
                inStack[i].saveToFile(dataWriter);
            }

        } catch (IOException e) {
            System.out.println("Chyba pri ukladani balicku karet");
        }
    }

    public void loadFromFile(DataInput dataReader) { // odstranit PRINTY!!!!
        try {
            int savedOutHead = dataReader.readInt();   // aktualni pocet hracu
            while (outHead != savedOutHead) {
                outHead++;
                outStack[outHead] = new Card(dataReader.readInt(),dataReader.readInt());
                outStack[outHead].setSpecial(dataReader.readInt());
            }

            int savedInHead = dataReader.readInt();   // aktualni pocet hracu
            while (inHead != savedInHead) {
                inHead++;
                inStack[inHead] = new Card(dataReader.readInt(),dataReader.readInt());
                inStack[inHead].setSpecial(dataReader.readInt());
            }
        } catch (IOException e) {
        }
    }
}