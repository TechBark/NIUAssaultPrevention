package org.techbarks.niuassaultprevention;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Page> pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        runParser();
    }

    private void runParser() {
        pages = new ArrayList<Page>();
        try {
            InputStream is = Resources.getSystem().openRawResource(R.raw.content);
            ContentXmlParser cxp = new ContentXmlParser();
            pages = cxp.parse(is);
            System.out.println("Data Read");
        } catch (Exception e) {
            Log.v("READ ERROR", e.toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Something went wrong.");
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
