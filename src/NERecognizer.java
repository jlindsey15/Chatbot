import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;



public class NERecognizer {
	NameFinderME dateNameFinder, locationNameFinder, moneyNameFinder, organizationNameFinder,
	percentageNameFinder, personNameFinder, timeNameFinder;

	public NERecognizer() throws FileNotFoundException {
		TokenNameFinderModel dateModel = null, locationModel = null, moneyModel = null, 
				organizationModel = null, percentageModel = null, personModel = null, 
				timeModel = null;

		InputStream dateModelIn = new FileInputStream("en-ner-date.bin");
		InputStream locationModelIn = new FileInputStream("en-ner-location.bin");
		InputStream moneyModelIn = new FileInputStream("en-ner-money.bin");
		InputStream organizationModelIn = new FileInputStream("en-ner-organization.bin");
		InputStream percentageModelIn = new FileInputStream("en-ner-percentage.bin");
		InputStream personModelIn = new FileInputStream("en-ner-person.bin");
		InputStream timeModelIn = new FileInputStream("en-ner-time.bin");
		InputStream[] modelInArray = {dateModelIn, locationModelIn, moneyModelIn, 
				organizationModelIn, percentageModelIn, personModelIn, timeModelIn};
		try {
			dateModel = new TokenNameFinderModel(dateModelIn);
			locationModel = new TokenNameFinderModel(locationModelIn);
			moneyModel = new TokenNameFinderModel(moneyModelIn);
			organizationModel = new TokenNameFinderModel(organizationModelIn);
			percentageModel = new TokenNameFinderModel(percentageModelIn);
			personModel = new TokenNameFinderModel(personModelIn);
			timeModel = new TokenNameFinderModel(timeModelIn);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			for (InputStream modelIn : modelInArray) {
				if (modelIn != null) {
					try {
						modelIn.close();
					}
					catch (IOException e) {
					}
				}
			}
		}
		dateNameFinder = new NameFinderME(dateModel);
		locationNameFinder = new NameFinderME(locationModel);
		moneyNameFinder = new NameFinderME(moneyModel);
		organizationNameFinder = new NameFinderME(organizationModel);
		percentageNameFinder = new NameFinderME(percentageModel);
		personNameFinder = new NameFinderME(personModel);
		timeNameFinder = new NameFinderME(timeModel);
	}
	
	public ArrayList<Span> recognizeAllNames(String[] text) {
		ArrayList<Span> nameSpans = new ArrayList<Span>();
		nameSpans.addAll(Arrays.asList(dateNameFinder.find(text)));
		nameSpans.addAll(Arrays.asList(locationNameFinder.find(text)));
		nameSpans.addAll(Arrays.asList(moneyNameFinder.find(text)));
		nameSpans.addAll(Arrays.asList(organizationNameFinder.find(text)));
		nameSpans.addAll(Arrays.asList(percentageNameFinder.find(text)));
		nameSpans.addAll(Arrays.asList(personNameFinder.find(text)));
		nameSpans.addAll(Arrays.asList(timeNameFinder.find(text)));
		dateNameFinder.clearAdaptiveData();
		locationNameFinder.clearAdaptiveData();
		moneyNameFinder.clearAdaptiveData();
		organizationNameFinder.clearAdaptiveData();
		percentageNameFinder.clearAdaptiveData();
		personNameFinder.clearAdaptiveData();
		timeNameFinder.clearAdaptiveData();
		return nameSpans;
	}

}
