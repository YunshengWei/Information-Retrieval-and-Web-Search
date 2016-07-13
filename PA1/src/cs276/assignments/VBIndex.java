package cs276.assignments;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import cs276.util.IndexCompression;
import cs276.util.Utils;

public class VBIndex implements BaseIndex {

  @Override
  public PostingList readPosting(FileChannel fc) throws IOException {
    /*
     * TODO: Your code here
     */
    ByteBuffer buffer = ByteBuffer.allocate(2 * INT_SIZE);
    if (fc.read(buffer) == -1) {
      return null;
    }
    buffer.flip();
    int termId = buffer.getInt();
    int length = buffer.getInt();

    byte[] vbCode = new byte[length];
    buffer = ByteBuffer.wrap(vbCode);
    fc.read(buffer);

    List<Integer> gaps = new ArrayList<>();
    int[] numberEndIndex = new int[2];
    int startIndex = 0;
    while (startIndex < length) {
      IndexCompression.VBDecodeInteger(vbCode, startIndex, numberEndIndex);
      gaps.add(numberEndIndex[0]);
      startIndex = numberEndIndex[1];
    }

    int[] postings = Utils.integerListToIntArray(gaps);
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
    byte[] vbCode = IndexCompression.VBEncode(gaps, gaps.length).toByteArray();

    ByteBuffer buffer = ByteBuffer.allocate(2 * INT_SIZE + vbCode.length);
    buffer.putInt(p.getTermId()).putInt(vbCode.length).put(vbCode);
    buffer.flip();
    fc.write(buffer);
  }
}
