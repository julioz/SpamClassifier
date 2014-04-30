package br.ufrj.ad.spamclassifier.database;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrj.ad.spamclassifier.model.Email;

public class Parser {

	private static final String PATH_SPAMBASE_NAMES = "database/spambase.names";
	private static final String PATH_SPAMBASE_DATA = "database/spambase.data";
	private static final String WORD_FREQ_KEY = "word_freq_";
	private static final String CHAR_FREQ_KEY = "char_freq_";
	
	private static Collection<String> mWords;
	private static Collection<String> mChars;

	public static ArrayList<Email> parseDatabase() throws IOException {
		List<String> names = Files.readAllLines(
				Paths.get(PATH_SPAMBASE_NAMES), Charset.defaultCharset());
		List<String> data = Files.readAllLines(
				Paths.get(PATH_SPAMBASE_DATA), Charset.defaultCharset());
		
		mWords = new ArrayList<String>();
		mChars = new ArrayList<String>();

		for (String line : names) {
			if (line.startsWith(WORD_FREQ_KEY)) {
				line = line.substring(0, line.indexOf(':'));
				line = line.replace(WORD_FREQ_KEY, "");
				mWords.add(line);
			} else if (line.startsWith(CHAR_FREQ_KEY)) {
				line = line.substring(0, line.indexOf(':'));
				line = line.replace(CHAR_FREQ_KEY, "");
				mChars.add(line);
			}
		}

		ArrayList<Email> mails = new ArrayList<Email>();
		for (int i = 0; i < data.size(); i++) {
			String[] src = data.get(i).split(",");
			Email mail = new Email(i);
			
			int ptr = 0;
			for (String word : mWords) {
				mail.setWordFrequency(word, Float.valueOf(src[ptr++]));
			}

			for (String character : mChars) {
				mail.setCharFrequency(character.charAt(0), Float.valueOf(src[ptr++]));
			}

			mail.setAvgUninterruptedCapitals(Float.valueOf(src[ptr++]));
			mail.setLongestUninterruptedCapitalsLength(Integer.parseInt(src[ptr++]));
			mail.setNumberOfCapitals(Integer.parseInt(src[ptr++]));
			mail.setSpam(Integer.valueOf(src[ptr]) == 0 ? false : true);
			mails.add(mail);
		}
		
		return mails;
	}
	
	public static Collection<String> getWords() {
		if (mWords == null) {
			throw new RuntimeException("You need to call Parser.parseDatabase() first!");
		}
		return mWords;
	}
	
	public static Collection<String> getChars() {
		if (mChars == null) {
			throw new RuntimeException("You need to call Parser.parseDatabase() first!");
		}
		return mChars;
	}
}
