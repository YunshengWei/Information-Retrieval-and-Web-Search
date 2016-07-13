package cs276.assignments;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class BasicIndex implements BaseIndex {
  @Override
  public PostingList readPosting(FileChannel fc) throws IOException {
    /*
     * TODO: Your code here Read and return the postings list from the given
     * file.
     */
    ByteBuffer buffer = ByteBuffer.allocate(2 * INT_SIZE);
    if (fc.read(buffer) == -1) {
      return null;
    }
    buffer.flip();
    int termId = buffer.getInt();
    int length = buffer.getInt();

    buffer = ByteBuffer.allocate(length * INT_SIZE);
    fc.read(buffer);
    buffer.flip();

    List<Integer> postings = new ArrayList<>(length);
    for (int i = 0; i < length; i++) {
      postings.add(buffer.getInt());
    }

    return new PostingList(termId, postings);
  }

  @Override
  public void writePosting(FileChannel fc, PostingList p) throws IOException {
    /*
     * TODO: Your code here Write the given postings list to the given file.
     */
    ByteBuffer buffer = ByteBuffer.allocate((2 + p.getList().size()) * INT_SIZE);
    buffer.putInt(p.getTermId()).putInt(p.getList().size());
    for (int docID : p.getList()) {
      buffer.putInt(docID);
    }
    buffer.flip();
    fc.write(buffer);
  }
}
