# Rijndael_AES_CIPHER
this repository is AES block cipher using Rijndael algorithm 

1. create instance with pad mode
```java
AESCipher cipher = new AESCipher(CBC_PKCS5PADDING);
```

2. set key and iv (if seleted cbc mode)
```java
byte[] plain = "abcdefghijklmnopqrstuvwxyz".getBytes();
byte[] key = "aaaaaaaaaaaaaaaabbbbbbbbbbbbbbbb".getBytes();
byte[] iv = "aaaaaaaaaaaaaaaa".getBytes();

cipher.setKey(key);
cipher.setIV(iv);
```

3. encrypt (plain is byte[])
```java
byte[] enc = cipher.encrypt(plain);
```

4. decrypt
```java
byte[] dec = cipher.decrypt(enc);
```
