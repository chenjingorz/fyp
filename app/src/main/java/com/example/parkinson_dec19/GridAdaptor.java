package com.example.parkinson_dec19;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class GridAdaptor extends BaseAdapter {

    Context context;
    ArrayList<Bitmap> imageList;

    public GridAdaptor(Context c, String poem) {
        context = c;
        imageList = imageReader(
                new File(Environment.getExternalStorageDirectory().getPath()+"/fyp/"+poem+"/images"));
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Bitmap getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv = new ImageView(context);
        iv.setLayoutParams(new GridView.LayoutParams(200, 200));
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setImageBitmap(getItem(position));
        return iv;
    }

    private ArrayList<Bitmap> imageReader(File dir){
        ArrayList<Bitmap> result = new ArrayList<>();

        File[] files = dir.listFiles();
        for (File i:files){
            result.add(BitmapFactory.decodeFile(i.getAbsolutePath()));
        }
        return result;
    }
}
