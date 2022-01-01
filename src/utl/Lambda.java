package src.utl;

/**
 * Generic interfaces to code lambda functions.
 * 
 * To have multiple objects as parameter or return value, use {@link Tuple}.
 * 
 * @author TheCommandBlock
 * @since 26/12/2021
 */
public interface Lambda {

    /**
     * A generic lambda function that takes one object as parameter and returns null.
     */
    public static interface None <T> {
        /**
         * Executes the coded lambda function with a parameter
         * 
         * @param tpl a parameter of the generic type
         */
        public void exec(T tpl);
    }

    /**
     * A generic lambda function that takes one object as parameter and returns a boolean value.
     */
    public static interface Bool <T> {
        /**
         * Executes the coded lambda function with a parameter
         * 
         * @param tpl a parameter of the generic type
         * @return a boolean value
         */
        public boolean check(T tpl);
    }

    /**
     * A generic lambda function that takes one object as parameter and returns an object 
     * not necessarily of the same type.
     */
    public static interface Yield <T, R> {
        /**
         * Executes the coded lambda function with a parameter
         * 
         * @param tpl a parameter of the first generic type
         * @return an object or value of the second generic type
         */
        public R yield(T tpl);
    }

}
