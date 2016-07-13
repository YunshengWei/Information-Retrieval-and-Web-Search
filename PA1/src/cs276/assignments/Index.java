package cs276.assignments;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cs276.util.Pair;

public class Index {

  // Term id -> (position in index file, doc frequency) dictionary
  private static Map<Integer, Pair<Long, Integer>> postingDict =
      new TreeMap<Integer, Pair<Long, Integer>>();
  // Doc name -> doc id dictionary
  private static Map<String, Integer> docDict = new TreeMap<String, Integer>();
  // Term -> term id dictionary
  private static Map<String, Integer> termDict = new TreeMap<String, Integer>();
  // Block queue
  private static LinkedList<File> blockQueue = new LinkedList<File>();

  // Total file counter
  private static int totalFileCount = 0;
  // Document counter
  private static int docIdCounter = 0;
  // Term counter
  private static int wordIdCounter = 0;
  // Index
  private static BaseIndex index = null;

  /*
   * Write a posting list to the given file You should record the file position
   * of this posting list so that you can read it back during retrieval
   */
  private static void writePosting(FileChannel fc, PostingList posting)
      throws IOException {
    /*
     * TODO: Your code here
     */
    postingDict.put(posting.getTermId(),
        Pair.make(fc.position(), posting.getList().size()));
    Index.index.writePosting(fc, posting);
  }

  public static void main(String[] args) throws IOException {
    /* Parse command line */
    if (args.length != 3) {
      System.err
          .println("Usage: java Index [Basic|VB|Gamma] data_dir output_dir");
      return;
    }

    /* Get index */
    String className = "cs276.assignments." + args[0] + "Index";
    try {
      Class<?> indexClass = Class.forName(className);
      index = (BaseIndex) indexClass.newInstance();
    } catch (Exception e) {
      System.err
          .println("Index method must be \"Basic\", \"VB\", or \"Gamma\"");
      throw new RuntimeException(e);
    }

    /* Get root directory */
    String root = args[1];
    File rootdir = new File(root);
    if (!rootdir.exists() || !rootdir.isDirectory()) {
      System.err.println("Invalid data directory: " + root);
      return;
    }

    /* Get output directory */
    String output = args[2];
    File outdir = new File(output);
    if (outdir.exists() && !outdir.isDirectory()) {
      System.err.println("Invalid output directory: " + output);
      return;
    }

    if (!outdir.exists()) {
      if (!outdir.mkdirs()) {
        System.err.println("Create output directory failure");
        return;
      }
    }

    /* BSBI indexing algorithm */
    File[] dirlist = rootdir.listFiles();

    /* For each block */
    for (File block : dirlist) {
      File blockFile = new File(output, block.getName());
      if (blockFile.isHidden()) {
        continue;
      }

      blockQueue.add(blockFile);

      File blockDir = new File(root, block.getName());
      File[] filelist = blockDir.listFiles();

      Map<Integer, List<Integer>> partialInvertedIndex = new TreeMap<>();

      /* For each file */
      for (File file : filelist) {
        if (file.isHidden()) {
          continue;
        }

        ++totalFileCount;
        String fileName = block.getName() + "/" + file.getName();
        int docId = docIdCounter++;
        docDict.put(fileName, docId);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
          String[] tokens = line.trim().split("\\s+");
          for (String token : tokens) {
            /*
             * TODO: Your code here For each term, build up a list of documents
             * in which the term occurs
             */
            if (!termDict.containsKey(token)) {
              termDict.put(token, wordIdCounter++);
            }
            int termId = termDict.get(token);

            if (!partialInvertedIndex.containsKey(termId)) {
              partialInvertedIndex.put(termId, new ArrayList<>());
            }
            List<Integer> postings = partialInvertedIndex.get(termId);
            if (postings.isEmpty()
                || postings.get(postings.size() - 1) != docId) {
              postings.add(docId);
            }
          }
        }
        reader.close();
      }

      /* Sort and output */
      if (!blockFile.createNewFile()) {
        System.err.println("Create new block failure.");
        return;
      }

      RandomAccessFile bfc = new RandomAccessFile(blockFile, "rw");

      /*
       * TODO: Your code here Write all posting lists for all terms to file
       * (bfc)
       */
      FileChannel fc = bfc.getChannel();
      for (Map.Entry<Integer, List<Integer>> entry : partialInvertedIndex
          .entrySet()) {
        Index.writePosting(fc,
            new PostingList(entry.getKey(), entry.getValue()));
      }

      bfc.close();
    }

    /* Required: output total number of files. */
    System.out.println(totalFileCount);

    /* Merge blocks */
    while (true) {
      if (blockQueue.size() <= 1)
        break;

      File b1 = blockQueue.removeFirst();
      File b2 = blockQueue.removeFirst();

      File combfile = new File(output, b1.getName() + "+" + b2.getName());
      if (!combfile.createNewFile()) {
        System.err.println("Create new block failure.");
        return;
      }

      RandomAccessFile bf1 = new RandomAccessFile(b1, "r");
      RandomAccessFile bf2 = new RandomAccessFile(b2, "r");
      RandomAccessFile mf = new RandomAccessFile(combfile, "rw");

      /*
       * TODO: Your code here Combine blocks bf1 and bf2 into our combined file,
       * mf You will want to consider in what order to merge the two blocks
       * (based on term ID, perhaps?).
       */
      FileChannel fc1 = bf1.getChannel();
      FileChannel fc2 = bf2.getChannel();
      FileChannel mfc = mf.getChannel();

      PostingList pl1 = index.readPosting(fc1);
      PostingList pl2 = index.readPosting(fc2);
      while (pl1 != null || pl2 != null) {
        if (pl1 == null || pl2 != null && pl1.getTermId() > pl2.getTermId()) {
          Index.writePosting(mfc, pl2);
          pl2 = index.readPosting(fc2);
        } else if (pl2 == null || pl1.getTermId() < pl2.getTermId()) {
          Index.writePosting(mfc, pl1);
          pl1 = index.readPosting(fc1);
        } else {
          // pl1.getTermId() == pl2.getTermId()
          pl1.getList().addAll(pl2.getList());
          Collections.sort(pl1.getList());
          Index.writePosting(mfc,
              new PostingList(pl1.getTermId(), pl1.getList()));
          pl1 = index.readPosting(fc1);
          pl2 = index.readPosting(fc2);
        }
      }

      bf1.close();
      bf2.close();
      mf.close();
      b1.delete();
      b2.delete();
      blockQueue.add(combfile);
    }

    /* Dump constructed index back into file system */
    File indexFile = blockQueue.removeFirst();
    indexFile.renameTo(new File(output, "corpus.index"));

    BufferedWriter termWriter =
        new BufferedWriter(new FileWriter(new File(output, "term.dict")));
    for (String term : termDict.keySet()) {
      termWriter.write(term + "\t" + termDict.get(term) + "\n");
    }
    termWriter.close();

    BufferedWriter docWriter =
        new BufferedWriter(new FileWriter(new File(output, "doc.dict")));
    for (String doc : docDict.keySet()) {
      docWriter.write(doc + "\t" + docDict.get(doc) + "\n");
    }
    docWriter.close();

    BufferedWriter postWriter =
        new BufferedWriter(new FileWriter(new File(output, "posting.dict")));
    for (Integer termId : postingDict.keySet()) {
      postWriter.write(termId + "\t" + postingDict.get(termId).getFirst() + "\t"
          + postingDict.get(termId).getSecond() + "\n");
    }
    postWriter.close();
  }

}
