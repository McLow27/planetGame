package src.utl;

public class Tuple {

    public final static class Pair<A, B> extends Tuple {
        private A a;
        private B b;
        public static final int args = 2;

        public Pair() {
            this.a = null;
            this.b = null;
        }

        public Pair(A a, B b) {
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

    public final static class Triple<A, B, C> extends Tuple {
        private A a;
        private B b;
        private C c;
        public static final int args = 3;

        public Triple() {
            this.a = null;
            this.b = null;
            this.c = null;
        }

        public Triple(A a, B b, C c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public A getAlpha() {
            return a;
        }

        public B getBeta() {
            return b;
        }

        public C getGamma() {
            return c;
        }

        public void setAlpha(A a) {
            this.a = a;
        }

        public void setBeta(B b) {
            this.b = b;
        }

        public void setGamma(C c) {
            this.c = c;
        }
    }

    public final static class Quadruple<A, B, C, D> extends Tuple {
        private A a;
        private B b;
        private C c;
        private D d;
        public static final int args = 4;

        public Quadruple() {
            this.a = null;
            this.b = null;
            this.c = null;
            this.d = null;
        }

        public Quadruple(A a, B b, C c, D d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        public A getAlpha() {
            return a;
        }

        public B getBeta() {
            return b;
        }

        public C getGamma() {
            return c;
        }

        public D getDelta() {
            return d;
        }

        public void setAlpha(A a) {
            this.a = a;
        }

        public void setBeta(B b) {
            this.b = b;
        }

        public void setGamma(C c) {
            this.c = c;
        }

        public void setDelta(D d)  {
            this.d = d;
        }
    }

    public final static class Quintuple<A, B, C, D, E> extends Tuple {
        private A a;
        private B b;
        private C c;
        private D d;
        private E e;
        public static final int args = 5;

        public Quintuple() {
            this.a = null;
            this.b = null;
            this.c = null;
            this.d = null;
            this.e = null;
        }

        public Quintuple(A a, B b, C c, D d, E e) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.e = e;
        }

        public A getAlpha() {
            return a;
        }

        public B getBeta() {
            return b;
        }

        public C getGamma() {
            return c;
        }

        public D getDelta() {
            return d;
        }

        public E getEpsilon() {
            return e;
        }

        public void setAlpha(A a) {
            this.a = a;
        }

        public void setBeta(B b) {
            this.b = b;
        }

        public void setGamma(C c) {
            this.c = c;
        }

        public void setDelta(D d)  {
            this.d = d;
        }

        public void setEpsilon(E e) {
            this.e = e;
        }
    }

}