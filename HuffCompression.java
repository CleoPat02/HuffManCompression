import java.io.*;
import java.util.*;

public class HuffCompression {
    private static StringBuilder sb = new StringBuilder();
    private static Map<Byte, String> huffmap = new HashMap<>();

    public static void compress(String src, String dst) {// source and destintion path
        try {
            FileInputStream input = new FileInputStream(src); // obtains data in the form of raw bytes from
                                                              // text/audio/video
            byte[] b = new byte[input.available()];// no of bytes in the file
            input.read(b);
            byte[] encoded = HuffmanCode(b);
            OutputStream outStream = new FileOutputStream(dst);
            ObjectOutputStream objectOutStream = new ObjectOutputStream(outStream);
            objectOutStream.writeObject(encoded);
            objectOutStream.writeObject(huffmap);
            input.close();
            objectOutStream.close();
            outStream.close();
            ;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static byte[] HuffmanCode(byte[] bytes) {
        PriorityQueue<ByteNode> nodes = getNodes(bytes);// create min priority queue
        ByteNode root = HuffmanTree(nodes);// create huffman tree
        Map<Byte, String> HuffCode = getHuffCode(root);// create lookup of character and their encoded value
        byte[] EncodedStream = CombineBytes(bytes, HuffCode); // convert the map to a byte stream which becomes the
        return EncodedStream; // output (encoded file)
        // we have a stream of characters which we store in a byte array
        // after the code we have a stream of encoded version of the characters which we
        // store in a byte array

    }

    private static PriorityQueue<ByteNode> getNodes(byte[] bytes) {
        PriorityQueue<ByteNode> nodes = new PriorityQueue<>();
        Map<Byte, Integer> freq = new HashMap<>();// map consisting of frequncy of each character
        for (byte b : bytes) {
            Integer value = freq.get(b);
            if (value == null) {
                freq.put(b, 1);
            } else {
                freq.put(b, value + 1);
            }
        }
        for (Map.Entry<Byte, Integer> entry : freq.entrySet()) {
            nodes.add(new ByteNode(entry.getKey(), entry.getValue()));
        }
        return nodes;
    }

    private static ByteNode HuffmanTree(PriorityQueue<ByteNode> nodes) {
        while (nodes.size() > 1) {
            ByteNode left = nodes.poll();
            ByteNode right = nodes.poll();
            ByteNode parent = new ByteNode(null, left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;
            nodes.add(parent);
        }
        return nodes.poll();
    }

    private static Map<Byte, String> getHuffCode(ByteNode root) {
        if (root == null) {
            return null;
        }
        getHuffCode(root.left, "0", sb);
        getHuffCode(root.right, "1", sb);
        return huffmap;
    }

    private static void getHuffCode(ByteNode node, String code, StringBuilder sb) {
        StringBuilder sb1 = new StringBuilder();// string builder object efficient in memory than string

        sb1.append(code);
        if (node != null) {
            if (node.data == null) {
                getHuffCode(node.left, "0", sb1);
                getHuffCode(node.right, "1", sb1);
            } else {
                huffmap.put(node.data, sb1.toString());
            }
        }
    }

    private static byte[] CombineBytes(byte[] bytes, Map<Byte, String> code) {
        // Calculate the total number of bits needed for encoding
        int totalBits = 0;
        for (byte b : bytes) {
            totalBits += code.get(b).length();
        }

        // Initialize the encoded byte array
        byte[] encoded = new byte[(totalBits + 7) / 8];
        int byteIndex = 0; // Index to keep track of the current byte being filled
        int bitIndex = 0; // Index to keep track of the current bit position in the byte

        // Iterate over each byte in the original data
        for (byte b : bytes) {
            String huffCode = code.get(b);
            // Append the bits of Huffman code to the encoded byte array
            for (int i = 0; i < huffCode.length(); i++) {
                // Set the bit at the current bit index in the current byte
                if (huffCode.charAt(i) == '1') {
                    encoded[byteIndex] |= (1 << (7 - bitIndex));
                }
                bitIndex++;
                // If the current byte is full, move to the next byte
                if (bitIndex == 8) {
                    byteIndex++;
                    bitIndex = 0;
                }
            }
        }

        return encoded;
    }

    public static void main(String args[]) {
        compress("C:\\Users\\KIIT\\Desktop\\text.txt",
                "C:\\Users\\KIIT\\Desktop\\compressedtext2.txt");

        // decompress("C:\\Users\\KIIT\\Desktop\\compressedtext2.txt",
        // "C:\\Users\\KIIT\\Desktop\\text5.txt");
    }

}
