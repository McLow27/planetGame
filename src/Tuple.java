package src;

public class Tuple{
    public final static class Pair<A, B> {
        private A a;
        private B b;

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

    public final static class Triple<A, B, C> {
        private A a;
        private B b;
        private C c;

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

}