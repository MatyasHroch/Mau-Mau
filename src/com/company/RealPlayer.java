package com.company;

// staci prepsat pouze ony dve abstraktni metody a funguje vse
public class RealPlayer extends Player {

    @Override
    protected boolean play(Stack stack) throws OutOfCardsException { // necha hrace si klidne vsechno proklikat, kdyz da nulu, zavola se finish turn
        printHand();
        int index = 1;
        while (true) {
            index = getIntFromTo(0,getHand().size() + 1);
            if (index == 0)
            {
                finishTurn(stack);
                return true;
            }
            Card chosenCard = getHand().get(index-1);
            if (tryToss(chosenCard,stack.getLastCard())){
                toss(chosenCard,stack);
                return true;
            }
            else {
                System.out.println("Tahle tam dat nejde");
            }
        }
    }


    @Override
    protected int whichColour() {
        System.out.println("Zvolte jakou barvu bude mít tato dáma");
        System.out.println(" ♣ (1)     ♠ (2)     ♦ (3)     ♥ (4)");

        return getIntFromTo(1,4);
    }
}