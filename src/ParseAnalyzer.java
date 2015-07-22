import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;

import opennlp.tools.parser.Parse;
import opennlp.tools.parser.lang.en.HeadRules;
import opennlp.tools.util.Span;


public class ParseAnalyzer {
	Parse parse;
	ArrayList<Parse> tempRemovedPhrases = new ArrayList<Parse>();
	ArrayList<Parse> tempRemovedPhrases2 = new ArrayList<Parse>();
	ArrayList<Parse> tempRemovedPhrases3 = new ArrayList<Parse>();

	//ArrayList<>
	public static final String[] REMOVABLE_PHRASE_TYPES = {"PP", "ADVP", "PPART", "INTJ", "PRN"};
	public static final String[] TO_BE = {"am", "are", "is", "was", "were", "be", "been", "being", "'m", "'s", "'re"};
	public static final String[] HELPING_VERBS = {"have", "has", "had",
		"shall", "will", "do", "does", "did", "may", "must", "might", "can", 
		"could", "would", "should", "am", "are", "is", "was", "were", "be", "been", "being", "'m", "'s", "'re"};
	public static final String[] SS_WORDS = {"NN", "NNS", "NNP", "NNPS", "PRP", "VBGG"};
	public static final String[] NOUN_PHRASES = {"NP", "INF"};
	public static final String[] VERB_WORDS = {"VB", "VBG", "VBD", "VBZ", "VBP", "VBN", "MD"};
	public static final String[] TO_BECOME = {"become", "becomes", "became"};
	public static final String[] TO_REMAIN = {"remain", "remains", "remained"};
	public static final String[] ADJ_TYPES = {"ADJP", "JJ", "CD", "PRP$"};
	public boolean notLinking = false;
	public boolean dontSearchForObjects = false;
	Tokenizer tok = new Tokenizer();


	public ParseAnalyzer(Parse datParse, boolean modify) {
		this.parse = datParse;
		if (modify) {
			doInfinitives(parse);
			try {
				VBGSplitter(parse, parse.getParent().indexOf(parse), parse.getParent());
			}
			catch (NullPointerException e) {
				VBGSplitter(parse.getChildren()[0], 0, parse);
			}
			parse = removeSoloPunctuation(parse);
			changeUselessSTags(parse);
		}

	}

	public Parse[] subjectPredicateForm() {
		Parse[] SPForm = new Parse[50];
		boolean keepGoing = true;
		parse = removeSoloPunctuation(parse);
		Parse currentParse = parse;
		while (keepGoing) {
			currentParse = removeSoloPunctuation(currentParse);
			int numberChildren = 0;
			Parse[] children = currentParse.getChildren();
			for (Parse child : children) {
				if (Arrays.asList(REMOVABLE_PHRASE_TYPES).contains(child.getType())) {
					tempRemovedPhrases.add(child);
				}
				else numberChildren++;
			}
			if (numberChildren == 0) {
				break;
			}
			if (numberChildren < 2 && numberChildren > 0) {
				currentParse = currentParse.getChildren()[0];
			}
			else if (numberChildren == 2) {
				if (currentParse.getTagNodes()[0].getType().equals("EX")) {
					currentParse = currentParse.getChildren()[1];
					dontSearchForObjects = true;
					continue;
				}
				Parse subject = null;
				Parse verb = null;
				for (Parse child : currentParse.getChildren()) {

					if (Arrays.asList(VERB_WORDS).contains(child.getType()) || child.getType().equals("VP")) {
						verb = child;
					}
					else if (Arrays.asList(SS_WORDS).contains(child.getType()) || Arrays.asList(NOUN_PHRASES).contains(child.getType())) {
						subject = child;
					}
				}
				if (subject == null || verb == null) {
					System.out.println("Failed to properly split sentence into subject predicate formzz.");
					currentParse.show();
				}
				SPForm[0] = subject;
				SPForm[1] = verb;

				keepGoing = false;
			}

			else {
				if (numberChildren > 2) {
					System.out.println("Sentence: ");
					for (Parse child : currentParse.getChildren()) {
						child.show();
					}
					System.out.println();
					System.out.println("Failed to properly split sentence into subject predicate form.");
					keepGoing = false;
				}
			}

		}

		return SPForm;

	}

	//public Parse[] 

	public static Parse removeSoloPunctuation(Parse theParse) {
		Parse[] children = theParse.getChildren();
		ArrayList<Integer> toBeRemoved = new ArrayList<Integer>();
		for (int i = children.length - 1; i >= 0; i--) {
			if (!Character.isLetter(children[i].getCoveredText().charAt(0)) && !Character.isLetter(children[i].getType().charAt(0))) toBeRemoved.add(i);
		}
		for (int i : toBeRemoved) {
			theParse.remove(i);
		}
		return theParse;
	}

