/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import static java.lang.Math.pow;

/**
 *
 * @author Vo Van Tien
 */
public class RC2_Cipher {

    public RC2_Cipher() {
    }

    private int Str2Dec(String str) {
        String result = "";

        char ch[] = str.toCharArray();
        int length = ch.length;

        for (int i = 0; i < length; i++) {
            String stmp = Integer.toHexString(ch[i]);
            while (stmp.length() < 2) {
                stmp = "0" + stmp;
            }

            result = result + stmp;
        }

        return Integer.parseInt(result, 16);
    }

    public int[] KeyExpansion(String key, int keySize, int keyBit) {

        int[] L = new int[128];
        int[] K = new int[64];

        int[] pitable = {
            217, 120, 249, 196, 25, 221, 181, 237, 40, 233, 253, 121, 74, 160, 216, 157,
            198, 126, 55, 131, 43, 118, 83, 142, 98, 76, 100, 136, 68, 139, 251, 162,
            23, 154, 89, 245, 135, 179, 79, 19, 97, 69, 109, 141, 9, 129, 125, 50,
            189, 143, 64, 235, 134, 183, 123, 11, 240, 149, 33, 34, 92, 107, 78, 130,
            84, 214, 101, 147, 206, 96, 178, 28, 115, 86, 192, 20, 167, 140, 241, 220,
            18, 117, 202, 31, 59, 190, 228, 209, 66, 61, 212, 48, 163, 60, 182, 38,
            111, 191, 14, 218, 70, 105, 7, 87, 39, 242, 29, 155, 188, 148, 67, 3,
            248, 17, 199, 246, 144, 239, 62, 231, 6, 195, 213, 47, 200, 102, 30, 215,
            8, 232, 234, 222, 128, 82, 238, 247, 132, 170, 114, 172, 53, 77, 106, 42,
            150, 26, 210, 113, 90, 21, 73, 116, 75, 159, 208, 94, 4, 24, 164, 236,
            194, 224, 65, 110, 15, 81, 203, 204, 36, 145, 175, 80, 161, 244, 112, 57,
            153, 124, 58, 133, 35, 184, 180, 122, 252, 2, 54, 91, 37, 85, 151, 49,
            45, 93, 250, 152, 227, 138, 146, 174, 5, 223, 41, 16, 103, 108, 186, 201,
            211, 0, 230, 207, 225, 158, 168, 44, 99, 22, 1, 63, 88, 226, 137, 169,
            13, 56, 52, 27, 171, 51, 255, 176, 187, 72, 12, 95, 185, 177, 205, 46,
            197, 243, 219, 71, 229, 165, 156, 119, 10, 166, 32, 104, 254, 127, 193, 173
        };

        assert (keySize > 0 && keySize <= 128);
        assert (keyBit >= 0 && keyBit <= 1024);
        if (keyBit == 0) {
            keyBit = 1024;
        }

        for (int i = 0; i < keySize; ++i) {
            L[i] = (int) key.charAt(i);
        }

        for (int i = keySize; i < 128; ++i) {
            L[i] = pitable[(L[i - 1] + L[i - keySize]) % 256];
        }

        int keyByte = (keyBit + 7) / 8;
        int keyMask = 255 % (int) pow((float) 2, (8 + keyBit - 8 * keyByte));

        L[128 - keyByte] = pitable[L[128 - keyByte] & keyMask];

        for (int i = 127 - keyByte; i >= 0; --i) {
            L[i] = pitable[L[i + 1] ^ L[i + keyByte]];
        }

        for (int i = 0; i < 64; ++i) {
            K[i] = L[2 * i] + 256 * L[2 * i + 1];
        }

        return K;
    }

