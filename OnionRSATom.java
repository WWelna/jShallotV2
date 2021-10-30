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

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.RSAPublicKeyStructure;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.LongByReference;

import java.security.SecureRandom;

public class OnionRSATom {
	org.bouncycastle.asn1.pkcs.RSAPrivateKey privatekey=null;
	org.bouncycastle.asn1.pkcs.RSAPublicKey  publickey=null;
	libtomrsa rsa = (libtomrsa)Native.loadLibrary("rsatom", libtomrsa.class);
	SecureRandom random = new SecureRandom();
	BigInteger n;

	Pointer prng;
	
	String uuid;
	byte[] privatekey_bytes;
	byte[] publickey_bytes;
	
	public OnionRSATom() {
		prng = rsa.get_prng();
		morerandom();
	}

	public void morerandom() {
		rsa.prng_seed(prng);
	}
	
	public void makekey() throws Exception {
		Pointer handle = rsa.mkkey(prng, 1024);
		LongByReference n = new LongByReference();
		if(handle != Pointer.NULL) {
			Pointer pp = rsa.exportPrivate(handle, n);
			privatekey_bytes = Arrays.copyOf(pp.getByteArray(0, (int) n.getValue()),(int) n.getValue());
			rsa.freeExport(pp);
			
			pp=rsa.exportPublic(handle, n);
			publickey_bytes = Arrays.copyOf(pp.getByteArray(0, (int) n.getValue()),(int) n.getValue());
			rsa.freeExport(pp);
			
			rsa.freekey(handle);
			
			publickey = RSAPublicKey.getInstance(publickey_bytes);
			privatekey = RSAPrivateKey.getInstance(privatekey_bytes);
			this.n = privatekey.getPrime1().subtract(BigInteger.ONE).multiply(privatekey.getPrime2().subtract(BigInteger.ONE));
			uuid = DigestUtils.sha1Hex(this.n.toByteArray());
		}
	}

	public String getuuid() {
		return uuid;
	}

    	private BigInteger getCoprime() {
		int length = 64; //n.bitLength()-1;
		BigInteger e = BigInteger.probablePrime(length,random);
		while (!(n.gcd(e)).equals(BigInteger.ONE)) {
			e = BigInteger.probablePrime(length,random);
		}
		return e;
	}
    
	private BigInteger getPrivate(BigInteger e) {
		return e.modInverse(n);
	}
    
	private BigInteger getPrimeExponent(BigInteger d, BigInteger prime) {
		return d.mod(prime.subtract(BigInteger.ONE));
	}
	
	public void keymorph() throws Exception {
		publickey = new RSAPublicKey(publickey.getModulus(), getCoprime());
	}

	public long keymorph2(long prime) throws Exception {
		if(prime==-1) return -1;
		publickey = new RSAPublicKey(publickey.getModulus(), BigInteger.valueOf(prime));
		return prime;
	}

	public String exportPublic() throws Exception {
		if(publickey == null) return "";
		return new String(Base64.encodeBase64Chunked(publickey.getEncoded()));
	}
	
	public org.bouncycastle.asn1.pkcs.RSAPublicKey getPublicKey() {
		return publickey;
	}
	
	public org.bouncycastle.asn1.pkcs.RSAPrivateKey getPrivateKey() {
		return privatekey;
	}
	
	public String exportPrivate() throws Exception {
		if(privatekey == null) return "";
		BigInteger d = getPrivate(publickey.getPublicExponent());
		privatekey = new RSAPrivateKey(privatekey.getModulus(), publickey.getPublicExponent(), d, privatekey.getPrime1(), privatekey.getPrime2(), getPrimeExponent(d, privatekey.getPrime1()), getPrimeExponent(d, privatekey.getPrime2()), privatekey.getCoefficient());
		return new String(Base64.encodeBase64Chunked(privatekey.getEncoded()));
	}
	
	public String getOnion() throws Exception {
		byte[] b = DigestUtils.getSha1Digest().digest(publickey.getEncoded());
		return new Base32().encodeAsString(b).toLowerCase().substring(0, 16);
	}

	public void close() {
		if(prng != Pointer.NULL)
			rsa.free_prng(prng);
	}

}
