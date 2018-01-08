package psd.psdreader.parser.layer;

import psd.psdreader.parser.PsdInputStream;

import java.io.IOException;

public interface LayerAdditionalInformationParser {
	public void parse(PsdInputStream stream, String tag, int size) throws IOException;
}
