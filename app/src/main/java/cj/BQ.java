package cj;

public enum BQ {
    SINGLE,
    LOOP,
    LIST,
    SHUFFLE;

    public static BQ getDefault() {
        return LOOP;
    }

    public static BQ switchNextMode(BQ current) {
        if (current == null) return getDefault();

        switch (current) {
            case LOOP:
                return SHUFFLE;
            case SHUFFLE:
                return SINGLE;
            case SINGLE:
                return LOOP;
        }
        return getDefault();
    }
}
