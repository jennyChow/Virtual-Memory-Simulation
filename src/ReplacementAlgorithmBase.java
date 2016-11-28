/**
 * Created by jingqiuzhou on 6/1/16.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

abstract public class ReplacementAlgorithmBase {

    private int mFrameNumber; // Number of frames
    private int mPageBits;	// Page size bits

    // Performance statistic.
    private int mRefCount; 	        // Number of references.
    private int mFaultCount;		// Count of faults.
    private int mRemoveCount;		// Number of pages removed.
    private int mRewriteCount;		// Number of modified pages removed.

    // The page table.
    private Map<Long, PTE> mPageTable = new HashMap<Long, PTE>();

    //The index of the array is the frame number, and the value is page number
    private long[] mMemory;

    // set up mMemory.
    private void initMemory() {
        mMemory = new long[mFrameNumber];
        for (int i = 0; i < mFrameNumber; i++) {
            mMemory[i] = -1;
        }
    }

    public ReplacementAlgorithmBase (int frameNumber, int pageBits) {
        mFrameNumber = frameNumber;
        mPageBits = pageBits;
        mRefCount = 0;
        mFaultCount = 0;
        mRemoveCount =0;
        mRewriteCount = 0;
        initMemory();
    }

    public int getFrameNumbers() {
        return mFrameNumber;
    }

    public int getPageBits() {
        return mPageBits;
    }

    // Read the indicated file and process its references.
    public int read(String filename) {
        System.out.println("==== Processing data ====");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Read and interpret all the lines.
        int count = 0;
        String line = null;
        while (true) {
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line == null) {
                break;
            }
            line = line.trim();
            String[] splitedLine = line.split("[,\\s]+");
            if (splitedLine.length != 3) {
                continue;
            }
            String instructionType = splitedLine[0];
            MemRef.MemRefType refType =null;
            if (instructionType.equals("I")) {
                refType = MemRef.MemRefType.INSTR_FETCH;
            } else if (instructionType.equals("S")) {
                refType = MemRef.MemRefType.STORE;
            } else if (instructionType.equals("L")) {
                refType = MemRef.MemRefType.FETCH;
            } else if (instructionType.equals("M")) {
                refType = MemRef.MemRefType.UPDATE;
            } else {
                continue;
            }
            if (refType == null) {
                continue;
            }

            Long address = Long.parseLong(splitedLine[1], 16);
            int size = Integer.parseInt(splitedLine[2]);
            MemRef r = new MemRef(refType, address, size);
             count ++;
            runMemoryReference(r);
        }
        return count;
    }

    // Simulate the response to a memory reference.
    public void runMemoryReference(MemRef r) {
        // Analyze the reference.
        long pageNumber = pageNumber(r);
        int touchedPageCount = touchedPageCount(r);
        for(int i = 0; i < touchedPageCount; i++, pageNumber++) {
            mRefCount++;
            // Check the frame.
            PTE pte = mPageTable.get(pageNumber);
            if(pte == null || !pte.isValid()) {
                mFaultCount++;

                // Get the frame to use.
                int target = findTargetToReplace(r);
                if(target >= mFrameNumber) {
                    System.out.println("findTargetToReplace() returned out-of-bounds frame" + target );
                    System.exit(1);
                }
                // Remove the existing page.
                long oldPTEPageNumber = mMemory[target];
                PTE oldPTE = mPageTable.get(oldPTEPageNumber);
                if(oldPTE != null && oldPTE.isValid()) {
                    // Remove the current page.
                    mRemoveCount ++;
                    if(oldPTE.getMod()){
                        mRewriteCount++;
                    }
                    mPageTable.remove(oldPTEPageNumber);
                }

                // Insert the the pte.
                if(pte == null) {
                    mPageTable.put(pageNumber, new PTE(target));
                    pte = mPageTable.get(pageNumber);
                } else {
                    pte.validate(target);
                }

                mMemory[target] = pageNumber;
            }

            pte.setRef();
            if (r.isWrite()) {
                pte.setMod();
            }
        }

    }

    // Extract the page number from a reference.
    public long pageNumber(MemRef r) {
        return r.addr >> mPageBits;
    }

    // Calculate the page count. A reference may go across page boundaries, so more page
    // fault will occur when this reference has to be replaced.
    public int touchedPageCount (MemRef r) {

        return (int)((r.addr+r.size-1 >> mPageBits) - pageNumber(r) + 1);
    }

    // Get the PTE for the indicated page number.
    public PTE getPTEByPage(long pageNumber) {
        return mPageTable.get(pageNumber);
    }

    // Get the page table entry by frame
    public PTE getPTEByFrame(int frame) {
        if(frame >= mFrameNumber) {
            System.out.println("get_PTE_by_frame() sent frame number " + frame + " out of range");
            System.exit(1);
        }
        long pageNumber = mMemory[frame];
        return mPageTable.get(pageNumber);
    }

    public int rangedIncrease(int rangedFrameNumber) {
        rangedFrameNumber++;
        if(rangedFrameNumber >= getFrameNumbers()) {
            rangedFrameNumber = 0;
        }
        return rangedFrameNumber;
    }

    public void resetStatus() {
        mRefCount = 0;
        mFaultCount = 0;
        mRemoveCount = 0;
        mRewriteCount = 0;
    }

    public int getRefCount() {
        return mRefCount;
    }

    public int getFaultCount() {
        return mFaultCount;
    }

    public double getFaultRate() {
        double ret = 0.0;
        if (mRefCount > 0) {
            ret = (double)mFaultCount/(double)mRefCount;
        }
        return ret;
    }

    public int getRemoveCount() {
        return mRemoveCount;
    }

    public int getRewriteCount() {
        return mRewriteCount;
    }


    // The name of this algorithm.
    abstract public String name();

    // find a target page to replace.
    abstract public int findTargetToReplace(MemRef r);

    public void printStats() {
        String output = "Algorithm: " + name() + "\n"
                + "Memory: " + mFrameNumber + " frames; Frame Size:" + (1 << mPageBits) + " bytes\n"
                + "Total Reference: " + getRefCount() + "; Page Fault Count: " + getFaultCount() + "; Page Fault Rate: " + getFaultRate() + "\n";
        System.out.println(output);
    }

}
