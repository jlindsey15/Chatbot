import java.util.ArrayList;
import java.util.Arrays;

import opennlp.tools.parser.Parse;


public class ConversationObject {
	public static ArrayList<ConversationObject> conversationObjects = new ArrayList<ConversationObject>();
	String simpleSubject;
	public int id;
	ArrayList<ConversationAction> actions = new ArrayList<ConversationAction>();
	ArrayList<ConversationAction> receivedActions = new ArrayList<ConversationAction>();
	ArrayList<ConversationAction> indirectReceivedActions = new ArrayList<ConversationAction>();
	ArrayList<String> determiners = new ArrayList<String>();
	ArrayList<GeneralObject> categories = new ArrayList<GeneralObject>();
	ArrayList<ConversationObject> equivalentTo = new ArrayList<ConversationObject>();
	public ConversationObject lastNounComplement = null;
	public Property lastAdjComplement = null;
	ArrayList<Property> properties = new ArrayList<Property>();

	public static ConversationObject createConversationObject(Parse subject) {

		ConversationObject findResult = findConversationObject(subject, false);
		if (findResult == null) {
			return new ConversationObject(subject);
		}
		else {
			return findResult;
		}
	}

	public static ConversationObject findConversationObject(Parse parse, boolean countIndefinite) {
		int size = conversationObjects.size();
		int actionSize = ConversationAction.conversationActions.size();
		if (parse.getType().equals("NP")) {
			ConversationObject temp = new ConversationObject(parse);
			for (int i = size - 1; i >= 0; i--) {
				if (temp.qualifiesAs(conversationObjects.get(i), countIndefinite)) {
					int currentSize = conversationObjects.size();
					int currentActionSize = ConversationAction.conversationActions.size();
					for (int j = 0; j < currentSize - size; j++) {
						conversationObjects.remove(conversationObjects.size() -1);
					}
					for (int j = 0; j < currentActionSize - actionSize; j++) {
						ConversationAction.conversationActions.remove(ConversationAction.conversationActions.size() -1);
					}
					return conversationObjects.get(i);
				}
			}
			int currentSize = conversationObjects.size();
			int currentActionSize = ConversationAction.conversationActions.size();
			for (int j = 0; j < currentSize - size; j++) {
				conversationObjects.remove(conversationObjects.size() -1);
			}
			for (int j = 0; j < currentActionSize - actionSize; j++) {
				ConversationAction.conversationActions.remove(ConversationAction.conversationActions.size() -1);
			}
			return null;
		}
		return null;
	}


	public boolean qualifiesAs(ConversationObject other, boolean allowIndefinite) {
		
		if (!allowIndefinite) {
			if (determiners.size() > 0 && !determiners.contains("the")) { //TODO: improve
				return false;
			}
		}
		if (!simpleSubject.equals(other.simpleSubject)) {
			return false;
		}
		for (ConversationAction action : actions) {
			boolean contains = false;
			for (ConversationAction otherAction : other.actions) {
				if (action.qualifiesAs(otherAction)) {
					contains = true;
					break;
				}
			}
			if (!contains) {
				return false;
			}
		}
		/*for (ConversationAction action : receivedActions) {
			boolean contains = false;
			for (ConversationAction otherAction : other.receivedActions) {
				if (action.qualifiesAs(otherAction)) {
					contains = true;
					break;
				}
			}
			if (!contains) {
				return false;
			}
		}*/
		for (ConversationAction action : indirectReceivedActions) {
			boolean contains = false;
			for (ConversationAction otherAction : other.indirectReceivedActions) {
				if (action.qualifiesAs(otherAction)) {
					contains = true;
					break;
				}
			}
			if (!contains) {
				return false;
			}
		}
		for (Property property : properties) {
			boolean contains = false;
			for (Property otherProperty : other.properties) {
				if (property.qualifiesAs(otherProperty)) {
					contains = true;
					break;
				}
			}
			if (!contains) {
				return false;
			}
		}

		return true;
	}

