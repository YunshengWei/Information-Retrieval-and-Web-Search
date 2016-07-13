package cs276.assignments;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.BitSet;

import cs276.util.IndexCompression;
import cs276.util.Utils;

public class GammaIndex implements BaseIndex {

  @Override
  public PostingList readPosting(FileChannel fc) throws IOException {
    /*
     * TODO: Your code here
     */
    ByteBuffer buffer = ByteBuffer.allocate(3 * INT_SIZE);
    if (fc.read(buffer) == -1) {
      return null;
    }
    buffer.flip();
    int termId = buffer.getInt();
    int bufSize = buffer.getInt();
    int numPostings = buffer.getInt();

    buffer = ByteBuffer.allocate(bufSize);
    fc.read(buffer);
    buffer.flip();

    int startIndex = 0;
    int[] numberEndIndex = new int[2];
    int[] postings = new int[numPostings];
    BitSet gammaCode = BitSet.valueOf(buffer);
    for (int i = 0; i < numPostings; i++) {
      IndexCompression.gammaDecodeInteger(gammaCode, startIndex,
          numberEndIndex);
      startIndex = numberEndIndex[1];
      postings[i] = numberEndIndex[0];
    }

    IndexCompression.gapDecode(postings);
    return new PostingList(termId, Utils.intArrayToIntegerList(postings));
  }

  @Override
  public void writePosting(FileChannel fc, PostingList p) throws IOException {
    /*
     * TODO: Your code here
     */
    int[] gaps = Utils.integerListToIntArray(p.getList());
    IndexCompression.gapEncode(gaps);

    byte[] gammaCode = IndexCompression
        .gammaEncodedOutputStream(gaps, gaps.length).toByteArray();
    ByteBuffer buffer = ByteBuffer.allocate(3 * INT_SIZE + gammaCode.length);
    buffer.putInt(p.getTermId()).putInt(gammaCode.length).putInt(gaps.length)
        .put(gammaCode);
    buffer.flip();
    fc.write(buffer);
  }

}
