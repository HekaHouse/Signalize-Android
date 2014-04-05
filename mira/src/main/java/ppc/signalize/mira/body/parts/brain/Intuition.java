package ppc.signalize.mira.body.parts.brain;


import android.content.res.AssetManager;
import android.util.Log;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.classify.JointClassification;
import com.aliasi.classify.LMClassifier;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ppc.signalize.mira.MyVoice;
import ppc.signalize.mira.body.parts.nervous.concurrent.ModelingRunner;


public class Intuition implements Runnable {

    public static final String SEVER_CLASSIFICATION_FILE = "sever";
    public static final String POLAR_CLASSIFICATION_FILE = "polar";
    public static final String SENTIMENT = "polarity";
    public static final String SEVERITY = "severity";
    private static final String TAG = "Intuition";
    public static Salience tier_one = new Salience();
    public static Salience tier_two = new Salience();


    static HashMap<String, File> mCompiled = new HashMap<String, File>();
    static HashMap<String, File> mRepositories = new HashMap<String, File>();
    static HashMap<String, String[]> mCategories = new HashMap<String, String[]>();
    static HashMap<String, LMClassifier> mClassifiers = new HashMap<String, LMClassifier>();

    static {
        String type = SENTIMENT;
        mCategories.put(type, new String[]{"positive", "negative"});
        DynamicLMClassifier c = DynamicLMClassifier.createNGramProcess(mCategories.get(type), 8);
        mClassifiers.put(type, c);

        type = SEVERITY;
        mCategories.put(type, new String[]{"severe", "minor"});
        c = DynamicLMClassifier.createNGramProcess(mCategories.get(type), 8);
        mClassifiers.put(type, c);

        buildTiers();
    }

    private static boolean severityLoaded;
    private static boolean sentimentLoaded;
    private final MyVoice mWorld;
    private final boolean mEval;

    public Intuition(boolean eval, MyVoice _world) {
        Log.d(TAG, "start intuition ");
        Intuition.init(_world);
        mWorld = _world;
        mEval = eval;
        new Thread(new ModelingRunner(SENTIMENT, mWorld, mEval), "starting sentiment").start();
        new Thread(new ModelingRunner(SEVERITY, mWorld, mEval), "starting severity").start();
    }

