package org.techbarks.niuassaultprevention;

import android.util.Pair;

import java.util.List;

/**
 * Created by Austin on 2/15/2016.
 */
// This class represents a single page in the XML feed.
public class Page {
    public final Integer pageID;
    public final String question;
    public final String description;
    //List of buttons on this page
    //Format: <pageID to link to, description text>
    public final List<Pair<Integer,String>> button;

    public Page(Integer pageID, String question, String description, List<Pair<Integer,String>> button) {
        this.pageID = pageID;
        this.question = question;
        this.description = description;
        this.button = button;
    }
}