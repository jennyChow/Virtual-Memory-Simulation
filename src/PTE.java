/**
 * Created by jingqiuzhou on 5/31/16.
 */
public class PTE {
    private boolean mValid; // Check if the entry is valid.
    private boolean mRef;	// Reference bit.
    private boolean mMod;	//Check if the entry is modified.
    int mFrameNumber;

    PTE () {
        mValid = false;
        mRef = false;
        mMod = false;
        mFrameNumber = 0;
    }

    PTE (int frameNumber) {
        mValid = true;
        mRef =false;
        mMod = false;
        mFrameNumber = frameNumber;
    }


    boolean isValid() {
        return mValid;
    }
    boolean getRef() {
        return mRef;
    }
    boolean getMod() {
        return mMod;
    }
    int getFrame() {
        return mFrameNumber;
    }
    void setRef () {
        mRef = true;
    }
    void setMod() {
        mRef = true;
        mMod = true;
    }
    void clearRef() {
        mRef = false;
    }
    void clearmod() {
        mMod = false;
    }

    // Validate the page into the indicated frame.
    void validate(int frameNumber) {
        mValid = true;
        mRef = false;
        mMod = false;
        mFrameNumber = frameNumber;
    }

    // Invalidate the entry, which means simulates removal of the page.
    boolean inValidate() {
        boolean ret = mMod;
        mValid = false;
        mRef = false;
        mMod = false;
        mFrameNumber = 0;
        return ret;
    }
}
