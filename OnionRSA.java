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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.x509.RSAPublicKeyStructure;

public class OnionRSA {
	
	RSAPrivateKey privatekey=null;
	RSAPublicKey  publickey=null;
	
	public OnionRSA() {
		
	}
	
	public OnionRSA(String pubkey, String privkey) throws Exception {
		if(pubkey.length()>0) importPublic(pubkey);
		if(privkey.length()>0) importPrivate(privkey);
	}
	
	public void makekey() throws Exception {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
	    keyGen.initialize(1024); KeyPair key = keyGen.generateKeyPair();
		privatekey = (RSAPrivateKey) key.getPrivate();
		publickey = (RSAPublicKey) key.getPublic();
	}
	
	public void importPublic(String key) throws Exception {
		X509EncodedKeySpec pubk = new X509EncodedKeySpec(Base64.decodeBase64(key));
		publickey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(pubk);
	}
	
	public void importPrivate(String key) throws Exception {
		PKCS8EncodedKeySpec privk = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
		privatekey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(privk);
	}
	
	public String exportPublic() {
		if(publickey == null) return "";
		return new String(Base64.encodeBase64Chunked(publickey.getEncoded()));
	}
	
	public String exportPrivate() {
		if(privatekey == null) return "";
		return new String(Base64.encodeBase64Chunked(privatekey.getEncoded()));
	}
	
	public byte[] encrypt(byte[] data) throws Exception {
		if(publickey == null) return null;
		final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publickey);
		return cipher.doFinal(data);
	}
	
	public byte[] decrypt(byte[] data) throws Exception {
		if(privatekey == null) return null;
		final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privatekey);
		return cipher.doFinal(data);
	}
	
	public String getOnion() throws Exception {
		@SuppressWarnings("deprecation")
		RSAPublicKeyStructure myKey = new RSAPublicKeyStructure(publickey.getModulus(), publickey.getPublicExponent());
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		ASN1OutputStream as = new ASN1OutputStream(bs);
		as.writeObject(myKey.toASN1Object());
		byte[] b = bs.toByteArray();
		b = DigestUtils.getSha1Digest().digest(b);
		return new Base32().encodeAsString(b).toLowerCase().substring(0, 16);
	}

}
