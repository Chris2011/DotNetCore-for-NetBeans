package net.chrizzly.csharp4netbeans.filetypes.cs.lexer;

/**
 *
 * @author Chrl
 */
public final class TokenType {
    public int id;
    public String category;
    public String text;

    private TokenType(int id, String category) {
        this.id = id;
        this.category = category;
    }

    public static TokenType valueOf(int id) {
//        TokenType[] values = values();
//        for (TokenType value : values) {
//            if (value.id == id) {
//                return value;
//            }
//        }
//        throw new IllegalArgumentException("The id " + id + " is not recognized");

return null;
    }
}