    private static void buildTiers() {

        tier_one.add(" blood(\\S*)");
        tier_two.add(" call( (me|us))? ");
        tier_two.add(" did not ");
        tier_one.add(" f[ae]ll ");
        tier_two.add(" missed ");
        tier_two.add(" missing ");
        tier_two.add(" rough ");
        tier_one.add(" stole ");
        tier_one.add(" trip(ped)? ");
        tier_two.add("add(itional|ed) expense");
        tier_two.add("an?( \\S+)? issue");
        tier_two.add("asked to be");
        tier_two.add("because of medicine");
        tier_one.add("bleed(ing)?");
        tier_one.add("broke my");
        tier_one.add("bruis[ingesd]+");
        tier_one.add("changed forever");
        tier_one.add("communicable");
        tier_two.add("complaining");
        tier_one.add("didn't get");
        tier_one.add("didn't heal");
        tier_one.add("didn't need");
        tier_one.add("discharge");
        tier_two.add("discomfort");
        tier_one.add("disease");
        tier_one.add("error");
        tier_one.add("feels wrong");
        tier_one.add("forceful let.?down");
        tier_one.add("fractured");
        tier_one.add("hallucinating");
        tier_one.add("heart rate was high");
        tier_one.add("hepatitis");
        tier_one.add("hipaa");
        tier_one.add("ignored");
        tier_two.add("disturbing");
        tier_one.add("intrusive");
        tier_one.add("disgrace");
        tier_one.add("be dead");
        tier_two.add("\\d+ hours");
        tier_one.add(" so severe");
        tier_two.add("noisy");
        tier_two.add("not given a room");
        tier_one.add("inexperienced");
        tier_one.add("no one came");
        tier_one.add(" bad ");
        tier_one.add("swollen");
        tier_one.add("had seizures");
        tier_two.add("in my room");
        tier_one.add("was hampered");
        tier_one.add("incorrect");
        tier_one.add("infect(ion|ed)?");
        tier_one.add("left inside");
        tier_one.add("left it in");
        tier_two.add("more complaints");
        tier_one.add("pain(sful)");
        tier_one.add("needed antibodies");
        tier_one.add("never addressed");
        tier_one.add("never have happened");
        tier_one.add("nightmare");
        tier_one.add("no reason for");
        tier_one.add("not acceptable");
        tier_one.add("not correct");
        tier_one.add("not received");
        tier_one.add("not receiving");
        tier_one.add("not trained");
        tier_one.add("not( \\S+) substituted");
        tier_one.add("over.?medicated");
        tier_one.add("overdosed");
        tier_one.add("pink eye");
        tier_one.add("pneumonia");
        tier_one.add("pulled my (\\S+) out");
        tier_two.add("supposed to be");
        tier_one.add("the wrong");
        tier_two.add("treatment plan");
        tier_one.add("unacceptable");
        tier_one.add("urinary tract");
        tier_one.add("wanted to die");
        tier_two.add("wasn't");
        tier_one.add("without consulting");
        tier_one.add("wrong( \\S+)");
        tier_one.add("wtf");
        tier_one.add("put in incorrectly");
        tier_one.add("have poor");
        tier_one.add("put at risk");
        tier_one.add("very sick from");

        tier_one.add("violently");
        tier_one.add("spreading of");
        tier_one.add("concerned about catching");
        tier_one.add("so hard that");
        tier_one.add("without( my)? knowledge");
        tier_one.add("didn't know how");
        tier_one.add("should be monitored");
        tier_one.add(" no record of ");
        tier_one.add(" more specific instructions");
        tier_one.add("should not have");
        tier_one.add("could not get");
        tier_one.add("concern for ");
        tier_one.add("again for ");
        tier_one.add("high fever");
        tier_one.add("fired");
        tier_one.add("leakage");
        tier_one.add("(squirted|spurted|spewed|spilled)");
        tier_one.add("forgot to sign");
        tier_one.add("really upset");
        tier_one.add("greatly contributed");
        tier_one.add("fiasco");
        tier_one.add("was very");
        tier_one.add("declining multiple times");
        tier_one.add("abuse");
        tier_one.add("awful");
        tier_one.add("disaster");

        tier_two.add("mess");
        tier_two.add("refused");
        tier_two.add("stupid");
        tier_two.add("call button");
        tier_two.add("emergency button");
        tier_two.add("too small");
        tier_two.add("more often");
        tier_two.add("long time");
        tier_two.add("not good");
        tier_two.add("not returned");
        tier_two.add("control of");
        tier_two.add("poking me");
        tier_two.add("forgot about");
        tier_two.add("accommodation");
        tier_two.add("veins");
        tier_two.add("fear");
        tier_two.add("claustrophobic");
        tier_two.add("confusion");
        tier_two.add("oversight");
        tier_two.add("less informative");
        tier_two.add("abrupt");
        tier_two.add("lack of communication");
        tier_two.add("waited( a)? considerable");
        tier_two.add("had a possible");
        tier_two.add("multiple times during");
        tier_two.add("live insertion");
        tier_two.add("really scared");
        tier_two.add("relieve pain");
        tier_two.add("staff needs work");
        tier_two.add("stop bed");
        tier_two.add("mildew");
        tier_two.add("(was|too) cold");
        tier_two.add("had to wait");
        tier_two.add("unsuccessful");
        tier_two.add("increased (\\S+)");
        tier_two.add("uncomfortable");
        tier_two.add("had none at");
        tier_two.add("too loud");
        tier_two.add("poor training");
        tier_two.add("freezing");
        tier_two.add("hopeless");
        tier_two.add("disgusting");
        tier_two.add("dirty");
        tier_two.add("filthy");
        tier_two.add("feces");
        tier_two.add("did not wash");
        tier_two.add("took longer");
        tier_two.add("delay");
        tier_two.add("waiting for");
        tier_two.add("inconvenient");
        tier_two.add("before i got");
        tier_two.add("too long");
        tier_two.add("took too");
        tier_two.add("not offered");
        tier_two.add("for hours");
        tier_two.add("had to ask");
        tier_two.add("had to wait");
        tier_two.add("so long");
        tier_two.add("extended( my)? stay");
        tier_two.add("more detail");
        tier_two.add("too much");
        tier_two.add("had hard time");
        tier_two.add("s?he forgot");
        tier_two.add("line of communication");
        tier_two.add("horrible");
        tier_two.add("on the floor");
        tier_two.add("having terrible");
        tier_two.add(" to beg");
        tier_two.add("screamed");
        tier_two.add("still hurts");
        tier_two.add("very difficult");
        tier_two.add("finding");
        tier_two.add("problems");
        tier_two.add("impersonal");
        tier_two.add("digging for");
        tier_two.add("were difficult");
        tier_two.add("not helpful");
        tier_two.add("severe[ly]");
        tier_two.add("diarrhea");
        tier_two.add("urine");
        tier_two.add("hurt");
        tier_two.add("had to re.?fill");
        tier_two.add("no explanation");
        tier_two.add("trash");
        tier_two.add("additional questions");
        tier_two.add("without being told");
        tier_two.add("very poor");
        tier_two.add("long time");
        tier_two.add("engorgement");
        tier_two.add("no good");
        tier_two.add("inedible");
        tier_two.add("not respecting");
        tier_two.add("needed someone");
        tier_two.add("no one");
        tier_two.add("never( \\S+)? clean(ed)?");
        tier_two.add("never read");
        tier_two.add(" no( \\S+)? reading");
        tier_two.add("scared");
        tier_two.add("food was just not");
        tier_two.add("hours wait time");
        tier_two.add("never explained");
        tier_two.add("never came back");
        tier_two.add("forgot to tell");
        tier_two.add("could help");
        tier_two.add("very hard");
        tier_two.add("should not be");
        tier_two.add("ridiculous");
        tier_two.add("did not communicate");
        tier_two.add("could not afford");
        tier_two.add("complications");
        tier_two.add("careless");
        tier_two.add("would have helped");
        tier_two.add("very small");
        tier_two.add("poor experience");
        tier_two.add("breakdowns?");
        tier_two.add("did not mee?t expectations");
        tier_two.add("not clean(ed)?");
        tier_two.add("took forever");
        tier_two.add("will influence my decision");
        tier_two.add("previous patient was still");
        tier_two.add("the floor still wet");
        tier_two.add("soaked the sheets");
        tier_two.add("have my room changed");
        tier_two.add("cleaned( \\S+)? myself");
        tier_two.add("exposed");
        tier_two.add("did not want to");
        tier_two.add("needs to be more");
        tier_two.add("ignored by");
        tier_two.add("should not have been");
        tier_two.add("had not eaten");
        tier_two.add("stink");
        tier_two.add("unbearable");
        tier_two.add("didn't listen");
        tier_two.add("took so long");
        tier_two.add("staff was very");
        tier_two.add("staff not helpful");
        tier_two.add("staff could have been more helpful");
        tier_two.add("about what was happening");
        tier_two.add("and ask dumb questions");
        tier_two.add("unsanitary");
        tier_two.add("unattentive");
        tier_two.add("sanitary conditions?");
        String t1 = "(";
        for (String s : tier_one) {
            t1 = t1 + s + "|";
        }
        t1 = t1.replaceAll("\\|$", ")");
        Pattern tier_one_pat = Pattern.compile(t1, Pattern.CASE_INSENSITIVE);
        tier_one.salient_pattern = tier_one_pat;
        String t2 = "(";
        for (String s : tier_two) {
            t2 = t2 + s + "|";
        }
        t2 = t2.replaceAll("\\|$", ")");
        Pattern tier_two_pat = Pattern.compile(t2, Pattern.CASE_INSENSITIVE);
        tier_two.salient_pattern = tier_two_pat;
    }

