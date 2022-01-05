package com.company;

// staci prepsat pouze ony dve abstraktni metody a funguje vse
public class MpcPlayer extends Player {

    @Override
    protected boolean play(Stack stack) throws OutOfCardsException { // vyzkousi zleva vsechny karty, kdyz zadna nesedi, tak zavola finish turn
        printHand(); // u MPC hrace tisknout, co ma v ruce nemusime, ale pro testy a ukazky se to hodi
        int index = 1;
        int max = hand.size();
        while (index > 0 && index <= max) {
            Card chosenCard = getHand().get(index-1);
            if (tryToss(chosenCard,stack.getLastCard())) {
                toss(chosenCard,stack);
                return true;
            }
            index++;
        }
        finishTurn(stack);
        return true;
    }

    @Override
    protected int whichColour() {
        int[] counter = new int[] {0,0,0,0,0};
        for (Card card: hand
        ) {
            if (card.getChar() != 12)  // damy nepocita, vsechno ostatni ano
            {
                counter[card.getColour()]++;
            }
        }
        int iMax = 1;
        for (int i = 0; i < 5; i++) {
            if (counter[i] > counter[iMax]) {
                iMax = i;
            }
        }
        return iMax;
    }
}