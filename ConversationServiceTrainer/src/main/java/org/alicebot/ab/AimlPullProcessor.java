package org.alicebot.ab;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Aron on 4/3/2014.
 * A part of Signalize for Project Patient Care
 */
public class AimlPullProcessor {
    private final String _aiml;
    XmlPullParser _xml;
    private String TAG = "AimlPullProcessor";


    public AimlPullProcessor(XmlPullParser xml, String aiml) {
        _xml = xml;
        _aiml = aiml;
    }

    public boolean isCategory() throws XmlPullParserException {
        boolean toReturn = false;
        if (_xml.getEventType() == XmlPullParser.START_TAG)
            toReturn = _xml.getName().equals("category");
        return toReturn;
    }

    private boolean isCategoryComplete() throws XmlPullParserException {
        boolean toReturn = false;
        if (_xml.getEventType() == XmlPullParser.END_TAG)
            toReturn = _xml.getName().equals("category");
        return toReturn;
    }


    public Category getCategory() throws XmlPullParserException, IOException {
        if (!isCategory()) {
            Log.d(TAG, "this is not a category!");
            return null;
        }
        String pattern = "*";
        String topic = "*";
        String that = "*";
        String template = "";
        int step = 0;

        while (!isCategoryComplete()) {
            _xml.next();
            step++;
            if (_xml.getEventType() == XmlPullParser.START_TAG) {
                if (isCategory()) {
                    Log.d(TAG, "you cannot have nested category!");
                    break;
                } else if (isPattern()) {
                    pattern = getPattern();
                } else if (isThat()) {
                    that = getThat();
                } else if (isTopic()) {
                    topic = getTopic();
                } else if (isTemplate()) {
                    template = getTemplate().replaceAll("(<bot[^/>]*/>)</bot>", "\1")
                            .replaceAll("(<get[^/>]*/>)</get>", "\1")
                            .replaceAll("(<star[^/>]*/>)</star>", "\1")
                            .replaceAll("(<srai[^/>]*/>)</srai>", "\1")
                            .replaceAll("(<set[^/>]*/>)</set>", "\1")
                            .replaceAll("(<SET[^/>]*/>)</SET>", "\1");
                    if (isCategoryComplete())
                        break;
                } else
                    Log.d(TAG, "unrecognized tag " + _xml.getName());
            } else if (_xml.getEventType() == XmlPullParser.END_TAG) {
                if (isCategoryComplete())
                    break;
            }
        }
        return new Category(0, pattern, that, topic, template, _aiml);
    }

    public Category getCategory(String topical) throws XmlPullParserException, IOException {
        if (!isCategory()) {
            Log.d(TAG, "this is not a category!");
            return null;
        }
        String pattern = "*";
        String topic = topical;
        String that = "*";
        String template = "";
        int step = 0;

        while (!isCategoryComplete()) {
            _xml.next();
            step++;
            if (_xml.getEventType() == XmlPullParser.START_TAG) {
                if (isCategory()) {
                    Log.d(TAG, "you cannot have nested category!");
                    break;
                } else if (isPattern()) {
                    pattern = getPattern();
                } else if (isThat()) {
                    that = getThat();
                } else if (isTopic()) {
                    topic = getTopic();
                } else if (isTemplate()) {
                    template = getTemplate().replaceAll("(<bot[^/>]*/>)</bot>", "\1")
                            .replaceAll("(<get[^/>]*/>)</get>", "\1")
                            .replaceAll("(<star[^/>]*/>)</star>", "\1")
                            .replaceAll("(<srai[^/>]*/>)</srai>", "\1")
                            .replaceAll("(<set[^/>]*/>)</set>", "\1")
                            .replaceAll("(<SET[^/>]*/>)</SET>", "\1");
                    if (isCategoryComplete())
                        break;
                } else
                    Log.d(TAG, "unrecognized tag " + _xml.getName());
            } else if (_xml.getEventType() == XmlPullParser.END_TAG) {
                if (isCategoryComplete())
                    break;
            }
        }
        return new Category(0, pattern, that, topic, template, _aiml);
    }

