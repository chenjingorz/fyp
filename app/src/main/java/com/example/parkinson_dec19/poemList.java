package com.example.parkinson_dec19;

import java.util.HashMap;

public class poemList {

    //poem#, poemName, flag
    HashMap poemList = new HashMap<String, String>();
    HashMap poemFlag = new HashMap<String, Integer>();

    public poemList(){
        poemList.put("poem0","静夜思");
        poemList.put("poem1","凉州曲");

        poemFlag.put("poem0",0);
        poemFlag.put("poem1",1);
    }

    public void addNewPoem(String poemNo, String poemName){
        poemList.put(poemNo,poemName);
        poemFlag.put(poemNo,0);
    }
    public void updateFlag(String poemNo){
        poemFlag.put(poemNo,1);
    }

    public HashMap getPoemFlag(){
        return poemFlag;
    }
}