    public static void extract_training(MyVoice _world) {
        AssetManager am = _world.getAssets();
        if (!mCompiled.get("polarity").exists()) {
            try {
                ZipInputStream[] iss = {new ZipInputStream(am.open("polarity.zip")), new ZipInputStream(am.open("severity.zip"))};

                int count = 0;
                for (ZipInputStream is : iss) {
                    String out = _world.getTrainingRepo().getPath();
                    count++;
                    byte[] buffer = new byte[2048];
                    try {

                        // now iterate through each item in the stream. The get next
                        // entry call will return a ZipEntry for each file in the
                        // stream
                        ZipEntry entry;
                        while ((entry = is.getNextEntry()) != null) {
                            String s = String.format("Entry: %s len %d added %TD",
                                    entry.getName(), entry.getSize(),
                                    new Date(entry.getTime()));

                            String fallout = out + "/" + entry.getName();
                            Log.d(TAG, fallout);
                            if (entry.isDirectory()) {
                                new File(fallout + "/").mkdir();
                                continue;
                            }

                            FileOutputStream output = null;
                            try {
                                output = new FileOutputStream(fallout);
                                int len = 0;
                                while ((len = is.read(buffer)) > 0) {
                                    output.write(buffer, 0, len);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                // we must always close the output file
                                if (output != null) output.close();
                            }
                        }
                    } finally {
                        // we must always close the zip file.
                        is.close();
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void init(MyVoice _world) {
        mRepositories.put("polarity", _world.getPolarTrainingRepo());
        mRepositories.put("severity", _world.getSeverTrainingRepo());

        mCompiled.put("polarity", _world.getPolarClassifierRepo());
        mCompiled.put("severity", _world.getSevereClassifierRepo());

        extract_training(_world);
    }

    public static void train(MyVoice _world, String type) throws IOException {
        if (mRepositories.size() < 1)
            init(_world);
        if (!mCompiled.get(type).exists()) {
            for (int i = 0; i < mCategories.get(type).length; ++i) {

                File[] trainFiles = mRepositories.get(type).listFiles();
                for (int j = 0; j < trainFiles.length; ++j) {
                    File trainFile = trainFiles[j];
                    String category;

                    if (trainFile.getName().contains(mCategories.get(type)[0])) {
                        category = mCategories.get(type)[0];
                    } else if (trainFile.getName().contains(mCategories.get(type)[1])) {
                        category = mCategories.get(type)[1];
                    } else
                        continue;

                    Classification classification = new Classification(category);

                    if (isTrainingFile(trainFile, category)) {
                        String review = Files.readFromFile(trainFile, "ISO-8859-1");
                        Log.d(TAG, "train " + type + ":" + review);
                        Classified<CharSequence> classified = new Classified<CharSequence>(review, classification);
                        ((DynamicLMClassifier) mClassifiers.get(type)).handle(classified);
                    }
                }
            }
            AbstractExternalizable.compileTo(((DynamicLMClassifier) mClassifiers.get(type)), mCompiled.get(type));
            Log.d(TAG, "training " + type + " complete");
        } else {
            try {
                Log.d(TAG, "loading " + type);
                mClassifiers.put(type, (LMClassifier) AbstractExternalizable.readObject(mCompiled.get(type)));
                Log.d(TAG, "loading " + type + " complete");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    public static void evaluate(MyVoice _world, String type) throws IOException {
        if (mRepositories.size() < 1)
            init(_world);

        int numTests = 0;
        int numCorrect = 0;
        for (int i = 0; i < mCategories.get(type).length; ++i) {
            String category = mCategories.get(type)[i];
            File file = mRepositories.get(type);
            File[] testFiles = file.listFiles();
            for (int j = 0; j < testFiles.length; ++j) {
                File testFile = testFiles[j];
                if (isTestingFile(testFile, category)) {
                    String review = Files.readFromFile(testFile, "ISO-8859-1");
                    Log.d(TAG, "eval " + type + ":" + review);
                    ++numTests;
                    Classification classification = mClassifiers.get(type).classify(review);
                    String resultCategory = classification.bestCategory();
                    Log.d(TAG, "eval   " + type + ":" + review);
                    Log.d(TAG, "result " + category + ":" + resultCategory);

                    if (resultCategory.equals(category))
                        ++numCorrect;
                }
            }
        }
        Log.d(TAG, "evaluation of " + type);
        Log.d(TAG, "number of tests:" + numTests);
        Log.d(TAG, "number correct :" + numCorrect);
        Log.d(TAG, "             % :" + (((double) numCorrect) / ((double) numTests)));
    }


    static boolean isTestingFile(File file, String type) {
        return file.getName().charAt(file.getName().length() - 1) == '9' && file.getName().contains(type);  // test on fold 9
    }

    static boolean isTrainingFile(File file, String type) {
        return file.getName().charAt(file.getName().length() - 1) != '9' && file.getName().contains(type);  // test on fold 9
    }

    public static boolean isSeverityLoaded() {
        return severityLoaded;
    }

    public static void setSeverityLoaded(boolean severityLoaded) {
        Intuition.severityLoaded = severityLoaded;
    }

    public static boolean isSentimentLoaded() {
        return sentimentLoaded;
    }

    public static void setSentimentLoaded(boolean sentimentLoaded) {
        Intuition.sentimentLoaded = sentimentLoaded;
    }

    public static JointClassification classify(String consider, String type) {
        return mClassifiers.get(type).classify(consider);
    }


    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {


    }
}
