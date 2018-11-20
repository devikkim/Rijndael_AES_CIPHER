/**
 * AES(Rijndael) Block Cipher class
 *
 * <p> Rijndael 알고리즘을 Wrapping 한 클래스
 *
 * <p> 클래스 인스턴스 시 블록 모드를 세팅 이후 키를 세팅 (CBC일 경우, IV도 세팅) encrypt, decrypt 함수를 통하여 암,복호화 진행
 *
 * <p> 작성자 : 김인권
 * 
 * <p> 작성일 : 2018.02.22
 */
package rijndael;

import rijndael.AbstractCustomCipher;

import javax.management.openmbean.InvalidKeyException;

public class AESCipher extends AbstractCustomCipher {
  private RijndaelAlgorithm cipher;
  private byte[] iv;

  public AESCipher(String blockModeAndPadMode) {
    super(blockModeAndPadMode);

    cipher = new RijndaelAlgorithm();
  }

  @Override
  public void setKey(byte[] key) {
    int keyBit = 0;
    if (key.length == 16) {
      keyBit = 128;
    } else if (key.length == 24) {
      keyBit = 192;
    } else if (key.length == 32) {
      keyBit = 256;
    } else {
      throw new InvalidKeyException("key size = " + key.length);
    }
    cipher.makeKey(key, keyBit);
  }

  @Override
  public void setIV(byte[] iv) {
    if (iv.length != 16) {
      throw new InvalidKeyException("initial vector size = " + iv.length);
    }
    this.iv = iv;
  }

  @Override
  protected byte[] cbcEncrypt(byte[] plain) {
    try {
      byte[] XORBlock = new byte[plain.length];

      byte[] encryptedBytes = new byte[plain.length];
      int outputIdx = 0;

      for (int i = 0; i < plain.length; i += BLOCK_SIZE) {
        if (i == 0) { // 첫 평문 블록과 IV의 XOR
          for (int idx = 0; idx < 16; idx++) {
            XORBlock[idx] = (byte) (plain[idx] ^ this.iv[idx]);
          }
          cipher.encrypt(XORBlock, i, encryptedBytes, i);
        } else { // 이후, 앞의 암호화된 블록과 평문 블럭과의 XOR
          for (int currentIdx = i; currentIdx < i + BLOCK_SIZE; currentIdx++) {
            XORBlock[currentIdx] = (byte) (plain[currentIdx] ^ encryptedBytes[outputIdx]);
            outputIdx++;
          }
          cipher.encrypt(XORBlock, i, encryptedBytes, i);
        }
      }
      return encryptedBytes;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected byte[] cbcDecrypt(byte[] encryptedBytes) {
    try {
      byte[] output = new byte[encryptedBytes.length];
      int outputIdx = 0;

      byte[] decryptedBytes = new byte[encryptedBytes.length];

      for (int i = 0; i < encryptedBytes.length; i += BLOCK_SIZE) {
        cipher.decrypt(encryptedBytes, i, output, i);

        if (i == 0) { // 첫 암호화 블록을 복호화 한 후, 나온 결과를 IV와 XOR
          for (int idx = 0; idx < 16; idx++) {
            decryptedBytes[idx] = (byte) (this.iv[idx] ^ output[idx]);
          }
        } else { // 이후 암호화 블록은 복호화 한 후, 이전 암호화 블록과 XOR
          for (int currentIdx = i; currentIdx < i + 16; currentIdx++) {
            decryptedBytes[currentIdx] = (byte) (output[currentIdx] ^ encryptedBytes[outputIdx]);
            outputIdx++;
          }
        }
      }
      return decryptedBytes;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected byte[] ecbEncrypt(byte[] plain) {
    byte[] encryptedBytes = new byte[plain.length];

    for (int i = 0; i < plain.length; i += BLOCK_SIZE)
      cipher.encrypt(plain, i, encryptedBytes, i);

    return encryptedBytes;
  }

  @Override
  protected byte[] ecbDecrypt(byte[] encryptedBytes) {
    try {
      byte[] decryptedBytes = new byte[encryptedBytes.length];

      for (int i = 0; i < encryptedBytes.length; i += BLOCK_SIZE) {
        cipher.decrypt(encryptedBytes, i, decryptedBytes, i);
      }
      return decryptedBytes;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) {
    byte[] plain = "abcdefghijklmnopqrstuvwxyz".getBytes();
    byte[] key = "aaaaaaaaaaaaaaaabbbbbbbbbbbbbbbb".getBytes();
    byte[] iv = "aaaaaaaaaaaaaaaa".getBytes();

    AESCipher cipher = new AESCipher(CBC_PKCS5PADDING);

    cipher.setKey(key);
    cipher.setIV(iv);

    byte[] enc = cipher.encrypt(plain);
    byte[] dec = cipher.decrypt(enc);
    System.out.println("aes cbc decrypted data : " + new String(dec));
  }
}
