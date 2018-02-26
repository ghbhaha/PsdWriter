/*
 * Copyright 2018 ghbhaha. https://github.com/ghbhaha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


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
        layerMaskWriter.layers.add(new psd.psdwiter.bean.Layer("test/1.png"));
        layerMaskWriter.layers.add(new psd.psdwiter.bean.Layer("test/2.png"));
        layerMaskWriter.layers.add(new psd.psdwiter.bean.Layer("test/3.png"));
        layerMaskWriter.layers.add(new psd.psdwiter.bean.Layer("test/4.png"));
        ImageDataWriter imageDataWriter = new ImageDataWriter("test/preview.png");

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
