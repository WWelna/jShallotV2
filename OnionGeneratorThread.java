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
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class OnionGeneratorThread extends Thread {
	private int num;
	
	OnionGeneratorThread(int num) {
		this.num=num;
	}

	@Override public void run() {
	while(true) {
		try {
			WordList words = new WordList();
			int total_counter = 0, reseed_counter=0;
			OnionRSATom r = new OnionRSATom(); r.makekey();
			Primes32 primes = new Primes32();
			while(true) {
				long mess_ret;
				total_counter++; reseed_counter++;
				String s = r.getOnion();
				List<String> matches = OnionMatch.match_wordlist(s, words);
				if(matches.size()>3) {
					int more4=0;
					for(String match:matches)
						if(match.length()>4)
							more4++;
					if(more4>1) {
						try (PrintWriter out = new PrintWriter("onions/"+r.getuuid()+"-"+s+".txt")) {
							System.out.println("Thread "+num+" / "+total_counter+" -> "+s+" "+matches);
							out.println("Thread "+num+" / "+total_counter+" -> "+s+" "+matches);
							out.println(r.exportPublic());
							out.println(r.exportPrivate());
						}
					}
				}
				while(true) {
					try {
						mess_ret = r.keymorph2(primes.getprime());
						break;
					} catch(Exception e) {
						continue;
					}
				} 
				if(mess_ret==-1 || reseed_counter > 4096) {
					System.out.println("Thread "+num+" Reseeding at "+total_counter);
					r.morerandom();
					r.makekey();
					if(mess_ret == -1) {
						primes.close(); primes = new Primes32();
					}
					r.keymorph2(primes.getprime());
					reseed_counter = 0;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	}
	
	private String[] get_lines(String file) throws IOException {
		String content = new String(Files.readAllBytes(Paths.get(file))).replaceAll("[^A-Za-z\\r?\\n]+"," ");
		return content.toLowerCase().split("\\r?\\n");
	}

}
