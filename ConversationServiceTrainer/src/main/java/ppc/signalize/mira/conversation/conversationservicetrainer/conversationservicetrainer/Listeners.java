package ppc.signalize.mira.conversation.conversationservicetrainer.conversationservicetrainer;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import org.alicebot.ab.Ghost;

/**
 * Created by mukundan on 7/24/14.
 */
public class Listeners {
    static class AddTagListener implements View.OnClickListener{

        Context context;
        EditText currentResponseET;
        private String previousText;
        private UtilityStrings.TAGTOADD tagtoadd;
        public AddTagListener(Context context, UtilityStrings.TAGTOADD tagtoadd, EditText editText){
            this.context = context;
            this.tagtoadd = tagtoadd;
            this.currentResponseET = editText;
        }
        @Override
        public void onClick(View v) {
            switch (tagtoadd){
                case TEMPLATE:
                    addTemplateTag();
                    break;
                case RANDOM:
                    addRandom_LiTag();
                    break;
                case SRAI:
                    break;
                case CATEGORY:
                    addCategoryTag();
                    break;
                case CONDITION:
                    addConditionTag();
                    break;
                case DATE:
                    addDateTag();
                    break;
                case GENDER:
                    addGenderTag();
                    break;
                case GET:
                    addGetTag();
                    break;
                case ID:
                    addIDTag();
                    break;
                case LEARN:
                    addLearnTag();
                    break;
                case PATTERN:
                    addPatternTag();
                    break;
                case PERSON:
                    addPersonTag();
                    break;
                case PERSON2:
                    addPerson2Tag();
                    break;
                case SET:
                    addSetTag();
                    break;
                case SIZE:
                    addSizeTag();
                    break;
                case SR:
                    addSRTag();
                    break;
                case STAR:
                    addStarTag();
                    break;
                case THAT:
                    addThatTag();
                    break;
                case THINK:
                    addThinkTag();
                    break;
                case TOPIC:
                    addTopicTag();
                    break;
                case VERSION:
                    addVersionTag();
                    break;
                case BOT:
                    addBOTTag();
                    break;
                case AIML:
                    addAIMLTag();
                    break;
                case THAT_PREVIOUS:
                    addThatPreviousTag();
                    break;
                case THATSTAR:
                    addThatStarTag();
                    break;
                case TOPICSTAR:
                    addTopicStarTag();
                    break;
                case INPUT:
                    addInputTag();
                    break;
                case UPPERCASE:
                    addUpperCaseTag();
                    break;
                case LOWERCASE:
                    addLowercaseTag();
                    break;
                case FORMAL:
                    addFormalTag();
                    break;
                case SENTENCE:
                    addSentenceTag();
                    break;
                case LI:
                    addLiTag();
                    break;
                case GOSSIP:
                    addGossipTag();
                    break;
                case OOB:
                    addOobTag();
                    break;

            }

        }

        private void addTag(String tag, String end){
            currentResponseET.clearFocus();
            previousText = currentResponseET.getText().toString();

            int start = currentResponseET.getSelectionStart();
            String s = currentResponseET.getText().toString();
            s = s.substring(0,start) + tag + s.substring(start);
            currentResponseET.setText(s);


            currentResponseET.setSelection(start + tag.indexOf(end) + end.length());
            //activity.validateXML(activity.currentResponseET, TAGTOADD.TEMPLATE);
            currentResponseET.requestFocus();
            AdvancedSettings.showValidToast(context,"Added " + tag + " Tag");

        }

        private void addGossipTag() {
            String tag = "<gossip> </gossip>";
            addTag(tag,">");

        }

        private void addOobTag() {
            String tag = "<oob> </oob>";
            addTag(tag,">");

        }

        private void addLiTag() {
            String tag = "<li> </li>";
            addTag(tag,">");

        }

        private void addSentenceTag() {
            String tag = "<sentence> </sentence>";
            addTag(tag,">");

        }

        private void addFormalTag() {
            String tag = "<formal> </formal>";
            addTag(tag,">");
        }

        private void addLowercaseTag() {
            String tag = "<lowercase> </lowercase>";
            addTag(tag,">");
        }

        private void addUpperCaseTag() {
            String tag = "<uppercase> </uppercase>";
            addTag(tag,">");

        }

        private void addInputTag() {
            String tag = "<input index=\"1\" />";
            addTag(tag,"/>");

        }

        private void addThatStarTag() {
            String tag = "<thatstar index=\"1\" />";
            addTag(tag,"/>");

        }

