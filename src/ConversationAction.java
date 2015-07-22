import java.util.ArrayList;
import java.util.Arrays;

import edu.mit.jwi.item.POS;

import opennlp.tools.parser.Parse;

public class ConversationAction {
	public static ArrayList<ConversationAction> conversationActions = new ArrayList<ConversationAction>();
	int id;
	String verb = null;
	String tense = null;
	ConversationObject subject = null;
	ConversationObject directObject = null;
	ConversationObject indirectObject = null;
	Property predicateAdj = null;
	ConversationObject predicateNoun = null;
	ConversationObject nounComplement = null;
	Property adjComplement = null;
	ConversationAction verbComplement = null;
	ArrayList<Adverbial> modifiers = new ArrayList<Adverbial>();

	public static ConversationAction findConversationAction(Parse action, ArrayList<Parse> modifiers, ConversationObject subject, Parse splitHelpingVerb, ArrayList<ConversationAction> searchArea) {
		System.out.println("Finding CA");
		int size = ConversationObject.conversationObjects.size();
		int actionSize = conversationActions.size();
		int subjectActionSize = (subject != null)? subject.actions.size() : 0;
		int searchAreaSize = searchArea.size();
		ConversationAction temp = new ConversationAction(action, modifiers, subject, splitHelpingVerb);
		temp.print();
		for (int i = searchAreaSize - 1; i >= 0; i--) {
			System.out.println("Searching...");
			if (temp.qualifiesAs(searchArea.get(i))) {
				System.out.println("Found match!");
				searchArea.get(i).print();
				int currentSize = ConversationObject.conversationObjects.size();
				int currentActionSize = conversationActions.size();
				int currentSubjectActionSize = (subject != null)? subject.actions.size() : 0;
				for (int j = 0; j < currentSize - size; j++) {
					ConversationObject.conversationObjects.remove(ConversationObject.conversationObjects.size() -1);
				}
				for (int j = 0; j < currentActionSize - actionSize; j++) {
					conversationActions.remove(conversationActions.size() -1);
				}
				for (int j = 0; j < currentSubjectActionSize - subjectActionSize; j++) {
					subject.actions.remove(subject.actions.size() -1);
				}
				return searchArea.get(i);
			}
			int currentSize = ConversationObject.conversationObjects.size();
			int currentActionSize = conversationActions.size();
			int currentSubjectActionSize = (subject != null)? subject.actions.size() : 0;

			for (int j = 0; j < currentSize - size; j++) {
				ConversationObject.conversationObjects.remove(ConversationObject.conversationObjects.size() -1);
			}
			for (int j = 0; j < currentActionSize - actionSize; j++) {
				conversationActions.remove(conversationActions.size() -1);
			}
			for (int j = 0; j < currentSubjectActionSize - subjectActionSize; j++) {
				subject.actions.remove(subject.actions.size() -1);
			}
		}
		return null;
	}

	public static ConversationAction findConversationAction(Parse action, ArrayList<Parse> modifiers, ConversationObject subject, Parse splitHelpingVerb) {
		return findConversationAction(action, modifiers, subject, splitHelpingVerb, conversationActions);
	}



	public void print() {
		System.out.println("NEW ACTION");
		System.out.println("id: " + id);
		System.out.println("verb: " + verb);
		System.out.println("tense: " + tense);
		if (subject !=null ) System.out.println("subject: " + subject.id);
		if (directObject != null) System.out.println("direct object: " + directObject.id);
		if (indirectObject != null) System.out.println("indirect object: " + indirectObject.id);
		if (nounComplement != null) System.out.println("noun complement: " + nounComplement.id);
		if (adjComplement != null) {
			System.out.println("adj complement: ");
			adjComplement.print();
		}
		if (predicateAdj != null) {
			System.out.println("Predicate Adj: ");
			predicateAdj.print();
		}
		if (predicateNoun != null) {
			System.out.println("Predicate Noun: ");
			predicateAdj.print();
		}
		if (verbComplement != null) System.out.println("verb Complement: " + verbComplement.id);
		if (modifiers.size() > 0) System.out.println("modifiers: ");
		for (Adverbial modifier : modifiers) {
			modifier.print();
		}
		System.out.println();
	}
	public boolean qualifiesAs(ConversationAction other) {
		if (!verb.equals(other.verb)) {
			return false;
		}
		if (!tense.equals(other.tense)) {
			if (!other.tense.equals("forever")) return false;
		}
		if (directObject != null) {
			if (!directObject.qualifiesAs(other.directObject, true)) { //TODO: explain why true?
				return false;
			}
		}
		if (indirectObject != null) {
			if (!indirectObject.qualifiesAs(other.indirectObject, true)) { //TODO: explain why true?
				return false;
			}
		}
		if (nounComplement != null) {
			if (!nounComplement.qualifiesAs(other.nounComplement, true)) { //TODO: explain why true?
				return false;
			}
		}
		if (adjComplement != null) {
			if (!adjComplement.qualifiesAs(other.adjComplement)) {
				return false;
			}
		}
		if (verbComplement != null) {
			if (!verbComplement.qualifiesAs(other.verbComplement)) {
				return false;
			}
		}
		if (predicateAdj != null) {
			if (!predicateAdj.qualifiesAs(other.predicateAdj)) {
				return false;
			}
		}
		if (predicateNoun != null) {
			if (!predicateNoun.qualifiesAs(other.predicateNoun, true)) { //TODO: explain why true?
				return false;
			}
		}
		for (Adverbial mod : modifiers) {
			boolean foundMatch = false;
			for (Adverbial otherMod : other.modifiers) {
				if (mod.qualifiesAs(otherMod)) {
					foundMatch = true;
				}
			}
			if (!foundMatch) return false;
		}
		return true;
	}

