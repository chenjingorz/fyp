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

    public HashMap getPoemList(){
        return poemList;
    }

    public String getPoemName(String key){
        return (String)poemList.get(key);
    }
}
