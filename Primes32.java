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

import java.io.FileInputStream;
import java.io.IOException;

public class Primes32 implements AutoCloseable {
	FileInputStream in = null;
	byte[] prime_bytes = new byte[4];
	
	public Primes32() throws Exception {
		in = new FileInputStream("primes.32b");
	}
	
	public long getprime() throws Exception {
		long r=0;
		if(in.read(prime_bytes)!=-1) {
			r |= (prime_bytes[0]&0xffL);
			r |= (prime_bytes[1]&0xffL)<<8;
			r |= (prime_bytes[2]&0xffL)<<16;
			r |= (prime_bytes[3]&0xffL)<<24;
		return r;
		} else {
			return -1;
		}
	}
	
	public void close() throws Exception {
		in.close();
	}

}
