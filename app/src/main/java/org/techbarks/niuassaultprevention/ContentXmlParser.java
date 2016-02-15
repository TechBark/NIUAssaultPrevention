package org.techbarks.niuassaultprevention;

import android.util.Pair;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Austin on 2/15/2016.
 */
public class ContentXmlParser {
    private static final String ns = null;

    // Builds up the reader and handles it after processing is complete.
    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    // Kicks off processing of all the page elements within the content xml tag.
    private List<Page> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Page> entries = new ArrayList<Page>();

        parser.require(XmlPullParser.START_TAG, ns, "content");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("page")) {
                entries.add(readPage(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    // This class represents a single page in the XML feed.
    public static class Page {
        public final Integer pageID;
        public final String question;
        public final String description;
        //List of buttons on this page
        //Format: <pageID to link to, description text>
        public final List<Pair<Integer,String>> button;

        private Page(Integer pageID, String question, String description, List<Pair<Integer,String>> button) {
            this.pageID = pageID;
            this.question = question;
            this.description = description;
            this.button = button;
        }
    }

    // Parses the contents of an page. If it encounters a page id, question, description or button
    // it hands them off to their respective &quot;read&quot; methods for processing. Otherwise,
    // skips the tag.
    private Page readPage(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "page");
        Integer pageID = -1;
        String question = null;
        String description = null;
        List<Pair<Integer,String>> buttons = new ArrayList<Pair<Integer,String>>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("id")) {
                pageID = Integer.parseInt(readBasicStringTag(parser, "id"));
            } else if (name.equals("question")) {
                question = readBasicStringTag(parser, "question");
            } else if (name.equals("description")) {
                description = readBasicStringTag(parser, "description");
            } else if (name.equals("button")) {
                buttons.add(readButton(parser));
            } else {
                skip(parser);
            }
        }
        return new Page(pageID, question, description, buttons);
    }

    // Processes the button block to get the button's parameters, namely the page to link to on
    // click and the text to display on the button.
    private Pair<Integer,String> readButton(XmlPullParser parser) throws IOException, XmlPullParserException {
        Integer linkTo = 0;
        String text = "";
        parser.require(XmlPullParser.START_TAG, ns, "button");
        String tag = parser.getName();
        if (tag.equals("linkTo")) {
            linkTo = Integer.parseInt(readText(parser));
        }
        tag = parser.getName();
        if (tag.equals("text")) {
            text = readText(parser);
        }
        parser.require(XmlPullParser.END_TAG, ns, "button");
        return new Pair(linkTo,text);
    }

    // Processes the given tag string in the feed.
    private String readBasicStringTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String item = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return item;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}