package com.example.parkinson_dec19;

import java.util.HashMap;

public class PoemList {

    static HashMap poemList = new HashMap<String, String>(){
        {
            put("poem0","静夜思");
            put("poem1","凉州曲");
        }
    };
    static HashMap poemFlag = new HashMap<String, Integer>(){
        {
            put("poem0",1);
            put("poem1",0);
        }
    };

    public void updateFlag(String poemNo){
        poemFlag.put(poemNo,1);
    }

    public HashMap getPoemFlag(){
        return poemFlag;
    }

    public String getPoemName(String key){
        return (String)poemList.get(key);
    }
}
