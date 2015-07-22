import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.Sequence;


public class POSTagger {
	POSTaggerME tagger;
	public POSTagger() throws FileNotFoundException {
		POSModel model = null;
		InputStream modelIn = new FileInputStream("en-pos-perceptron.bin");
		try {
		 
		  model = new POSModel(modelIn);
		}
		catch (IOException e) {
		  // Model loading failed, handle the error
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
		tagger = new POSTaggerME(model);
	}
	
	public String[] getTags(String[] text) {
		String[] tags = tagger.tag(text);
		return tags;
	}
	
	//must be called immediately after getTags() to be useful
	public double[] getLastProbs() {
		return tagger.probs();
	}
	
	public Sequence[] topKSequences(String[] text) {
		return tagger.topKSequences(text);
	}
	

}
