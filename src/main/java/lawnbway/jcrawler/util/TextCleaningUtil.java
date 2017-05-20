package lawnbway.jcrawler.util;

public class TextCleaningUtil {
	
	/**
	 * Removes all non-letter characters and folds input to lowercase.
	 * 
	 * @param input text with non-letter characters
	 * @return String consisting only of lowercase letters and whitespaces
	 */
	public static String removeNonLetterChars(String input) {
		String output = input.replaceAll("[^a-zA-Z ]", "").toLowerCase();
		return output;
	}
	
	public static String removeArticles(String input) {
		String output = input.replaceAll(" a | an | the ", "");
		return output;
	}
}