    public boolean isTopic() throws XmlPullParserException {
        boolean toReturn = false;
        if (_xml.getEventType() == XmlPullParser.START_TAG)
            toReturn = _xml.getName().equals("topic");
        return toReturn;
    }

    private boolean isTopicComplete() throws XmlPullParserException {
        boolean toReturn = false;
        if (_xml.getEventType() == XmlPullParser.END_TAG)
            toReturn = _xml.getName().equals("topic");
        return toReturn;
    }

    public String getTopic() throws XmlPullParserException, IOException {
        if (!isTopic()) {
            Log.d(TAG, "this is not a topic!");
            return null;
        }
        String toAdd = "";
        while (!isTopicComplete()) {
            _xml.next();
            if (_xml.getEventType() == XmlPullParser.TEXT) {
                if (_xml.getText() != null)
                    toAdd = addToString(toAdd, _xml.getText());
            } else if (_xml.getEventType() == XmlPullParser.START_TAG) {
                if (isTopic())
                    Log.d(TAG, "you cannot have nested topic!");
                else
                    toAdd = addStartTagToString(toAdd, _xml.getName());

            } else if (_xml.getEventType() == XmlPullParser.END_TAG) {
                if (!isTopicComplete())
                    toAdd = addEndTagToString(toAdd, _xml.getName());
            }
        }
        return toAdd;
    }

    public ArrayList<Category> getTopicCategories() throws XmlPullParserException, IOException {
        if (!isTopic()) {
            Log.d(TAG, "cannot retrieve categories, this is not a topic!");
            return null;
        }
        ArrayList<Category> toReturn = new ArrayList<Category>();
        while (!isTopicComplete()) {
            String att = getAttribute("name");
            _xml.next();
            if (isCategory())
                toReturn.add(getCategory(att));
        }
        return toReturn;
    }

    public boolean isPattern() throws XmlPullParserException {
        boolean toReturn = false;
        if (_xml.getEventType() == XmlPullParser.START_TAG)
            toReturn = _xml.getName().equals("pattern");
        return toReturn;
    }

    private boolean isPatternComplete() throws XmlPullParserException {
        boolean toReturn = false;
        if (_xml.getEventType() == XmlPullParser.END_TAG)
            toReturn = _xml.getName().equals("pattern");
        return toReturn;
    }

    public String getPattern() throws XmlPullParserException, IOException {
        if (!isPattern()) {
            Log.d(TAG, "this is not a pattern!");
            return null;
        }
        String toAdd = "";
        while (!isPatternComplete()) {
            _xml.next();
            if (_xml.getEventType() == XmlPullParser.TEXT) {
                if (_xml.getText() != null)
                    toAdd = addToString(toAdd, _xml.getText());
            } else if (_xml.getEventType() == XmlPullParser.START_TAG) {
                if (isPattern())
                    Log.d(TAG, "you cannot have nested pattern!");
                else
                    toAdd = addStartTagToString(toAdd, _xml.getName());

            } else if (_xml.getEventType() == XmlPullParser.END_TAG) {
                if (!isPatternComplete())
                    toAdd = addEndTagToString(toAdd, _xml.getName());
            }
        }
        return toAdd;
    }

    public boolean isThat() throws XmlPullParserException {
        boolean toReturn = false;
        if (_xml.getEventType() == XmlPullParser.START_TAG)
            toReturn = _xml.getName().equals("that");
        return toReturn;
    }


    private boolean isThatComplete() throws XmlPullParserException {
        boolean toReturn = false;
        if (_xml.getEventType() == XmlPullParser.END_TAG)
            toReturn = _xml.getName().equals("that");
        return toReturn;
    }

