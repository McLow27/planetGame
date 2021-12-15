package src.srv;

public record ServerResponse(String one) {
    public ServerResponse(byte[] bytes) {
        this(parse(bytes));
    }

    private static String parse(byte[] bytes) {
        return "";
    }
}
