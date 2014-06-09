package ppc.signalize.mira.conversation;

/**
 * Created by mukundan on 6/9/14.
 */
public class ListRow {
    private String topic;
    private String pattern;
    private int fullXml;

    public ListRow(){
        }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public int getFullXml() {
        return fullXml;
    }

    public void setFullXml(int fullXml) {
        this.fullXml = fullXml;
    }
}
