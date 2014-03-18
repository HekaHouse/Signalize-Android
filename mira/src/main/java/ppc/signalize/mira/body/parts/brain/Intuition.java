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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ppc.signalize.mira.MyVoice;
import ppc.signalize.mira.body.parts.nervous.concurrent.ModelingRunner;


public class Intuition implements Runnable {

	public static final String SEVER_CLASSIFICATION_FILE = "sever";
	public static final String POLAR_CLASSIFICATION_FILE = "polar";
    private static final String TAG = "Intuition";
    public static final String SENTIMENT = "polarity";
    public static final String SEVERITY = "severity";

    static HashMap<String,File> mCompiled = new HashMap<String,File>();
    static HashMap<String,File> mRepositories = new HashMap<String,File>();
    static HashMap<String,String[]> mCategories = new HashMap<String,String[]>();
    static HashMap<String,LMClassifier> mClassifiers = new HashMap<String,LMClassifier>();

    static {
        String type = SENTIMENT;
        mCategories.put(type, new String[]{"positive", "negative"});
        DynamicLMClassifier c = DynamicLMClassifier.createNGramProcess(mCategories.get(type),8);
        mClassifiers.put(type, c);

        type = SEVERITY;
        mCategories.put(type,new String[] {"severe","minor"});
        c = DynamicLMClassifier.createNGramProcess(mCategories.get(type),8);
        mClassifiers.put(type, c);
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
        new Thread(new ModelingRunner(SENTIMENT,mWorld,mEval),"starting sentiment").start();
        new Thread(new ModelingRunner(SEVERITY,mWorld,mEval),"starting severity").start();
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

                    if (isTrainingFile(trainFile,category)) {
                        String review = Files.readFromFile(trainFile, "ISO-8859-1");
                        Log.d(TAG, "train " + type + ":" + review);
                        Classified<CharSequence> classified = new Classified<CharSequence>(review, classification);
                        ((DynamicLMClassifier)mClassifiers.get(type)).handle(classified);
                    }
                }
            }
            AbstractExternalizable.compileTo(((DynamicLMClassifier)mClassifiers.get(type)), mCompiled.get(type));
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


    public static void evaluate(MyVoice _world,String type) throws IOException {
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
                    String review = Files.readFromFile(testFile,"ISO-8859-1");
                    Log.d(TAG, "eval "+ type + ":" + review);
                    ++numTests;
                    Classification classification = mClassifiers.get(type).classify(review);
                    String resultCategory = classification.bestCategory();
                    Log.d(TAG, "eval   "+ type + ":" + review);
                    Log.d(TAG, "result "+ category + ":" + resultCategory);

                    if (resultCategory.equals(category))
                        ++numCorrect;
                }
            }
        }
        Log.d(TAG, "evaluation of " + type);
        Log.d(TAG, "number of tests:"+ numTests);
        Log.d(TAG, "number correct :"+ numCorrect);
        Log.d(TAG, "             % :"+ (((double)numCorrect) / ((double)numTests)));
    }


    static boolean isTestingFile(File file, String type) {
        return file.getName().charAt(file.getName().length()-1) == '9' && file.getName().contains(type);  // test on fold 9
    }

    static boolean isTrainingFile(File file, String type) {
        return file.getName().charAt(file.getName().length()-1) != '9' && file.getName().contains(type);  // test on fold 9
    }

    public static void setSeverityLoaded(boolean severityLoaded) {
        Intuition.severityLoaded = severityLoaded;
    }

    public static void setSentimentLoaded(boolean sentimentLoaded) {
        Intuition.sentimentLoaded = sentimentLoaded;
    }
    public static boolean isSeverityLoaded() {
        return severityLoaded;
    }

    public static boolean isSentimentLoaded() {
        return sentimentLoaded;
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
