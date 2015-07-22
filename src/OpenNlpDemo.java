import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


import opennlp.tools.parser.Parse;
import opennlp.tools.sentdetect.*;
import opennlp.tools.util.*;

public class OpenNlpDemo {
	//TODO:  GeneralObjcts and countIndefinite in qualifiesAs - essentially improve "the" vs "a" system - also othere determiners, Linking verb to properties (or vice versa) Ensure lower case strings, Nonessential additional modifiers treated as essential, ambiguous modifiers, Predicate nominative/adjective/adverb, verb implications, essential dependent clauses, possessives, passive voice (including passive voice relative clause modifiers stack overflow problem), 
	public static void main (String[] args) throws IOException {
		Stemmer.initialize();
		while (true) {
			System.out.println();
			System.out.println("Type in a sentence...");
			System.out.println();
			Scanner scanner = new Scanner(System.in);
			String input = scanner.nextLine();
			NLP.processSentence(input);
		}

	}

}
