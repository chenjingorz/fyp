package com.example.parkinson_dec19;

import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.tesseract.android.TessBaseAPI;

public class TessOcr {

    TessBaseAPI baseApi;

    public void initAPI(){
        baseApi = new TessBaseAPI();
        String dataPath = TessWord.instance.getTessDataParentDirectory();

//        baseApi.init(dataPath,"chi_sim");
        baseApi.init(dataPath,"eng");
    }

    public String recognise(Bitmap bitmap){
        baseApi.setImage(bitmap);
        String result = baseApi.getUTF8Text();

        System.out.println(result);

        baseApi.end();

        return result;
    }
}
