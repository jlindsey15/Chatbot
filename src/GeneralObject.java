import java.util.ArrayList;

import opennlp.tools.parser.Parse;


public class GeneralObject {
	public int id;
	public static ArrayList<GeneralObject> generalObjects = new ArrayList<GeneralObject>();
	public GeneralObject(Parse nounPhrase) {
		id = generalObjects.size();
	}

}
