package com.company;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;

public class Card {
    private int aChar;
    private int colour;
    private int special;

    public Card(int aChar, int colour) {
        this.aChar = aChar;
        this.colour = colour;
        this.special = 0;
    }

    public void setSpecial(int special) {
        this.special = special;
    }

    public int getSpecial() {
        return special;
    }

    public int getChar() {
        return aChar;
    }

    public void setColour(int colour) {
        if (this.getChar() == 12)
        {
            this.colour = colour;
        }
    }

    public int getColour() {
        return colour;
    }

    public int compareTo(Card other) {   // radi se podle hodnoty barvy ve hre tisic a nasledne zleva doprava podle hodnoty karty, pricemz eso je na konci
        int firstNumber = this.getColour() * 100 + this.getChar();
        int secondNumber = other.getColour() * 100 + this.getChar();
        return secondNumber - firstNumber;
    }

    public void saveToFile(DataOutput dataWriter) {
        try{
            dataWriter.writeInt(aChar);
            dataWriter.writeInt(colour);
            dataWriter.writeInt(special);
        }
        catch (IOException ignored){
        }
    }

    public String getStringColour(int x) {
        switch (x) {
            case 1 -> {
                return "♣ ";
            }
            case 2 -> {
                return "♠ ";
            }
            case 3 -> {
                return "♦ ";
            }
            case 4 -> {
                return "♥ ";
            }
            default -> {
                return getStringColour(getColour());
            }
        }
    }

    @Override
    public String toString() {
        String result;
        result = getStringColour(getColour());

        switch (getChar()){
            case 11 -> result += "J";
            case 12 -> result += "Q";
            case 13 -> result += "K";
            case 14 -> result += "A";
            default -> result += getChar();
        }
        return result;
    }

    public void print()
    {
        System.out.print(this.toString());
    }
}