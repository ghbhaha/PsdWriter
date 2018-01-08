package psd.psdwiter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ColorModeWriter implements WriterFace
{
    @Override
    public byte[] toByte() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(0);
        return byteBuffer.array();
    }

    @Override
    public void writeBytes(BufferedOutputStream fileOutputStream) throws IOException{
        fileOutputStream.write(toByte());
    }
}
