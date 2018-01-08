package psd.psdreader.util;

import psd.psdreader.parser.layer.Channel;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.List;

public class BufferedImageBuilder {

    private final List<Channel> channels;
    private final byte[][] uncompressedChannels;
    private final int width;
    private final int height;
    private int opacity = -1;

    public BufferedImageBuilder(List<Channel> channels, int width, int height) {
        this.uncompressedChannels = null;
        this.channels = channels;
        this.width = width;
        this.height = height;
    }

    public BufferedImageBuilder(byte[][] channels, int width, int height) {
        this.uncompressedChannels = channels;
        this.channels = null;
        this.width = width;
        this.height = height;
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    public BufferedImage makeImage() {
        if (width == 0 || height == 0) {
            return null;
        }

        byte[] rChannel = getChannelData(Channel.RED);
        byte[] gChannel = getChannelData(Channel.GREEN);
        byte[] bChannel = getChannelData(Channel.BLUE);
        byte[] aChannel = getChannelData(Channel.ALPHA);
        applyOpacity(aChannel);

        BufferedImage im = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] data = ((DataBufferInt) im.getRaster().getDataBuffer()).getData();
        int n = width * height - 1;
        while (n >= 0) {
            int a = aChannel[n] & 0xff;
            int r = rChannel[n] & 0xff;
            int g = gChannel[n] & 0xff;
            int b = bChannel[n] & 0xff;
            data[n] = a << 24 | r << 16 | g << 8 | b;
            n--;
        }
        return im;
    }

    private void applyOpacity(byte[] a) {
        if (opacity != -1) {
            double o = (opacity & 0xff) / 256.0;
            for (int i = 0; i < a.length; i++) {
                a[i] = (byte) ((a[i] & 0xff) * o);
            }
        }
    }

    private byte[] getChannelData(int channelId) {
        if (uncompressedChannels == null) {
            for (Channel c : channels) {
                if (channelId == c.getId() && c.getCompressedData() != null) {
                    ChannelUncompressor uncompressor = new ChannelUncompressor();
                    byte[] uncompressedChannel = uncompressor.uncompress(c.getCompressedData(), width, height);
                    if (uncompressedChannel != null) {
                        return uncompressedChannel;
                    }
                }
            }
        } else {
            if (channelId >= 0 && uncompressedChannels[channelId] != null) {
                return uncompressedChannels[channelId];
            }else if (channelId==-1){
                return uncompressedChannels[3];
            }
        }
        return fillBytes(width * height, (byte) (channelId == Channel.ALPHA ? 255 : 0));
    }

    private byte[] fillBytes(int size, byte value) {
        byte[] result = new byte[size];
        if (value != 0) {
            for (int i = 0; i < size; i++) {
                result[i] = value;
            }
        }
        return result;
    }

}
