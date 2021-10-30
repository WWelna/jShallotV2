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

#include <tomcrypt.h>
#include <stdio.h>
#include <stdlib.h>

int urandom(char *buff, int len) {
	FILE *f = fopen("/dev/urandom", "rb");
	int ret;
	if(f==NULL) return -1;
	ret=fread(buff, 1, len, f);
	fclose(f);
	return ret;
}

extern prng_state *get_prng() {
	prng_state *ret = calloc(1, sizeof(prng_state));
	register_prng(&sober128_desc);
	sober128_start(ret);
	return ret;	
}

extern void prng_seed(prng_state *prng) {
	char seed[64];
	urandom(seed, 64);
	sober128_add_entropy(seed, 64, prng);	
}

extern void free_prng(prng_state *prng) {
	free(prng);
}

extern rsa_key *mkkey(prng_state *prng, int bits) {
	int prng_idx,err;
	rsa_key *key = calloc(1, sizeof(rsa_key));
	ltc_mp = ltm_desc;
	if((err=rsa_make_key(prng, find_prng("sober128"), bits/8, 65537, key))!=CRYPT_OK) {
		printf("mkkey: %s\n",error_to_string(err));
		return NULL;
	}
	return key;
}

extern void freekey(rsa_key *key) {
	rsa_free(key);
	free(key);
}

extern char *exportPublic(rsa_key *key, long *n) {
	char *tmp=calloc(1, 4096); *n=4096;
	int err;	
	if((err=rsa_export(tmp, n, PK_PUBLIC, key))!=CRYPT_OK) {
		printf("exportPublic: %s\n",error_to_string(err));
		free(tmp);
		return NULL;
	}
	return tmp;
}

extern char *exportPrivate(rsa_key *key, long *n) {
        char *tmp=calloc(1, 4096); *n=4096;
        int err;
        if((err=rsa_export(tmp, n, PK_PRIVATE, key))!=CRYPT_OK) {
                printf("exportPrivate: %s\n",error_to_string(err));
                free(tmp);
                return NULL;
        }
        return tmp;
}

extern void freeExport(void *p) {
	free(p);
}
