package md.jack;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class DES
{
    // Initial Permutation table
    private static final byte[] IP = {
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7
    };

    // Permuted Choice 1 table
    private static final byte[] PC1 = {
            57, 49, 41, 33, 25, 17, 9,
            1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27,
            19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
            7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29,
            21, 13, 5, 28, 20, 12, 4
    };

    // Permuted Choice 2 table
    private static final byte[] PC2 = {
            14, 17, 11, 24, 1, 5,
            3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8,
            16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32
    };

    // Array to store the number of rotations that are to be done on each round
    private static final byte[] rotations = {
            1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1
    };

    // Expansion (aka P-box) table
    private static final byte[] E = {
            32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1
    };

    // S-boxes (i.e. Substitution boxes)
    private static final byte[][] S = {{
            14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
            0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
            4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
            15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13
    }, {
            15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
            3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
            0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
            13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9
    }, {
            10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
            13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
            13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
            1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12
    }, {
            7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
            13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
            10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
            3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14
    }, {
            2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
            14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
            4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
            11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3
    }, {
            12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
            10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
            9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
            4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13
    }, {
            4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
            13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
            1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
            6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12
    }, {
            13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
            1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
            7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
            2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11
    }};

    // Permutation table
    private static final byte[] P = {
            16, 7, 20, 21,
            29, 12, 28, 17,
            1, 15, 23, 26,
            5, 18, 31, 10,
            2, 8, 24, 14,
            32, 27, 3, 9,
            19, 13, 30, 6,
            22, 11, 4, 25
    };

    // Final permutation (aka Inverse permutation) table
    private static final byte[] FP = {
            40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25
    };

    // 28 bits each, used as storage in the KS (Key Structure) rounds to 
    // generate round keys (aka subkeys)
    private static int[] C = new int[28];
    private static int[] D = new int[28];

    // Decryption requires the 16 subkeys to be used in the exact same process
    // as encryption, with the only difference being that the keys are used
    // in reverse order, i.e. last key is used first and so on. Hence, during
    // encryption when the keys are first generated, they are stored in this
    // array. In case we wish to separate the encryption and decryption
    // programs, then we need to generate the subkeys first in order, store
    // them and then use them in reverse order.
    private static int[][] subkey = new int[16][48];

    public static void main(String args[])
    {
        System.out.println("Enter the input = 8 characters:");
        final int[] inputBits = getBits();
        System.out.println("Enter key = 8 characters:");
        final int[] keyBits = getBits();

        System.out.println("\nENCRYPTION");
        int outputBits[] = permute(inputBits, keyBits, false);
        System.out.println("\nDECRYPTION");
        permute(outputBits, keyBits, true);
    }

    private static int[] getBits()
    {
        final String input = new Scanner(System.in).nextLine();
        final byte[] bytes = input.getBytes();

        final StringBuilder stringBuilder = new StringBuilder();

        for (byte b : bytes)
        {
            final String s = Integer.toBinaryString(b);

            IntStream.range(s.length(), 8).forEach(it -> stringBuilder.append("0"));

            stringBuilder.append(s);
        }

        final List<Integer> collect = Stream.of(stringBuilder.toString().split(""))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        final int[] output = new int[64];

        IntStream.range(0, 64).forEach(it -> {
            if (collect.size() > it)
            {
                output[it] = collect.get(it);
            }
            else
            {
                output[it] = 0;
            }
        });

        return output;
    }

    private static int[] permute(int[] inputBits, int[] keyBits, boolean isDecrypt)
    {
        // Initial permutation step takes input bits and permutes into the
        // newBits array
        int newBits[] = IntStream.range(0, inputBits.length).map(i -> inputBits[IP[i] - 1]).toArray();

        // 16 rounds will start here
        // L and R arrays are created to store the Left and Right halves of the
        // subkey respectively
        int L[] = new int[32];
        int R[] = new int[32];
        int i;

        System.out.print("Key before PC1 : ");
        displayBits(keyBits, 8);
        // Permuted Choice 1 is done here
        for (i = 0; i < 28; i++)
        {
            C[i] = keyBits[PC1[i] - 1];
        }
        for (; i < 56; i++)
        {
            D[i - 28] = keyBits[PC1[i] - 1];
        }
        System.out.print("\nKey after 56 bit permutation : ");
        displayBits(C, 7);
        System.out.print(" ");
        displayBits(D, 7);
        System.out.print("\nC0 : ");
        displayBits(C, 7);
        System.out.println();
        System.out.print("D0 : ");
        displayBits(D, 7);
        System.out.println();
        // After PC1 the first L and R are ready to be used and hence looping
        // can start once L and R are initialized
        System.arraycopy(newBits, 0, L, 0, 32);
        System.arraycopy(newBits, 32, R, 0, 32);
        System.out.print("Message before IP : ");
        displayBits(inputBits, 8);
        System.out.print("\nMessage after IP : ");
        displayBits(newBits, 8);
        System.out.print("\nL0 = ");
        displayBits(L, 8);
        System.out.print("R0 = ");
        displayBits(R, 8);
        for (int n = 0; n < 16; n++)
        {
            System.out.println("\n-------------");
            System.out.println("Round " + (n + 1) + ":");
            // newR is the new R half generated by the Fiestel function. If it
            // is encrpytion then the KS method is called to generate the
            // subkey otherwise the stored subkeys are used in reverse order
            // for decryption.
            int newR[] = new int[0];

            if (isDecrypt)
            {
                newR = fiestel(R, subkey[15 - n]);
            }
            else
            {
                newR = fiestel(R, KS(n, keyBits));
            }
            // xor-ing the L and new R gives the new L value. new L is stored
            // in R and new R is stored in L, thus exchanging R and L for the
            // next round.
            int newL[] = xor(L, newR);
            L = R;
            R = newL;
            System.out.print("\nL" + (n + 1) + " = ");
            displayBits(L, 4);
            System.out.print("\nR" + (n + 1) + " = ");
            displayBits(R, 4);
            System.out.println();
        }

        // R and L has the two halves of the output before applying the final
        // permutation. This is called the "Preoutput".
        int output[] = new int[64];
        System.arraycopy(R, 0, output, 0, 32);
        System.arraycopy(L, 0, output, 32, 32);
        int finalOutput[] = new int[64];
        System.out.print("R16 L16 : ");
        displayBits(output, 8);
        // Applying FP table to the preoutput, we get the final output:
        // Encryption => final output is ciphertext
        // Decryption => final output is plaintext
        for (i = 0; i < 64; i++)
        {
            finalOutput[i] = output[FP[i] - 1];
        }
        System.out.print("\nR16 L16 AFTER REVERSE IP : ");
        displayBits(finalOutput, 8);
        System.out.println();
        // Since the final output is stored as an int array of bits, we convert
        // it into a hex string:
        StringBuilder hex = new StringBuilder();
        for (i = 0; i < 8; i++)
        {
            StringBuilder bin = new StringBuilder();
            for (int j = 0; j < 8; j++)
            {
                bin.append(finalOutput[(8 * i) + j]);
            }
            int decimal = Integer.parseInt(bin.toString(), 2);
            hex.append(Character.toString((char) decimal));
        }
        if (isDecrypt)
        {
            System.out.print("Decrypted text: ");

        }
        else
        {
            System.out.print("Encrypted text: ");
        }
        System.out.println(hex);
        return finalOutput;
    }

    private static int[] KS(int round, int[] key)
    {
        // The KS (Key Structure) function generates the round keys.
        // C1 and D1 are the new values of C and D which will be generated in
        // this round.
        int C1[] = new int[28];
        int D1[] = new int[28];

        // The rotation array is used to set how many rotations are to be done
        int rotationTimes = (int) rotations[round];
        // leftShift() method is used for rotation (the rotation is basically)
        // a left shift operation, hence the name.
        C1 = leftShift(C, rotationTimes);
        System.out.print("C : ");
        displayBits(C1, 7);
        System.out.print("\nD : ");
        D1 = leftShift(D, rotationTimes);
        displayBits(D1, 7);
        System.out.println();
        // CnDn stores the combined C1 and D1 halves
        int CnDn[] = new int[56];
        System.arraycopy(C1, 0, CnDn, 0, 28);
        System.arraycopy(D1, 0, CnDn, 28, 28);
        // Kn stores the subkey, which is generated by applying the PC2 table
        // to CnDn
        int Kn[] = IntStream.range(0, 48).map(i -> CnDn[PC2[i] - 1]).toArray();

        // Now we store C1 and D1 in C and D respectively, thus becoming the
        // old C and D for the next round. Subkey is stored and returned.
        subkey[round] = Kn;
        C = C1;
        D = D1;
        return Kn;
    }

    private static int[] fiestel(int[] R, int[] roundKey)
    {
        System.out.print("Round key = ");
        displayBits(roundKey, 6);
        System.out.print("\nR : ");
        displayBits(R,4);
        // Method to implement Fiestel function.
        // First the 32 bits of the R array are expanded using E table.
        int expandedR[] = IntStream.range(0, 48).map(i -> R[E[i] - 1]).toArray();
        System.out.print("\nExpanded R : ");
        displayBits(expandedR, 6);
        // We xor the expanded R and the generated round key
        int temp[] = xor(expandedR, roundKey);
        System.out.print("\nK XOR E(R) : ");
        displayBits(temp, 6);
        System.out.println();
        // The S boxes are then applied to this xor result and this is the
        // output of the Fiestel function.
        return sBlock(temp);
    }

    private static int[] xor(int[] a, int[] b)
    {
        // Simple xor function on two int arrays
        return IntStream.range(0, a.length).map(i -> a[i] ^ b[i]).toArray();
    }

    private static int[] sBlock(int[] bits)
    {
        // S-boxes are applied in this method.
        int output[] = new int[32];
        // We know that input will be of 32 bits, hence we will loop 32/4 = 8
        // times (divided by 4 as we will take 4 bits of input at each
        // iteration).
        for (int i = 0; i < 8; i++)
        {
            // S-box requires a row and a column, which is found from the
            // input bits. The first and 6th bit of the current iteration
            // (i.e. bits 0 and 5) gives the row bits.
            final int row[] = new int[2];

            row[0] = bits[6 * i];
            row[1] = bits[(6 * i) + 5];

            final String sRow = row[0] + "" + row[1];
            // Similarly column bits are found, which are the 4 bits between
            // the two row bits (i.e. bits 1,2,3,4)

            int column[] = new int[4];
            column[0] = bits[(6 * i) + 1];
            column[1] = bits[(6 * i) + 2];
            column[2] = bits[(6 * i) + 3];
            column[3] = bits[(6 * i) + 4];

            final String sColumn = column[0] + "" + column[1] + "" + column[2] + "" + column[3];
            // Converting binary into decimal value, to be given into the
            // array as input
            final int iRow = Integer.parseInt(sRow, 2);
            final int iColumn = Integer.parseInt(sColumn, 2);
            final int x = S[i][(iRow * 16) + iColumn];
            // We get decimal value of the S-box here, but we need to convert
            // it into binary:
            final StringBuilder s = new StringBuilder(Integer.toBinaryString(x));
            // Padding is required since Java returns a decimal '5' as '111' in
            // binary, when we require '0111'.
            while (s.length() < 4)
            {
                s.insert(0, "0");
            }
            // The binary bits are appended to the output
            for (int j = 0; j < 4; j++)
            {
                output[(i * 4) + j] = Integer.parseInt(s.charAt(j) + "");
            }
        }
        // P table is applied to the output and this is the final output of one
        // S-box round:
        System.out.print("S1(B1)S2(B2).... : ");
        displayBits(output, 4);
        System.out.println();
        return IntStream.range(0, 32).map(i -> output[P[i] - 1]).toArray();
    }

    private static int[] leftShift(int[] bits, int n)
    {
        // Left shifting takes place here, i.e. each bit is rotated to the left
        // and the leftmost bit is stored at the rightmost bit. This is a left
        // shift operation.
        int answer[] = new int[bits.length];
        System.arraycopy(bits, 0, answer, 0, bits.length);
        IntStream.range(0, n).map(i -> answer[0]).forEach(temp -> {
            System.arraycopy(answer, 1, answer, 0, bits.length - 1);
            answer[bits.length - 1] = temp;
        });
        return answer;
    }

    private static void displayBits(int[] bits, int step)
    {
        // Method to display int array bits as a hexadecimal string.
        for (int i = 0; i < bits.length; i++)
        {
            final int bit = bits[i];
            System.out.print(bit);
            if((i + 1) % step == 0)
            {
                System.out.print(" ");
            }
        }
    }
}