import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;


public class SentenceParser {
	Parser parser;
	public SentenceParser() throws FileNotFoundException {
		ParserModel model = null;
		InputStream modelIn = new FileInputStream("en-parser-chunking.bin");
		try {
			model = new ParserModel(modelIn);
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
		parser = ParserFactory.create(model);

	}
	
	public Parse[] getParses(String text, int numberDesired) {
		Tokenizer tok = null;
		tok = new Tokenizer();
		String[] parseTokens = tok.tokenize(text.toString());
		text = tok.fixUp(text);
		ParserTool.parseLine(text, parser, numberDesired)[0].show();
		return ParserTool.parseLine(text, parser, numberDesired);
	}

}
