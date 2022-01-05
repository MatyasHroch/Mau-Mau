package com.company;

import java.util.ArrayList;

public class OldPlayer {
    private ArrayList<Card> hand;

    public OldPlayer(Stack stack, int count)
    {
        hand = new ArrayList<Card>();
    }

    private boolean tryToss(Card playersCard, Card lastCard) // overi, jestli svoji kartu muze na tuhle kartu dat, asi bude osetrovat i specialni karty
    {
        if (lastCard.getSpecial() != 0) // pokud je to aktivni specialni karta, tak jina pravidla nez obvykle
        {
            if (lastCard.getChar() == 12) // kdyz je kralovna, tak misto barvy pouzivame cislo jejiho special cisla
            {
                return playersCard.getColour() == lastCard.getSpecial();
            }
            return playersCard.getChar() == lastCard.getChar();
        }
        return playersCard.getChar()== lastCard.getChar() || playersCard.getColour()== lastCard.getColour() || playersCard.getChar() == 12;
    }



    private int colourCounter() {
        int[] counter = new int[] {0,0,0,0,0};
        for (Card card: hand
             ) {
            counter[card.getColour()]++;
        }
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (counter[i] > count)
            {
                count = counter[i];
            }
        }
        return count;
    }

    private Card findInHand(int aChar)
    {
        for (Card playersCard:hand)
        {
            if (aChar == playersCard.getChar())
            {
                return playersCard;
            }
        }
        return null;
    }
}