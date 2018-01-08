
package psd.psdwiter.bean;

import psd.psdreader.parser.layer.LayerType;
import sun.nio.ch.IOUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Layer {
    private int top = 0;
    private int left = 0;
    private int bottom = 1920;
    private int right = 1080;

    private int alpha = 255;
    boolean transparencyProtected = true;
    boolean visible = true;
    boolean obsolete = false;
    boolean isPixelDataIrrelevantValueUseful = false;
    boolean pixelDataIrrelevant = false;

    private String name;

    private BufferedImage image;
    private LayerType type = LayerType.NORMAL;

    ByteBuffer chanelInfo;

    public Layer(String path){
        try {
            File file = new File(path);
            image = ImageIO.read(file);
            name=  file.getName().replace(".png","");
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    public Layer(){

    }
    public int getWidth(){
        return getRight() - getLeft();
    }

    public int getHeight(){
        return getBottom() - getTop();
    }
    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public LayerType getType() {
        return type;
    }

    public void setType(LayerType type) {
        this.type = type;
    }

    public ByteBuffer getChanelInfo() {
        return chanelInfo;
    }

    public void setChanelInfo(ByteBuffer chanelInfo) {
        this.chanelInfo = chanelInfo;
    }

    public boolean isTransparencyProtected() {
        return transparencyProtected;
    }

    public void setTransparencyProtected(boolean transparencyProtected) {
        this.transparencyProtected = transparencyProtected;
    }

    public boolean isObsolete() {
        return obsolete;
    }

    public void setObsolete(boolean obsolete) {
        this.obsolete = obsolete;
    }

    public boolean isPixelDataIrrelevantValueUseful() {
        return isPixelDataIrrelevantValueUseful;
    }

    public void setPixelDataIrrelevantValueUseful(boolean pixelDataIrrelevantValueUseful) {
        isPixelDataIrrelevantValueUseful = pixelDataIrrelevantValueUseful;
    }

    public boolean isPixelDataIrrelevant() {
        return pixelDataIrrelevant;
    }

    public void setPixelDataIrrelevant(boolean pixelDataIrrelevant) {
        this.pixelDataIrrelevant = pixelDataIrrelevant;
    }
}
