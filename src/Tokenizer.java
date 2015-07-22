import java.io.*;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;


public class Tokenizer {
	TokenizerME tokenizer;

	public Tokenizer() {
		TokenizerModel model = null;
		InputStream modelIn = null;
		

		try {
			modelIn = new FileInputStream("en-token.bin");
			model = new TokenizerModel(modelIn);
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

		tokenizer = new TokenizerME(model);

	}
	public String[] tokenize(String text) {

		String tokens[] = tokenizer.tokenize(text);
		return tokens;
	}
	
	public String fixUp(String text) {
		String[] tokens = tokenize(text);
		if (tokens[0].equalsIgnoreCase("There")) {
			//if ()
		}
		String result = "";
		for (String token : tokens) {
			result += token + " ";
		}
		return result;
	}

}
