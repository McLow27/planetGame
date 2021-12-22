package src.utl;

public interface Lambda {

    public static interface None <T extends Tuple> {
        public void exec(T tpl);
    }

    public static interface Bool <T extends Tuple> {
        public boolean check(T tpl);
    }

    public static interface Yield <T extends Tuple, R> {
        public R yield(T tpl);
    }

}
