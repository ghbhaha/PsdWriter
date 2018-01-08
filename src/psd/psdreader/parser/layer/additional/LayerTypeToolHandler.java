package psd.psdreader.parser.layer.additional;

import psd.psdreader.parser.object.PsdDescriptor;

public interface LayerTypeToolHandler {

	public void typeToolTransformParsed(Matrix transform);
	public void typeToolDescriptorParsed(int version, PsdDescriptor descriptor);

}
