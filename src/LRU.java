/**
 * Created by jingqiuzhou on 5/25/16.
 */
public class LRU extends ReplacementAlgorithmBase {
    private long mCount;
    private long mCounts[];

    public LRU(int frameNumber) {
        super(frameNumber, 12);
        mCount = 0;
        mCounts = new long[frameNumber];
        for (int i=0; i<frameNumber; i++) {
            mCounts[i] = 0;
        }
    }

    @Override
    public String name() {
        return "LRU";
    }

    @Override
    public void runMemoryReference(MemRef r) {
        PTE pte = getPTEByPage(pageNumber(r));
        if (pte != null && pte.isValid()) {
            mCounts[pte.getFrame()] = mCount;
            mCount ++;
        }
        super.runMemoryReference(r);
    }

    @Override
    public int findTargetToReplace(MemRef r) {
        int ret = 0;
        long minCount = mCounts[0];
        for (int i=1; i<getFrameNumbers(); i++) {
            if (mCounts[i] < minCount) {
                minCount = mCounts[i];
                ret = i;
            }
        }
        mCounts[ret] = mCount;
        mCount ++;
        return ret;
    }

}