        private void addTopicStarTag() {
            String tag = "<topicstar index =\"1\" />";
            addTag(tag,"/>");

        }

        private void addAIMLTag() {
            String tag = "<aiml> </aiml>";
            addTag(tag,">");

        }

        private void addThatPreviousTag() {

            String tag = "<that index =\"1,1\" />";
            addTag(tag,"/>");
        }

        private void addBOTTag() {

            String tag = "<bot name=\"\" />";
            addTag(tag,"\"");
        }



        private void addVersionTag() {
            String tag = "<version />";
            addTag(tag,"/>");

        }

        private void addTopicTag() {

            String tag = "<topic name= \"\" > </topic>";
            addTag(tag,"\"");
        }

        private void addThinkTag() {
            String tag = "<think > </think>";
            addTag(tag,">");
        }

        private void addThatTag() {
            String tag = "<that> </that>";
            addTag(tag,">");

        }

        private void addStarTag() {
            String tag = "<star />";
            addTag(tag,">");
        }

        private void addSRTag() {
            String tag = "<sr />";
            addTag(tag,"/>");

        }

        private void addSetTag() {
            String tag = "<set name = \"\"> </set>";
            addTag(tag,"\"");

        }

        private void addSizeTag() {
            String tag = "<size />";
            addTag(tag,"/>");

        }

        private void addPerson2Tag() {
            String tag = "<person2 />";
            addTag(tag,"/>");

        }

        private void addPatternTag() {
            String tag = "<pattern> </pattern>";
            addTag(tag,">");

        }

        private void addPersonTag() {
            String tag = "<person />";
            addTag(tag,"/>");

        }

        private void addLearnTag() {
            String tag = "<learn> </learn>";
            addTag(tag,">");

        }

        private void addIDTag() {

            String tag = "<id/>";
            addTag(tag,"/>");
        }

        private void addGetTag() {
            String tag = "<get name = \"\"/>";
            addTag(tag,"\"");

        }

        private void addGenderTag() {
            String tag = "<gender />";
            addTag(tag,"/>");

        }

        private void addDateTag() {
            String tag = "<date />";
            addTag(tag,"/>");
        }

        private void addConditionTag() {
            String tag = "<condition> </condition>";
            addTag(tag,">");

        }

        private void addCategoryTag() {
            String tag = "<category> </category>";
            addTag(tag,">");

        }

        private void addTemplateTag(){
            currentResponseET.clearFocus();
            if(currentResponseET.getText().toString().contains("<template>")){
                AdvancedSettings.showErrorToast(context,"Response already contains template tag, cannot add one more!!");
            }
            else{
                String tag = "<template> </template>";
                addTag(tag, ">");
            }
        }
        private void addRandom_LiTag(){
            currentResponseET.clearFocus();
            if(currentResponseET.getText().toString().contains("<random>")){
                AdvancedSettings.showErrorToast(context,"Response already contains random tag, cannot add one more!!");
            }
            else{
                String tag = "<random><li> </li></random>";
                addTag(tag, "li>");
            }
        }

    }
    static class SraiSelected implements AdapterView.OnItemSelectedListener{

        String previousText;
        UtilityStrings.TAGTOADD tagtoadd;
        Context context;
        EditText currentResponseET;
        public SraiSelected(Context context,UtilityStrings.TAGTOADD tagtoadd, EditText editText){
            this.context = context;
            this.tagtoadd = tagtoadd;
            this.currentResponseET = editText;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(position != 0) {
                addSraiTag(position);
            }
        }
        private void addTag(String tag, String end){
            currentResponseET.clearFocus();
            previousText = currentResponseET.getText().toString();

            int start = currentResponseET.getSelectionStart();
            String s = currentResponseET.getText().toString();
            s = s.substring(0,start) + tag + s.substring(start);
            currentResponseET.setText(s);


            currentResponseET.setSelection(start + tag.indexOf(end) + end.length());
            //activity.validateXML(activity.currentResponseET, TAGTOADD.TEMPLATE);
            currentResponseET.requestFocus();
            AdvancedSettings.showValidToast(context,"Added " + tag + " Tag");

        }

        private void addSraiTag(int position){
            currentResponseET.clearFocus();
            if(currentResponseET.getText().toString().contains("<srai>") ||
                    currentResponseET.getText().toString().contains("<sraix>")){
                AdvancedSettings.showErrorToast(context,"Response already contains srai/sraix tag, cannot add one more!!");
            }
            else{

                String tag = "<srai>" + Ghost.listOfPatterns.get(position) + "</srai>";
                addTag(tag,">");
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}

