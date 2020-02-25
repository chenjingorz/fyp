package com.example.parkinson_dec19;

import java.util.HashMap;

/**
 * update this class when new assets are added
 */

public class PoemList {

    static HashMap poemList = new HashMap<String, String>(){
        {
            put("poem0","静夜思");
            put("poem1","江雪");
            put("poem2","相思");
            put("poem3","春晓");
            put("poem4","渡汉江");
        }
    };
    static HashMap poemFlag = new HashMap<String, Integer>(){
        {
            put("poem0",1);
            put("poem1",0);
            put("poem2",0);
            put("poem3",0);
            put("poem4",0);
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
