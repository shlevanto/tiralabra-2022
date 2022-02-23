package compressor;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import tool.Pair;
import config.Config;

public class BWTRLE {
    
    private BWT bwt;
    private RLE rle;
    private int chunkSize;
    private Config properties;

    public BWTRLE(Config properties) {
        this.bwt = new BWT();
        this.rle = new RLE();
        this.chunkSize = properties.getBwtChunkSize();
    }

    public ArrayList<Pair<char[], int[]>> encode(String s) {
        
        int start = 0;
        int noOfChunks = s.length() / this.chunkSize;
        
        ArrayList<String> chunks = new ArrayList<>();

        // add full-sized chunks to list
        for (int i = 0; i < noOfChunks; i++) {
            int end = start + this.chunkSize;
            String part = s.substring(start, end);
            chunks.add(part);
            start += this.chunkSize;
        }
        
        // and remainder
        chunks.add(s.substring(start));

        // BWT encode all chunks
        ArrayList<Pair<char[], int[]>> encodedChunks = new ArrayList<>();

        for (String chunk : chunks) {
            String bwtEncoded = this.bwt.encode(chunk);
            Pair<char[], int[]> doubleEncoded = this.rle.encode(bwtEncoded);
            encodedChunks.add(doubleEncoded);
        }

        return encodedChunks;
    }

    public String decode (ArrayList<Pair<char[], int[]>> encodedChunks) {
        // And decode
        StringBuilder sbChunksDecoded = new StringBuilder();

        for (Pair<char[], int[]> pair : encodedChunks) {
            String rleDecoded = this.rle.decode(pair);
            String bwtDecoded = this.bwt.decode(rleDecoded);
            sbChunksDecoded.append(bwtDecoded);
        }
        
        String chunkEncodedDecoded = sbChunksDecoded.toString();

        return chunkEncodedDecoded;
    }
}