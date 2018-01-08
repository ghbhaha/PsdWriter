package psd.psdwiter;


import psd.psdwiter.bean.Layer;
import psd.psdwiter.util.RleCompassion;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LayerMaskWriter implements WriterFace {


    public List<Layer> layers = new ArrayList<>();

    public static void main(String[] args) {
        LayerMaskWriter layerMaskWriter = new LayerMaskWriter();
        layerMaskWriter.layers.add(new Layer());
        layerMaskWriter.toByte();
    }

    @Override
    public byte[] toByte() {

        //总长度
        int allSize = 0;
        //图层信息  Layer info section
        List<byte[]> bytesList = new ArrayList<>();


        List<byte[]> bytesDataList = new ArrayList<>();

        //压缩
        int compassion = 1;

        //图层数据
        for (Layer layer : layers) {
            BufferedImage bimg = layer.getImage();

            ByteBuffer composeBuffer = ByteBuffer.allocate(2);
            composeBuffer.putShort((short) compassion);

            int[] rgbas = new int[bimg.getWidth() * bimg.getHeight()];
            for (int i = 0; i < bimg.getHeight(); i++) {
                for (int j = 0; j < bimg.getWidth(); j++) {
                    rgbas[i * bimg.getWidth() + j] = bimg.getRGB(j, i);
                }
            }

            ByteBuffer channelInfo = ByteBuffer.allocate(4 * 6);
            for (int j = 0; j < 4; j++) {
                byte[] chanelData = new byte[rgbas.length];
                for (int index = 0; index < rgbas.length; index++) {
                    Color color = new Color(rgbas[index], true);
                    if (j == 0) {
                        chanelData[index] = (byte) color.getRed();
                    } else if (j == 1) {
                        chanelData[index] = (byte) color.getGreen();
                    } else if (j == 2) {
                        chanelData[index] = (byte) color.getBlue();
                    } else if (j == 3) {
                        chanelData[index] = (byte) color.getAlpha();
                    }
                    //  chanelData[index] = chanelData[index] == 0? (byte) 255 : chanelData[index];
                }

                if (compassion == 1) {
                    channelInfo.putShort((short) (j == 3 ? -1 : j));
                    int length = 2 + 2 * layer.getHeight();
                    //压缩字段
                    bytesDataList.add(composeBuffer.array());
                    int pos = bytesDataList.size();
                    ByteBuffer lengthBuffer = ByteBuffer.allocate(2 * layer.getHeight());
                    for (int i = 0; i < layer.getHeight(); i++) {
                        byte[] colors = RleCompassion.compassion(Arrays.copyOfRange(chanelData, i * layer.getWidth(), (i + 1) * layer.getWidth()));
                        bytesDataList.add(colors);
                        length += colors.length;
                        lengthBuffer.putShort((short) colors.length);
                    }
                    channelInfo.putInt(length);
                    bytesDataList.add(pos, lengthBuffer.array());
                } else {
                    channelInfo.putShort((short) (j == 3 ? -1 : j));
                    channelInfo.putInt(2 + layer.getWidth() * layer.getHeight());
                    layer.setChanelInfo(channelInfo);
                    bytesDataList.add(composeBuffer.array());
                    bytesDataList.add(chanelData);
                }

            }
            layer.setChanelInfo(channelInfo);
        }

        //图层数量
        ByteBuffer layerCount = ByteBuffer.allocate(2);
        layerCount.putShort((short) layers.size());
        bytesList.add(layerCount.array());

        for (Layer layer : layers) {
            ByteBuffer byteBufferBounds = ByteBuffer.allocate(16);
            byteBufferBounds.putInt(layer.getTop());
            byteBufferBounds.putInt(layer.getLeft());
            byteBufferBounds.putInt(layer.getBottom());
            byteBufferBounds.putInt(layer.getRight());
            bytesList.add(byteBufferBounds.array());

            ByteBuffer channelNum = ByteBuffer.allocate(2);
            //4通道 a：–1 r：0 g：1 b：2
            channelNum.putShort((short) 4);
            bytesList.add(channelNum.array());


            //通道信息 每个通道 6字节
            bytesList.add(layer.getChanelInfo().array());


            //混合
            ByteBuffer blendSign = ByteBuffer.allocate(4);
            blendSign.put("8BIM".getBytes());
            bytesList.add(blendSign.array());

            ByteBuffer blendMode = ByteBuffer.allocate(4);
            blendMode.put("norm".getBytes());
            bytesList.add(blendMode.array());

            //透明
            byte[] Opacity = {(byte) 255};
            bytesList.add(Opacity);
            //裁剪
            byte[] Clipping = {(byte) 0};
            bytesList.add(Clipping);
            //Flags
            byte flag = 0;
            if (layer.isTransparencyProtected()) {
                flag |= 1;
            }
            if (!layer.isVisible()) {
                flag |= 1 << 1;
            }
            if (layer.isObsolete()) {
                flag |= 1 << 2;
            }
            if (layer.isPixelDataIrrelevantValueUseful()) {
                flag |= 1 << 3;
            }
            if (layer.isPixelDataIrrelevant()) {
                flag |= 1 << 4;
            }

            byte[] Flags = {flag};
            bytesList.add(Flags);
            //fillers
            byte[] filler = {(byte) 0};
            bytesList.add(filler);

            //ExtraDataSize 没mask 4字节 没混合 4字节 3.0没有Adjustment layer infos
            ByteBuffer ExtraDataSize = ByteBuffer.allocate(4);
            byte[] Layermask = new byte[4];

            //不需要混合 长度 Composite gray blend source + Composite gray blend destination range
            ByteBuffer LayerblendingRangeBuffer = ByteBuffer.allocate(12);
            LayerblendingRangeBuffer.putInt(8);
            LayerblendingRangeBuffer.putShort((short) 0);
            LayerblendingRangeBuffer.putShort((short) 0);
            LayerblendingRangeBuffer.putShort((short) 0);
            LayerblendingRangeBuffer.putShort((short) 0);
            byte[] LayerblendingRange = LayerblendingRangeBuffer.array();

            // Layer name: Pascal string, padded to a multiple of 4 bytes.
            String name = layer.getName();
            int size = name.getBytes().length & 0xFF;
            size = ((size + 1 + 3) & ~0x03);
            ByteBuffer nameBytesBuffer = ByteBuffer.allocate(size);
            nameBytesBuffer.put((byte) (size - 1));

            nameBytesBuffer.put(name.getBytes());
            byte[] nameBytes = nameBytesBuffer.array();

            ExtraDataSize.putInt(Layermask.length + LayerblendingRange.length + nameBytes.length);

            bytesList.add(ExtraDataSize.array());
            bytesList.add(Layermask);
            bytesList.add(LayerblendingRange);
            bytesList.add(nameBytes);
        }


        bytesList.addAll(bytesDataList);


        for (byte[] bytes : bytesList) {
            allSize += bytes.length;
        }

        // layer mask 长度描述 + layer 长度描述 + layer数据长度 + mask长度描述
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + 4 + allSize + 4);
        byteBuffer.putInt(4 + allSize + 4);
        byteBuffer.putInt(allSize);
        for (byte[] bytes : bytesList) {
            byteBuffer.put(bytes);
        }
        byteBuffer.putInt(0);

        bytesList.clear();
        return byteBuffer.array();
    }


    @Override
    public void writeBytes(BufferedOutputStream fileOutputStream) throws IOException {
        fileOutputStream.write(toByte());
    }

}
