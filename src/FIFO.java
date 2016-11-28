/**
 * Created by jingqiuzhou on 5/24/16.
 */
public class FIFO extends ReplacementAlgorithmBase {

    private int mNextReplacePosition = 0;

    FIFO(int frameNumbers) {
        super(frameNumbers, 12);
        mNextReplacePosition = 0;
    }

    @Override
    public String name() {
        return "FIFO";
    }

    @Override
    //find target page to replace
    public int findTargetToReplace(MemRef r) {
        int ret = mNextReplacePosition;
        mNextReplacePosition = rangedIncrease(mNextReplacePosition);
        return ret;
    }
}
