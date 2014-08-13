// IConseration.aidl
package ppc.signalize.mira.conversation;

// Declare any non-default types here with import statements
// An interface to the service.
// Declares functions the are performed by the service.
interface IConversation {
    String process(String input);
    String inputThatTopic();
    String getPatterns();
    String getFilename();
    String getFilenames();
    String getTemplate();
    void writeAIMLOut();
    void reSync();
    List<String> listOfPatterns();
    String getStorageType();
}
