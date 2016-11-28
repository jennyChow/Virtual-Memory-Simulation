/**
 * Created by jingqiuzhou on 5/31/16.
 */

/*
 * the object of class MemRef represent a reference. Every reference has a Boolean type,
 * a reference address, and a reference size in bytes.
 */
public class MemRef {

    // References are classified as instruction fetch, data store, data fetch, and data update.
    public enum MemRefType {
      INSTR_FETCH, STORE, FETCH, UPDATE
    }

    public MemRefType type;
    public long addr;
    public int size;

    MemRef() {
        type = null;
        addr = 0;
        size = 0;
    }

    MemRef(MemRefType _type, long _addr, int _size) {
        type = _type;
        addr = _addr;
        size = _size;
    }

    // Check if this type modify the memory.
    public boolean isWrite() {
        return type == MemRefType.STORE || type == MemRefType.UPDATE;
    }
}
