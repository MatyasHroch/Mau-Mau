package com.company;

// jako kosmeticka uprava nakonec, ja si proste pamatuju ze 11 je junek, 12 dama, 13 kral a 14 eso, navic, resit musim jen nektere z nich...
public enum myChar {
    ONE (1),
    TWO (2),
    THREE (3),
    FOUR (4),
    FIVE (5),
    SIX (6),
    SEVEN (7),
    EIGHT (8),
    NINE (9),
    TEN (10),
    JACK (11),
    QUEEN (12),
    KING (13),
    ACE (14);

    private int index;  //nějaké pomocné atributy

    //konstruktor (privátní, funguje jen pro konstatny výše, zkuste jej zavoalt sami níže...)
    myChar(int index){
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
