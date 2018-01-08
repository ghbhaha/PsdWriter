package psd.psdwiter;

import java.io.BufferedOutputStream;
import java.io.IOException;

public interface WriterFace {
    byte[] toByte();

    void writeBytes(BufferedOutputStream fileOutputStream) throws IOException;
}
