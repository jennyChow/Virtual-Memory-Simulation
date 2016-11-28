/**
 * Created by jingqiuzhou on 6/2/16.
 */


public class NRU extends ReplacementAlgorithmBase {

    private int mFrameScanStart = 0; // to record which frame starts to scan
    private int mMaintainFreq = 1000;
    private int mMaintainCount = 0;

    public NRU(int frameNumber, int maintainFreq) {
        super(frameNumber, 12);
        mMaintainFreq = maintainFreq;
        mMaintainCount = 0;
    }

    // define the class that a frame belongs to
    private int frameClass(PTE pte) {
        if (pte == null) {
            return 0;
        }
        if (!pte.getMod() && !pte.getRef()) {
            return 0;
        } else if (pte.getMod() && !pte.getRef()) {
            return 1;
        } else if (!pte.getMod() && pte.getRef()) {
            return 2;
        } else {
            return 3;
        }
    }

    @Override
    public String name() {
        return "NRU";
    }

    @Override
    public void runMemoryReference(MemRef r) {
        mMaintainCount ++;
        if (mMaintainCount >= mMaintainFreq) {
            maintain();
            mMaintainCount = 0;
        }
        super.runMemoryReference(r);
    }

    @Override
    public int findTargetToReplace(MemRef r) {
        boolean found[] = {false, false, false, true};
        int which[] = {0, 0, 0, mFrameScanStart};

        int scan = mFrameScanStart;
        do {
            int pageClass = frameClass(getPTEByFrame(scan));
            if (pageClass < 3 && found[pageClass] == false) {
                found[pageClass] = true;
                which[pageClass] = scan;
                if (pageClass == 0)
                    break;
            }
            scan = rangedIncrease(scan);
        } while (scan != mFrameScanStart);

        for (int i = 0; i<=3; i++) {
            if (found[i]) {
                int ret = which[i];
                mFrameScanStart = rangedIncrease(ret);
                return ret;
            }
        }
        return 0;
    }

    // clear reference bits
    private void maintain() {
        for (int i=0; i<getFrameNumbers(); i++) {
            PTE pte = getPTEByFrame(i);
            if (pte != null) {
                pte.clearRef();
            }
        }
    }

}