    public String getThat() throws XmlPullParserException, IOException {
        if (!isThat()) {
            Log.d(TAG, "this is not a that!");
            return null;
        }
        String toAdd = "";
        while (!isThatComplete()) {
            _xml.next();
            if (_xml.getEventType() == XmlPullParser.TEXT) {
                if (_xml.getText() != null)
                    toAdd = addToString(toAdd, _xml.getText());
            } else if (_xml.getEventType() == XmlPullParser.START_TAG) {
                if (isThat())
                    Log.d(TAG, "you cannot have nested that!");
                else
                    toAdd = addStartTagToString(toAdd, _xml.getName());

            } else if (_xml.getEventType() == XmlPullParser.END_TAG) {
                if (!isThatComplete())
                    toAdd = addEndTagToString(toAdd, _xml.getName());
            }
        }
        return toAdd;
    }

    public boolean isTemplate() throws XmlPullParserException {
        boolean toReturn = false;
        if (_xml.getEventType() == XmlPullParser.START_TAG)
            toReturn = _xml.getName().equals("template");
        return toReturn;
    }

    private boolean isTemplateComplete() throws XmlPullParserException {
        boolean toReturn = false;
        if (_xml.getEventType() == XmlPullParser.END_TAG)
            toReturn = _xml.getName().equals("template");
        return toReturn;
    }

    public String getTemplate() throws XmlPullParserException, IOException {
        if (!isTemplate()) {
            Log.d(TAG, "this is not a template!");
            return null;
        }
        int templateTags = 0;
        String template = "";
        _xml.next();
        while (!isTemplateComplete() || templateTags > 0) {

            if (_xml.getEventType() == XmlPullParser.TEXT) {
                if (_xml.getText() != null)
                    template = addToString(template, _xml.getText());
            } else if (_xml.getEventType() == XmlPullParser.START_TAG) {
                if (isTemplate()) {
                    Log.d(TAG, "nested templates!");
                    templateTags++;
                }
                template = addStartTagToString(template, _xml.getName());

            } else if (_xml.getEventType() == XmlPullParser.END_TAG) {
                if (templateTags > 0 || !isTemplateComplete()) {
                    template = addEndTagToString(template, _xml.getName());
                }
                if (templateTags > 0 && isTemplateComplete())
                    templateTags--;
            }
            _xml.next();
        }
        return template;
    }

    private String addEndTagToString(String base, String toAdd) {
        String tag = "";
        tag = "</" + toAdd + ">";
        return addToString(base, tag);
    }

    private String addStartTagToString(String base, String toAdd) {
        String tag = "";
        String att = getAttributes();
        if (att.length() > 0)
            tag = "<" + toAdd + att + ">";
        else
            tag = "<" + toAdd + ">";

        return addToString(base, tag);
    }

    private String addEmptyTagToString(String base, String toAdd) {
        String empty = "";
        String att = getAttributes();
        if (att.length() > 0)
            empty = "<" + toAdd + att + "/>";
        else
            empty = "<" + toAdd + "/>";

        return addToString(base, empty);
    }

    private String getAttribute(String toMatch) {
        String att = "";
        if (_xml.getAttributeCount() > 0) {
            for (int i = 0; i < _xml.getAttributeCount(); i++) {
                if (_xml.getAttributeName(i).equals(toMatch))
                    att = _xml.getAttributeValue(i);
            }
        }
        return att;
    }

    private String getAttributes() {
        String att = "";
        if (_xml.getAttributeCount() > 0) {
            for (int i = 0; i < _xml.getAttributeCount(); i++) {
                att += " " + _xml.getAttributeName(i) + "=\"" + _xml.getAttributeValue(i) + "\"";
            }
        }
        return att;
    }

    private String addToString(String base, String toAdd) {
        base = base + toAdd;
        return base;
    }

    public int getEventType() throws XmlPullParserException {
        return _xml.getEventType();
    }

    public int next() throws IOException, XmlPullParserException {
        return _xml.next();
    }
}
