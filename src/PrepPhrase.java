import opennlp.tools.parser.Parse;


public class PrepPhrase {
	public String preposition;
	public ConversationObject prepObject;
	public PrepPhrase(Parse pp) {
		if (pp.getType().equals("PP")) {
			preposition = pp.getChildren()[0].getCoveredText();
			prepObject = ConversationObject.createConversationObject(pp.getChildren()[1]);
		}
	}
	public boolean qualifiesAs(PrepPhrase other) {
		// TODO Auto-generated method stub
		return false;
	}
	public void print() {
		System.out.println("preposition: " + preposition + " /// Object: " + prepObject.id);
	}
}