	private ConversationObject(Parse subject) {
		

		id = conversationObjects.size();
		conversationObjects.add(this);
		ParseAnalyzer subjectAnalyzer = new ParseAnalyzer(subject, false);
		ArrayList<Parse> modifiers = new ArrayList<Parse>();
		Parse simpleSubject = subjectAnalyzer.getSimpleSubject();
		for (Parse modifier : subjectAnalyzer.tempRemovedPhrases) {
			modifiers.add(modifier);
		}
		this.simpleSubject = simpleSubject.getCoveredText().toLowerCase();
		for (Parse modifier : modifiers) {
			analyzeModifier(modifier);
		}


	}

	public void addProperty(Property prop) {
		properties.add(prop);
	}

	public void addCategory(GeneralObject category) {
		categories.add(category);
	}

	public void addAction(ConversationAction action) {
		actions.add(action);
	}

	public void addReceivedAction(ConversationAction action) {
		receivedActions.add(action);
	}

	public void addIndirectReceivedAction(ConversationAction action) {
		indirectReceivedActions.add(action);
	}

	public ConversationObject analyzeNounModifier(Parse modifier) {

		ConversationObject equivalent = createConversationObject(modifier);
		equivalentTo.add(equivalent);
		equivalent.equivalentTo.add(this);
		return equivalent;


	}

	public void analyzeModifier(Parse modifier) {
		if (Arrays.asList(ParseAnalyzer.ADJ_TYPES).contains(modifier.getType())) {
			Property property = new Property(modifier);
			actions.add(new ConversationAction(property, new ArrayList<Parse>(), this, null));
			properties.add(property);
		}
		else if (modifier.getType().equals("PP")) {
			Property property = new Property(modifier);
			actions.add(new ConversationAction(property, new ArrayList<Parse>(), this, null));
			properties.add(property);
		}
		else if (modifier.getType().equals("PRP$")) {
			Property property = new Property(modifier);
			actions.add(new ConversationAction(property, new ArrayList<Parse>(), this, null));
			properties.add(property);
		}
		else if (modifier.getType().equals("DT")) {
			determiners.add(modifier.getCoveredText().toLowerCase());
		}
		else if (Arrays.asList(ParseAnalyzer.NOUN_PHRASES).contains(modifier.getType()) || Arrays.asList(ParseAnalyzer.SS_WORDS).contains(modifier.getType())) {
			analyzeNounModifier(modifier);
		}
		else if (modifier.getType().equals("SBAR")) {

		}
		else {
			System.err.println("DUDE, serious problems with your modifiers...");
		}

	}

	public void print() {
		System.out.println("NEW CONVERSATION OBJECT");
		System.out.println("id: " + id);
		System.out.println("Simple subject: " + simpleSubject);
		if (actions.size() > 0) {
			System.out.println("Actions: ");
			for (ConversationAction action : actions) {
				System.out.print(action.id + ", ");
			}
			System.out.println();
		}
		if (receivedActions.size() > 0) {
			System.out.println("Received Actions: ");
			for (ConversationAction action : receivedActions) {
				System.out.print(action.id + ", ");
			}
			System.out.println();
		}

		if (indirectReceivedActions.size() > 0) {
			System.out.println("Indirect Received Actions: ");
			for (ConversationAction action : indirectReceivedActions) {
				System.out.print(action.id + ", ");
			}
			System.out.println();
		}
		if (determiners.size() > 0) {
			System.out.println("Determiners: ");
			for (String d : determiners) {
				System.out.print(d + ", ");
			}

			System.out.println();
		}
		if (categories.size() > 0) {
			System.out.println("Categories: ");
			for (GeneralObject thing : categories) {
				System.out.print(thing.id + ", ");
			}
			System.out.println();
		}
		if (equivalentTo.size() > 0) {
			System.out.println("Equivalent Objects: ");
			for (ConversationObject thing : equivalentTo) {
				System.out.print(thing.id + ", ");
			}
			System.out.println();
		}
		if (properties.size() > 0) {
			System.out.println("Properties: ");
			for (Property property : properties) {
				property.print();
			}
		}

	}

}
