import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.Span;


public class Chunker {
	ChunkerME chunker;
	public Chunker() throws FileNotFoundException {
		ChunkerModel model = null;
		InputStream modelIn = new FileInputStream("en-chunker.bin");
		try {
		 
		  model = new ChunkerModel(modelIn);
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
		chunker = new ChunkerME(model);
	}
	
	public String[] chunk(String[] text, String[] tags) {
		return chunker.chunk(text,  tags);
	}
	
	public Span[] chunkAsSpans(String[]text, String[] tags) {
		return chunker.chunkAsSpans(text,  tags);
	}
	
	//must be called immediately after chunk() to be useful
	public double[] probs() {
		return chunker.probs();
	}
	
	public Sequence[] topKSequences(String[] text, String[] tags) {
		return chunker.topKSequences(text, tags);
	}
	

}