	public void VBGSplitter(Parse par, int index, Parse parent) {

		if (par.getType().equals("VBG")) {
			try {
				Parse before = parent.getParent().getChildren()[parent.getParent().indexOf(parent) -1];
				if (before.getType().equals(",") || Arrays.asList(NOUN_PHRASES).contains(before.getType())) {
					par.setType("VBGPP");
					parent.getChildren()[index].setType("PPART");
					parent.setType("PPART");
					return;
				}
			}
			catch (Exception e) {
				;
			}
			try {
				Parse after = parent.getParent().getChildren()[parent.getParent().indexOf(parent) + 1];
				if (after.getType().equals(",") || Arrays.asList(NOUN_PHRASES).contains(after.getType())) {
					par.setType("VBGPP");
					parent.setType("PPART");
					return;
				}
			}
			catch (Exception e) {
				;
			}
			try {
				Parse[] tagNodes = par.getParent().getParent().getTagNodes();
				Parse lastWord = null;
				for (int i = 0; i < tagNodes.length; i++) {
					if (tagNodes[i].equals(par)) {
						lastWord = tagNodes[i-1];
					}
				}
				if (Arrays.asList(TO_BE).contains(lastWord.getCoveredText())) {
					return;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			par.setType("VBGG");
			parent.setType("NP");
			return;


		}

		else {

			if (par.getChildCount() > 0) {
				boolean yes = false;
				for (Parse child : par.getChildren()) {
					VBGSplitter(child, par.indexOf(child), par);


				}
			}
		}

	}

	public void doInfinitives(Parse par) {
		if (par.getType().equals("VP")) {
			if (par.getTagNodes()[0].getType().equals("TO")) {
				par.setType("INF");
			}
		}
		if (par.getChildCount() > 0) {
			for (Parse child : par.getChildren()) {
				doInfinitives(child);
			}
		}
	}


	public static void changeUselessSTags(Parse par) {
		if (par.getType().equals("S")) {

			Parse[] children = par.getChildren();
			ArrayList<Parse> realChildren = new ArrayList<Parse>();
			for (Parse child : children) {
				if (child.getType().equals("ADJP")||child.getType().equals("ADVP")) {
					;
				}
				else{
					realChildren.add(child);
				}

			}

			if (realChildren.size()==1) {
				par.setType(realChildren.get(0).getType());
			}
			else if (realChildren.size() ==0) {
				par.setType(par.getChildren()[0].getType());
			}
		}
		if (par.getChildCount() > 0) {
			for (Parse child : par.getChildren()) {
				changeUselessSTags(child);
			}
		}

	}





	public Parse getSimpleSubject(Parse subject) {
		subject = removeSoloPunctuation(subject);
		boolean assign = true;
		Parse simpleSubject = null;
		if (Arrays.asList(SS_WORDS).contains(subject.getType())) {
			if (assign) simpleSubject = subject;

		}
		else if (Arrays.asList(NOUN_PHRASES).contains(subject.getType())) {
			if (assign) {
				for (Parse child : subject.getChildren()) {
					if (assign) {
						Parse temp  = getSimpleSubject(child);	
						if (temp != null){
							if (assign) simpleSubject = temp;
							assign = false;
						}
					}
					else {
						tempRemovedPhrases.add(child);
					}
				}
			}
			else tempRemovedPhrases.add(subject);
		}
		else {
			tempRemovedPhrases.add(subject);
		}

		return simpleSubject;
	}


	public Parse getSimpleSubject() {
		return getSimpleSubject(parse);
	}

	public ArrayList<Parse> getVerbs(Parse predicate) {
		ArrayList<Parse> verbs = new ArrayList<Parse>();
		Parse[] children = predicate.getChildren();
		boolean keepDiving = true;
		boolean allFairGame = false;


		if (Arrays.asList(VERB_WORDS).contains(predicate.getType())) {
			verbs.add(predicate);
			if (!Arrays.asList(HELPING_VERBS).contains(predicate.getCoveredText())) {
				keepDiving = false;
			}
		}
		else if (Arrays.asList(REMOVABLE_PHRASE_TYPES).contains(predicate.getType())) {
			allFairGame = true;
			tempRemovedPhrases.add(predicate);


		}
		else if (predicate.getType().equals("VP") && keepDiving) {
			for (Parse child : predicate.getChildren()) {
				verbs.addAll(getVerbs(child));
			}
		}
		else {
			if (allFairGame) {
				tempRemovedPhrases.add(predicate);
			}
		}

		return verbs;
	}

	public ArrayList<Parse> getVerbs() {
		return getVerbs(parse);
	}

	public Parse[] getVerbObjects(ArrayList<Parse> verbs, Parse predicate) {
		tempRemovedPhrases.clear();
		Parse lastVerb = verbs.get(verbs.size() -1);
		Parse parent = predicate;
		while (true) {
			Parse child = parent.getChildren()[parent.getChildCount() - 1];
			if (child.getType().equals("VP")) {
				parent = child;
			}
			else break;
		}

		Parse[] children = parent.getChildren();
		int verbIndex = parent.indexOf(lastVerb);
		int currentIndex = verbIndex;
		Parse[] returned = new Parse[2];
		Parse nextPhrase = null;
		Parse lastObject = null;
		while (true) {

			currentIndex++;
			try {
				System.out.println("nextphrase");
				nextPhrase = children[currentIndex];
				nextPhrase.show();
			}
			catch (IndexOutOfBoundsException e) {
				return returned;
			}

			if (nextPhrase.getType().equals("PP")) {
				if (lastObject!=null) {
					tempRemovedPhrases.add(nextPhrase);
				}
			}

			else if (Arrays.asList(NOUN_PHRASES).contains(nextPhrase.getType())) {
				Parse followingPhrase = null;
				currentIndex++;
				try {
					followingPhrase = children[verbIndex + 2];

					if (followingPhrase.getType().equals("NP")) {
						returned[0] = followingPhrase;
						returned[1] = nextPhrase;
					}
					else {
						returned[0] = nextPhrase;
						tempRemovedPhrases.add(followingPhrase);

					}
				}
				catch (IndexOutOfBoundsException e) {
					returned[0] = nextPhrase;
				}
			}

			else if (nextPhrase.getType().equals("S")) {
				returned[0] = nextPhrase;
			}
			else if (Arrays.asList(ADJ_TYPES).contains(nextPhrase.getType())) {
				returned[0] = nextPhrase;
			}

			else {
				tempRemovedPhrases3.add(nextPhrase);
			}
			//}
		}
	}




}
