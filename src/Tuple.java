package src;

public class Tuple<A, B> {
    private A a;
    private B b;

    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getAlpha() {
        return a;
    }

    public B getBeta() {
        return b;
    }

    public void setAlpha(A a) {
        this.a = a;
    }

    public void setBeta(B b) {
        this.b = b;
    }
}
