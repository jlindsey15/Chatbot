import java.util.ArrayList;

import opennlp.tools.parser.Parse;


public class Adverbial {
	String type;
	PrepPhrase prepPhrase = null;
	PartPhrase partPhrase = null;
	ArrayList<String> adverbs = new ArrayList<String>();
	public void print() {
		System.out.println("NEW MODIFIER");
		System.out.println("type: " + type);
		if (prepPhrase != null) prepPhrase.print();
		if (partPhrase != null) partPhrase.print();
		if (adverbs.size() > 0) {
			for (String adv : adverbs) {
				System.out.print(adv + ", ");
			}
		}
		System.out.println();
	}
	public Adverbial(Parse adv) {
		if (adv.getType().equals("ADVP")) {
			for (Parse node : adv.getTagNodes()) {
				if (node.getType().equals("RB")) {
					adverbs.add(node.getCoveredText());
				}
			}
			type = "adv";
		}
		else if (adv.getType().equals("PP")) {
			prepPhrase = new PrepPhrase(adv);
			type = "prep";
		}
		else if (adv.getType().equals("PPART")) {
			partPhrase = new PartPhrase(adv);
			type = "part";
		}
	}
	public boolean qualifiesAs(Adverbial other) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
