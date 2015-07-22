import java.io.File;
import java.io.IOException;
import java.net.URL;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;


public class Stemmer {
	public static IDictionary dict;
	public static WordnetStemmer stemmer;
	public static void initialize() throws IOException {
		String wnhome = System.getenv("WNHOME");
		String path = wnhome + File.separator + "dict";
		URL url = new URL("file", null , "dict" );
		// construct the dictionary object and open it
		dict = new Dictionary(url);
		dict.open();
		stemmer = new WordnetStemmer(dict);
	}
	public static String stem(String word, POS pos) {
		return stemmer.findStems(word, pos).get(0);
	}

}
