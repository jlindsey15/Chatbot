import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;


public class SentenceDetector {
	
	SentenceDetectorME sentenceDetector;
	public SentenceDetector() throws FileNotFoundException {
		SentenceModel model = null;
		InputStream modelIn = new FileInputStream("en-sent.bin");
		try {
			model = new SentenceModel(modelIn);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				}
				catch (IOException e) {
				}
			}
		}
		sentenceDetector = new SentenceDetectorME(model);
	}
	
	public String[] detectSentences(String text) {
		String[] sentences = sentenceDetector.sentDetect(text);
		return sentences;
	}
}
