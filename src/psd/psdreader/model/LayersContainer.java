package psd.psdreader.model;

public interface LayersContainer {
    public Layer getLayer(int index);
    public int indexOfLayer(Layer layer);
    public int getLayersCount();
}
