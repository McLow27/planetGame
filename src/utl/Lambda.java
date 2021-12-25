package src.utl;

public interface Lambda {

    public static interface None <T> {
        public void exec(T tpl);
    }

    public static interface Bool <T> {
        public boolean check(T tpl);
    }

    public static interface Yield <T, R> {
        public R yield(T tpl);
    }

}
