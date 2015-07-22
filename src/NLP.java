import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import opennlp.tools.parser.Parse;


public class NLP {
	public static SentenceDetector sd = null;
	public static SentenceParser parser = null;
	public static void processSentence(String input) throws IOException {
		if (sd == null) sd = new SentenceDetector();
		if (parser == null) parser = new SentenceParser();
		String[] sentences = sd.detectSentences(input);
		for (int i = 0; i < sentences.length; i++) {
			Parse[] topParses = parser.getParses(sentences[i], 20);

			Parse[] spForm = null;
			for (int m = 0; m < topParses.length; m++) {
				Parse bestParse;

				bestParse = topParses[m].getChildren()[0]; //remove TOP level
				try {
					if (bestParse.getType().equals("S")) {
						processStatement(bestParse);
					}
					else if (bestParse.getType().equals("SBARQ")) {
						processWhQuestion(bestParse);
					}
					else if (bestParse.getType().equals("SQ")) {
						processInverted(bestParse, null);
					}
					break;
				}
				catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}
	}
	public static void processInverted(Parse parse, String question) {
		ParseAnalyzer analyzer = new ParseAnalyzer(parse, true);
		parse = analyzer.parse;
		if (!(parse.getChildCount() == 3)) {
			System.err.println("Problem with question parsing SQ yes-no");
			parse.show();
		}
		ConversationObject subject = ConversationObject.findConversationObject(parse.getChildren()[1], true);
		if (subject == null) System.out.println("I don't remember you telling me about " + parse.getChildren()[1].getCoveredText());
		else {
			ConversationAction findResult = ConversationAction.findConversationAction(parse.getChildren()[2], new ArrayList<Parse>(),
					subject, parse.getChildren()[0], subject.actions);
			if (findResult == null) System.out.println("I don't remember you telling me that " + parse.getChildren()[1].getCoveredText() + 
					" " + parse.getChildren()[0].getCoveredText() + " " + parse.getChildren()[2].getCoveredText());
			else {
				System.out.println("Yes, I think you told me that");
			}
		}

	}
	public static void processWhQuestion(Parse question) {
		ParseAnalyzer analyzer = new ParseAnalyzer(question, true);
		question = analyzer.parse;
		Parse questionWord = question.getChildren()[0];

		Parse invertedPhrase = question.getChildren()[1];
		if (!invertedPhrase.getType().equals("SQ")) {
			System.err.println("Problem with question parsing SQ");
			question.show();
		}
		if (questionWord.getType().equals("WHNP")) {
			if (invertedPhrase.getChildCount() == 1 && invertedPhrase.getChildren()[0].getType().equals("VP")) {
				ConversationAction findResult = ConversationAction.findConversationAction(invertedPhrase.getChildren()[0], new ArrayList<Parse>(), null, null);
				if (findResult == null) System.out.println("I don't remember you telling me that anyone " + invertedPhrase.getCoveredText());
				else {
					String determiner = "";
					if (findResult.subject.determiners.size() > 0) determiner = "The";
					System.out.println(determiner + " "  + findResult.subject.simpleSubject + " " + invertedPhrase.getCoveredText());
				}
			}
			else if (invertedPhrase.getChildCount() == 3 &&
					(invertedPhrase.getChildren()[0].getType().equals("VP") ||
							Arrays.asList(ParseAnalyzer.VERB_WORDS).contains(invertedPhrase.getChildren()[0].getType()) &&
							(invertedPhrase.getChildren()[2].getType().equals("VP") ||
									Arrays.asList(ParseAnalyzer.VERB_WORDS).contains(invertedPhrase.getChildren()[2].getType())))) {
				ConversationObject subject = ConversationObject.findConversationObject(invertedPhrase.getChildren()[1], true);
				if (subject == null) System.out.println("I don't remember you telling me about " + invertedPhrase.getChildren()[1].getCoveredText());
				else {
					ConversationAction findResult = ConversationAction.findConversationAction(invertedPhrase.getChildren()[2], new ArrayList<Parse>(),
							subject, invertedPhrase.getChildren()[0], subject.actions);
					if (findResult == null) System.out.println("I don't remember you telling me that " + invertedPhrase.getChildren()[1].getCoveredText() + 
							" " + invertedPhrase.getChildren()[0].getCoveredText() + " " + invertedPhrase.getChildren()[2].getCoveredText() + " anything or anyone.");
					else {
						ConversationObject obj = findResult.directObject;
						String determiner = "";
						if (findResult.subject.determiners.size() > 0) determiner = "The";
						if (obj == null) System.out.println("I don't remember you telling me that " + invertedPhrase.getChildren()[1].getCoveredText() + 
								" " + invertedPhrase.getChildren()[0].getCoveredText() + " " + invertedPhrase.getChildren()[2].getCoveredText() + " anything or anyone.");
						else {
							System.out.println(invertedPhrase.getChildren()[1].getCoveredText() + " " + invertedPhrase.getChildren()[0].getCoveredText() + 
									" " + invertedPhrase.getChildren()[2].getCoveredText() + " " + obj.simpleSubject);
						}
					}
				}
			}
			else System.err.println("fail");
				
		}



	}
	public static void processStatement(Parse bestParse) throws IOException {

		Parse[] spForm = null;
		ParseAnalyzer analyzer;
		analyzer = new ParseAnalyzer(bestParse, true);
		analyzer.parse.show();
		spForm = analyzer.subjectPredicateForm();
		ArrayList<Parse> actionModifiers = new ArrayList<Parse>();
		Parse subject = spForm[0];
		actionModifiers.addAll(analyzer.tempRemovedPhrases);
		ParseAnalyzer subjectAnalyzer = new ParseAnalyzer(spForm[0], false);
		for (Parse modifier : subjectAnalyzer.tempRemovedPhrases) {
			System.out.println(modifier + " ");
		}
		analyzer.tempRemovedPhrases.clear();



		ConversationAction action = new ConversationAction(spForm[1], actionModifiers, ConversationObject.createConversationObject(subject), null);

		System.out.println("DONE");
		System.out.println();
		System.out.println();
		System.out.println("STATE: ");
		System.out.println("OBJECTS: ");
		for (ConversationObject object : ConversationObject.conversationObjects) {
			object.print();
		}
		System.out.println("ACTIONS: ");
		for (ConversationAction a : ConversationAction.conversationActions) {
			a.print();
		}







	}
}

