package com.company;

public class Main {

    public static void main(String[] args) throws Exception {

        pokus();
    }

    public static void pokus() throws Exception {
        // write your code here
        System.out.println("♣  ♠  ♦  ♥");
        System.out.println();
        System.out.println("     /\\     |   __  __  ");
        System.out.println("    /  \\    |  /  \\/  \\");
        System.out.println("   /    \\   | |        |");
        System.out.println("   \\    /   |  \\      /");
        System.out.println("    \\  /    |   \\    /");
        System.out.println("     \\/     |     \\/");
        System.out.println();
        System.out.println();

        Game game = new Game();
        game.start();

    }
}