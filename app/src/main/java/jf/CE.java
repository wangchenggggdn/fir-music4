package jf;


public class CE extends CA {
    public static final int SCAN_START = 1;
    public static final int SCAN_DONE = 2;

    private int type;
    public CE(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
