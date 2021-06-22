package util;

public class StringIO {

	public static String[] stringToArrayBySpaces(String k) {
		if(k == null)
			return null;
		
		int size = 0;
		for(char c : k.toCharArray()) {
			if(Character.isWhitespace(c))
				size++;
		}
		String[] convertion = new String[size+1];
		int pointer = 0;
		int current = 0;
		for(int c = 0; c < k.length(); c++) {
			if(k.charAt(c) == ' ') {
				convertion[current] = k.substring(pointer, c);
				pointer = c+1;
				current++;
			} else if(c == k.length()-1) {
				convertion[current] = k.substring(pointer, c+1);
			}
		}
		return convertion;
	}
}
