package com.example.drinkalarm.metier;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by david on 05/04/14.
 */
public class ActionXmlParser {

    private static final String ns = null;

    public List<Action> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readAction(parser);
        } finally {
            in.close();
        }
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.

    private List<Action> readAction(XmlPullParser parser) throws XmlPullParserException, IOException {

        ArrayList<Action> actions = new ArrayList<Action>();
        parser.require(XmlPullParser.START_TAG, ns, "actions");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            parser.require(XmlPullParser.START_TAG, ns, "action");
            String label = null;
            String description = null;
            String son = null;
            Integer participant = 0;
            Boolean random = false;
            Map<String,Float> chances = new HashMap<String, Float>();

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("label")) {
                    label = readLabel(parser);
                } else if (name.equals("description")) {
                    description = readDescription(parser);
                } else if (name.equals("son")) {
                    son = readSon(parser);
                } else if (name.equals("participant")) {
                    participant = readParticipant(parser);
                } else if (name.equals("random")) {
                    random = readRandom(parser);
                } else if (name.equals("chance")) {
                    String mode = readChanceMode(parser);
                    chances.put(mode,readChance(parser));
                } else {
                    skip(parser);
                }
            }
            parser.require(XmlPullParser.END_TAG, ns, "action");
            actions.add(new Action(label, description, son, participant, random,chances));
        }
        return actions;
    }

    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return title;
    }

    private String readLabel(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "label");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "label");
        return title;
    }

    private Integer readParticipant(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "participant");
        Integer title = Integer.getInteger(readText(parser), 0);
        parser.require(XmlPullParser.END_TAG, ns, "participant");
        return title;
    }

    private String readChanceMode(XmlPullParser parser) throws IOException, XmlPullParserException {
        return parser.getAttributeValue(ns,"mode");
    }

    private Float readChance(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "chance");
        Float title = Float.parseFloat(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "chance");
        return title;
    }

    private Boolean readRandom(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "random");
        Boolean title = Boolean.parseBoolean(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "random");
        return title;
    }

    private String readSon(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "son");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "son");
        return title;
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
