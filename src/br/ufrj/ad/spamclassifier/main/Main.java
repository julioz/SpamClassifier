package br.ufrj.ad.spamclassifier.main;

import java.io.IOException;
import java.util.ArrayList;

import br.ufrj.ad.spamclassifier.database.Parser;
import br.ufrj.ad.spamclassifier.model.Email;

public class Main {

	public static void main(String[] args) throws IOException {
		ArrayList<Email> emails = Parser.parseDatabase();
		
		System.out.println(emails);
	}

}
