package com.example.parkinson_dec19;

import android.app.Application;

import com.myscript.certificate.MyCertificate;
import com.myscript.iink.Engine;

public class InkEngine extends Application
{
    private static Engine engine;


    public static synchronized Engine getEngine()
    {
        if (engine == null)
        {
            engine = Engine.create(MyCertificate.getBytes());
        }

        return engine;
    }


}