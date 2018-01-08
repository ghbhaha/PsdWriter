package psd;

import psd.psdwiter.*;

import java.io.*;

public class PsdWriter {


    public static void main(String[] args) {


        int width = 1080;
        int height = 1920;


        HeaderWriter headerWriter = new HeaderWriter(width, height);

        ColorModeWriter colorModeWriter = new ColorModeWriter();
        ImageResWriter imageResWriter = new ImageResWriter();
        LayerMaskWriter layerMaskWriter = new LayerMaskWriter();
        layerMaskWriter.layers.add(new psd.psdwiter.bean.Layer("/Users/guhaibo/Desktop/psd/layer0.png"));
        layerMaskWriter.layers.add(new psd.psdwiter.bean.Layer("/Users/guhaibo/Desktop/psd/layer1.png"));
        layerMaskWriter.layers.add(new psd.psdwiter.bean.Layer("/Users/guhaibo/Desktop/psd/layer2.png"));
        // layerMaskWriter.layers.add(new psdreader.psdwiter.bean.Layer("/Users/guhaibo/Desktop/psdreader/layer3.png"));

        ImageDataWriter imageDataWriter = new ImageDataWriter();


        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {

            fileOutputStream = new FileOutputStream(new File("/Users/guhaibo/Desktop/psd/test.psd"));
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            headerWriter.writeBytes(bufferedOutputStream);
            colorModeWriter.writeBytes(bufferedOutputStream);
            imageResWriter.writeBytes(bufferedOutputStream);
            layerMaskWriter.writeBytes(bufferedOutputStream);
            imageDataWriter.writeBytes(bufferedOutputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedOutputStream.close();
            } catch (Exception e) {
            }
            try {
                fileOutputStream.close();
            } catch (Exception e) {
            }
        }

    }
}
