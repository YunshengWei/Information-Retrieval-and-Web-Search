package edu.stanford.cs276;

public class BuildModels {

  public static double MU = .05;
  public static LanguageModel languageModel;
  public static NoisyChannelModel noisyChannelModel;

  public static void main(String[] args) throws Exception {

    String trainingCorpus = null;
    String editsFile = null;
    String extra = null;
    if (args.length == 2 || args.length == 3) {
      trainingCorpus = args[0];
      editsFile = args[1];
      if (args.length == 3) extra = args[2];
    } else {
      System.err.println(
          "Invalid arguments.  Argument count must 2 or 3 \n" 
          + "./buildmodels <training corpus dir> <training edit1s file> \n"
          + "./buildmodels <training corpus dir> <training edit1s file> <extra> \n"
          + "SAMPLE: ./buildmodels data/corpus data/edit1s.txt \n"
          + "SAMPLE: ./buildmodels data/corpus data/edit1s.txt extra \n");
      return;
    }
    System.out.println("training corpus: " + args[0]);

    languageModel = LanguageModel.create(trainingCorpus);
    noisyChannelModel = NoisyChannelModel.create(editsFile);

    // Save the models to disk
    noisyChannelModel.save();
    languageModel.save();

    if ("extra".equals(extra)) {
      /*
       * If you are going to implement something regarding to building models,
       * you can add code here. Feel free to move this code block to wherever
       * you think is appropriate. But make sure if you add "extra" parameter,
       * it will run code for your extra credit and it will run you basic
       * implementations without the "extra" parameter.
       */
    }
  }
}