    public String rc2Encrypt(int[] K, String plainText) {
        String cipher = "";

        char[] R = new char[4];
        //int[] R = new int[4];

        for (int i = 0; i < 4; i++) {
            String temp = "" + plainText.charAt(i * 2 + 1) + plainText.charAt(i * 2);
            R[i] = (char) Str2Dec(temp);
            //R[i] = (int)Str2Dec(temp);
        }

        int j;
        int s[] = {1, 2, 3, 5};

        for (int i = 0; i < 16; i++) {
            for (j = 0; j < 4; j++) {
                R[j] = (char) (R[j] + K[4 * i + j] + (R[(j + 4 - 1) % 4] & R[(j + 4 - 2) % 4]) + ((~R[(j + 4 - 1) % 4]) & R[(j + 4 - 3) % 4]));
                R[j] = (char) ((R[j] << s[j]) | (R[j] >> (16 - s[j])));
                //R[j] = (int)( R[j] + K[4 * i + j] + (R[(j + 4 - 1) % 4] & R[(j + 4 - 2) % 4]) + ((~R[(j + 4 - 1) % 4]) & R[(j + 4 - 3) % 4]) );
                //R[j] = (int)((R[j] << s[j]) | (R[j] >> (16 - s[j])));
            }
            if (i == 4 || i == 10) {
                for (j = 0; j < 4; j++) {
                    R[j] = (char) (R[j] + K[R[(j + 4 - 1) % 4] & 63]);
                    //R[j] = (int)(R[j] + K[R[(j + 4 - 1) % 4] & 63]);
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            R[i] = (char) ((R[i] << 8) | (R[i] >> 8));
            //R[i] = (int)((R[i] << 8) | (R[i] >> 8));

            String stmp;
            char chtmp[];

            stmp = Integer.toHexString((int) R[i]);
            while (stmp.length() < 4) {
                stmp = "0" + stmp;
            }
            chtmp = stmp.toCharArray();
            cipher += (char) Integer.parseInt("" + chtmp[0] + chtmp[1], 16);
            cipher += (char) Integer.parseInt("" + chtmp[2] + chtmp[3], 16);
        }
        return cipher;
    }

    public String rc2Decrypt(int[] K, String cipherText) {
        String result = "";

        char[] R = new char[4];
        //int[] R = new int[4];

        for (int i = 0; i < 4; i++) {
            String temp = "" + cipherText.charAt(i * 2 + 1) + cipherText.charAt(i * 2);
            R[i] = (char) Str2Dec(temp);
            //R[i] = (int) Str2Dec(temp);
        }

        int j;
        int[] s = {1, 2, 3, 5};

        for (int i = 15; i >= 0; i--) {
            for (j = 3; j >= 0; j--) {
                R[j] = (char) ((R[j] >> s[j]) | (R[j] << (16 - s[j])));
                R[j] = (char) (R[j] - K[4 * i + j] - (R[(j + 4 - 1) % 4] & R[(j + 4 - 2) % 4]) - ((~R[(j + 4 - 1) % 4]) & R[(j + 4 - 3) % 4]));
                //R[j] = (int)((R[j] >> s[j]) | (R[j] << (16 - s[j])));
                //R[j] = (int)(R[j] - K[4 * i + j] - (R[(j + 4 - 1) % 4] & R[(j + 4 - 2) % 4]) - ((~R[(j + 4 - 1) % 4]) & R[(j + 4 - 3) % 4]));
            }
            if (i == 5 || i == 11) {
                for (j = 3; j >= 0; j--) {
                    R[j] = (char) (R[j] - K[R[(j + 4 - 1) % 4] & 63]);
                    //R[j] = (int)(R[j] - K[R[(j + 4 - 1) % 4] & 63]);
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            R[i] = (char) ((R[i] << 8) | (R[i] >> 8));
            //R[i] = (int)((R[i] << 8) | (R[i] >> 8));

            String stmp;
            char chtmp[];

            stmp = Integer.toHexString((int) R[i]);
            while (stmp.length() < 4) {
                stmp = "0" + stmp;
            }
            chtmp = stmp.toCharArray();
            result += (char) Integer.parseInt("" + chtmp[0] + chtmp[1], 16);
            result += (char) Integer.parseInt("" + chtmp[2] + chtmp[3], 16);
        }

        return result;
    }

    public String Encrypt(int[] K, String plainText) {
        String cipherText = "";

        int length = plainText.length();

        String tmp;
        for (int i = 0; i < length; i = i + 8) {

            if (i + 8 < plainText.length()) {
                tmp = plainText.substring(i, i + 8);
            } else {
                tmp = plainText.substring(i, length);
                while (tmp.length() < 8) {
                    tmp += " ";
                }
            }

            cipherText += rc2Encrypt(K, tmp);
        }

        return cipherText;
    }

    public String Decrypt(int[] K, String cipherText) {
        String resultText = "";

        int length = cipherText.length();

        String tmp;
        for (int i = 0; i < length; i = i + 8) {

            if (i + 8 < cipherText.length()) {
                tmp = cipherText.substring(i, i + 8);
            } else {
                tmp = cipherText.substring(i, length);
                while (tmp.length() < 8) {
                    tmp += " ";
                }
            }

            resultText += rc2Decrypt(K, tmp);
        }

        return resultText;
    }

    public static void main(String[] args) {
        RC2_Cipher rc2 = new RC2_Cipher();
        String plainText, cipherText, key;
        int keyBit = 63;
        key="1619967735614";
        int[] K = rc2.KeyExpansion(key, key.length(), keyBit);

        System.out.println(rc2.Encrypt(K, "1619967735614"));
                System.out.println(rc2.Decrypt(K, rc2.Encrypt(K, "1619967735614")));

    }
}
