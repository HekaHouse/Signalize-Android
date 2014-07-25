// IConseration.aidl
package ppc.signalize.mira.conversation;

// Declare any non-default types here with import statements

interface IConversation {
    String process(String input);
    String inputThatTopic();
    String getPatterns();
    String getFilename();
    String getTemplate();
    void writeAIMLOut();
    void reSync();
}