	public ConversationAction(Property prop, ArrayList<Parse> modifiers, ConversationObject subj, Parse splitHelpingVerb) {
		this.subject = subj;
		for (Parse modifier : modifiers) {
			Adverbial mod = new Adverbial(modifier);
			this.modifiers.add(mod);
		}
		this.tense = "forever";
		this.verb = "be";
		predicateAdj = prop;
		id = conversationActions.size();
		conversationActions.add(this);

	}
	public ConversationAction(ConversationObject PN , ArrayList<Parse> modifiers, ConversationObject subj, Parse splitHelpingVerb) {
		this.subject = subj;
		for (Parse modifier : modifiers) {
			Adverbial mod = new Adverbial(modifier);
			this.modifiers.add(mod);
		}
		this.tense = "forever";
		this.verb = "be";
		predicateNoun = PN;
		id = conversationActions.size();
		conversationActions.add(this);

	}
	public ConversationAction(Parse predicate, ArrayList<Parse> modifiers, ConversationObject subj, Parse splitHelpingVerb) {
		this.subject = subj;
		ParseAnalyzer predicateAnalyzer = new ParseAnalyzer(predicate, false);
		ArrayList<Parse> verbs = predicateAnalyzer.getVerbs();
		if (splitHelpingVerb != null){
			if (splitHelpingVerb.isPosTag()) verbs.add(0, splitHelpingVerb);
			else {
				for (int i = splitHelpingVerb.getTagNodes().length -1 ; i >= 0; i--) {
					verbs.add(0, splitHelpingVerb.getTagNodes()[i]);
				}
			}
		}
		Parse mainVerb = verbs.get(verbs.size() -1);
		ArrayList<Parse> actionModifiers = new ArrayList<Parse>();
		actionModifiers.addAll(modifiers);
		actionModifiers.addAll(predicateAnalyzer.tempRemovedPhrases);
		Parse[] objects = predicateAnalyzer.getVerbObjects(verbs, predicate);
		actionModifiers.addAll(predicateAnalyzer.tempRemovedPhrases3);
		//TODO: make this not suck - they're ambiguous dammit
		actionModifiers.addAll(predicateAnalyzer.tempRemovedPhrases);
		Parse DO = objects[0];
		Parse IO = objects[1];
		verb = Stemmer.stem(mainVerb.getCoveredText(), POS.VERB);
		setTense(verbs, verbs.size() -1);
		if (DO!=null) analyzeDO(DO);
		if (IO != null) indirectObject = ConversationObject.createConversationObject(IO);
		for (Parse modifier : actionModifiers) {
			Adverbial mod = new Adverbial(modifier);
			this.modifiers.add(mod);
		}
		if (this.subject != null) this.subject.addAction(this);
		if (this.directObject != null) this.directObject.addReceivedAction(this);
		if (this.indirectObject != null) this.indirectObject.addIndirectReceivedAction(this);
		id = conversationActions.size();
		conversationActions.add(this);

	}

	public void analyzeDO(Parse directObject) {
		Parse simpleDirectObject = directObject;
		if (Arrays.asList(ParseAnalyzer.ADJ_TYPES).contains(directObject.getType())) {
			predicateAdj = new Property(directObject);
			return;
		}
		Parse AC = null;
		Parse VC = null;
		Parse NC = null;
		if (Arrays.asList(ParseAnalyzer.NOUN_PHRASES).contains(directObject.getType())) {
			if (Arrays.asList(ParseAnalyzer.NOUN_PHRASES).contains(directObject.getChildren()[0].getType())) {
				try {
					if (Arrays.asList(ParseAnalyzer.ADJ_TYPES).contains(directObject.getChildren()[1].getType())) {
						AC = directObject.getChildren()[1];
						simpleDirectObject = directObject.getChildren()[0];
					}
				}
				catch (IndexOutOfBoundsException e) {
					;
				}
			}
		}
		else if (directObject.getType().equals("S")) {
			if (Arrays.asList(ParseAnalyzer.NOUN_PHRASES).contains(directObject.getChildren()[0].getType())) {
				try {
					if (directObject.getChildren()[1].getType().equals("VP") || 
							Arrays.asList(ParseAnalyzer.VERB_WORDS).contains(directObject.getChildren()[1].getType())) {
						VC = directObject.getChildren()[1];
						simpleDirectObject = directObject.getChildren()[0];
					}
				}
				catch (IndexOutOfBoundsException e) {
					;
				}
			}
		}
		this.directObject = ConversationObject.createConversationObject(simpleDirectObject);
		if (AC!=null) adjComplement = new Property(AC);
		if (NC!= null) nounComplement = ConversationObject.createConversationObject(NC);
		if (VC != null) verbComplement = new ConversationAction(VC, new ArrayList<Parse>(), this.directObject, null);
	}


	public void setTense(ArrayList<Parse> verbs, int index) {
		
		Parse mainVerb = verbs.get(index);
		if (mainVerb.getType().equals("VBD")) {
			tense = "past";
		}
		else if (mainVerb.getType().equals("VBZ")) {
			tense = "present";
		}
		else if (mainVerb.getType().equals("VBN")) {
			tense = "past";
		}
		else if (mainVerb.getType().equals("VBP")) {
			tense = "present";
		}
		else if (mainVerb.getType().equals("VBG")) {
			setTense(verbs, index -1);
		}
		else if (mainVerb.getType().equals("VB")) {
			if (index == 0) tense = "unknown";
			else setTense(verbs, index -1);
		}
		else if (mainVerb.getType().equals("MD")) {
			tense = analyzeModalTense(mainVerb);
		}
		else {
			System.err.println("Wacko tense  " + mainVerb.getType());
		}
	}

	public String analyzeModalTense(Parse modal) {
		return "future"; //TODO: make this useful
	}

}
