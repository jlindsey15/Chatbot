import java.util.ArrayList;

import opennlp.tools.parser.Parse;


public class Property {
	public String type;
	public String baseProperty = null;
	public PrepPhrase prepPhrase = null;
	public PartPhrase partPhrase = null;
	public ArrayList<Adverbial> modifiers = new ArrayList<Adverbial>();
	
	public boolean qualifiesAs(Property other) {
		if (!type.equals(other.type)) {
			return false;
		}
		if (baseProperty != null && !baseProperty.equals(other.baseProperty)) {
			return false;
		}
		if (prepPhrase != null && !prepPhrase.qualifiesAs(other.prepPhrase)) {
			return false;
		}
		if (partPhrase != null && !partPhrase.qualifiesAs(other.partPhrase)) {
			return false;
		}
		for (Adverbial modifier : modifiers) {
			boolean found = false;
			for (Adverbial otherModifier : other.modifiers) {
				if (modifier.qualifiesAs(otherModifier)) {
					found = true;
				}
			}
			if (!found) return false;
		}
		return true;
	}
	public Property (Parse modifier) {
		if ((modifier.getType().equals("JJ")) || modifier.getType().equals("CD")) {
			baseProperty = modifier.getCoveredText();
			type = "adj";
		}
		else if (modifier.getType().equals("ADJP")) {
			for (Parse node : modifier.getTagNodes()) {
				if (node.getType().equals("JJ")) {
					baseProperty = node.getCoveredText();
				}
				else {
					modifiers.add(new Adverbial(node));
				}
			}
			type = "adj";
		}
		else if (modifier.getType().equals("RB")) {
			baseProperty = modifier.getCoveredText();
			type = "adv";
		}
		else if (modifier.getType().equals("PRP$")) {
			baseProperty = modifier.getCoveredText();
			type = "pos";
		}
		else if (modifier.getType().equals("PP")) {
			prepPhrase = new PrepPhrase(modifier);
			type = "prep";
		}
		else if (modifier.getType().equals("PPART")) {
			partPhrase = new PartPhrase(modifier);
			type = "part";
		}
		if (modifiers.contains("not")) {
			baseProperty = "not " + baseProperty;
			modifiers.remove("not");
		}
	}
	
	public void print() {
		System.out.println("NEW PROPERTY");
		System.out.println("Type: " + type);
		if (baseProperty != null) System.out.println("Base property: " + baseProperty);
		if (prepPhrase != null) System.out.println("Prep phrase: " + prepPhrase);
		System.out.println("Modifiers: ");
		for (Adverbial modifier : modifiers) {
			System.out.print(modifier + ", ");
		}
		System.out.println();
	}

}
