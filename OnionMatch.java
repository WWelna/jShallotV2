/* Copyright (C) 2021 William Welna (wwelna@occultusterra.com)
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnionMatch {
							/* 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 */
	static int[] variations = {0,0,0,0,0,1,1,1,1,1,1, 2, 2, 2, 3, 3, 3};
	static Map<Character,Character> l337 = new HashMap<>();
	
	static {
		l337.put('a', '4');
		//l337.put('b', '8');
		l337.put('e', '3');
		l337.put('g', '6');
		//l337.put('i', '1');
		//l337.put('o', '0');
		l337.put('r', '2');
		l337.put('s', '5');
		l337.put('t', '7');
		l337.put('z', '2');
	}
	
	public static List<String> match_wordlist(String subject, WordList words) {
		char[] subject_chars = subject.toCharArray();
		List<String> matches = new ArrayList<>();
		int variant = 0;
		for(int x=0; x<12; ++x) {
			if(Character.isAlphabetic(subject.charAt(x))) {
				Collection<String> list = words.getwords(Character.toLowerCase(subject.charAt(x)));
				for(String word:list) {
					if(word.length()<=16 && word.length()>=4 && word.length()<=(16-x)) {
						char[] word_chars = word.toCharArray();
						if(quick_check(Arrays.copyOfRange(subject_chars, x, word_chars.length+x), word_chars, variant)) {
							//System.out.println("MATCHED "+word);
							matches.add(word);
						}
					}
				}
			}
		}
		return matches;
	}
	
	public static boolean match(String subject, String word) {
		if(word.length()>16) return false;
		char[] subject_chars = subject.toCharArray();
		char[] word_chars = word.toCharArray();
		int match_to = subject_chars.length - word_chars.length;
		int variant = 0;//variations[word_chars.length];
		for(int x=0; x<match_to; ++x)
			if(quick_check(Arrays.copyOfRange(subject_chars, x, word_chars.length+x), word_chars, variant))
				return true;
		return false;
	}
	
	private static boolean check_l337(char c, char got) {
		if(Character.isAlphabetic(c) && Character.isDigit(got))
			if(l337.containsKey(c) && l337.get(c).charValue() == got)
				return true;
		return false;
	}
	
	private static boolean quick_check(char[] a, char[] b, int variant) {
		int not=0;
		for(int x=0; x<a.length && x<b.length; ++x)
			if(a[x] != b[x]) {
				if(check_l337(b[x], a[x]))
					continue;
				++not;
				if(not > variant)
					return false;
			}
		return true;
	}
	
}

