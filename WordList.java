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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import org.json.JSONArray;

public class WordList {
	Map<Character,Collection<String>> wordlist = new HashMap<>();
	
	public WordList() throws IOException {
		for(Character c = new Character('a'); c>='a' && c<='z'; c++)
			wordlist.put(c, loadfirstchar(c));
	}
	
	public Collection<String> getwords(Character c) {
		return wordlist.get(c);
	}
	
	Collection<String> loadfirstchar(Character c) throws IOException {
		StringBuilder filename = new StringBuilder();
		filename.append("wordlist_json/"); filename.append(c); filename.append("_json.txt");
		JSONArray j = new JSONArray(new String(Files.readAllBytes(Paths.get(filename.toString())), StandardCharsets.UTF_8));
		Collection<String> ret = new TreeSet<>();
		for(Iterator<Object> i = j.iterator(); i.hasNext(); ) {
			String entry = (String)i.next();
			ret.add(entry.toLowerCase());
		}
		return ret;
	}
}
