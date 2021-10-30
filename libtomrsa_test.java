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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import org.apache.commons.codec.binary.Base64;
import com.sun.jna.ptr.LongByReference;

public class libtomrsa_test {
	public static void main(String args[]) throws Exception {
		libtomrsa rsa = (libtomrsa)Native.loadLibrary("libtomrsa.so", libtomrsa.class);
		Pointer handle = rsa.mkkey(1024);
		LongByReference n = new LongByReference();
		if(handle != Pointer.NULL) {
			Pointer pp = rsa.exportPrivate(handle, n);
			byte[] privatekey = pp.getByteArray(0, (int) n.getValue()&0xFFFFFFFF);
			rsa.freeExport(pp);
			
			pp=rsa.exportPublic(handle, n);
			byte[] publickey = pp.getByteArray(0, (int) n.getValue());
			rsa.freeExport(pp);
			
			System.out.println(new String(Base64.encodeBase64(publickey)));
			System.out.println(new String(Base64.encodeBase64(privatekey)));
			
			rsa.freekey(handle);
		}	
	}
}
