package net.chrizzly.dotnetcore4netbeans.filetypes.cs.lexer;

import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

/**
 *
 * @author Chrl
 */
public class CsTokenId implements TokenId {
    private static final Language<CsTokenId> language = new CsLanguageHierarchy().language();
    private final String name;
    private final String primaryCategory;
    private final int id;

    public CsTokenId(String name, String primaryCategory, int id) {
        this.name = name;
        this.primaryCategory = primaryCategory;
        this.id = id;
    }

    public String primaryCategory() {
        return primaryCategory;
    }

    public int ordinal() {
        return id;
    }

    public String name() {
        return name;
    }

    public static final Language<CsTokenId> getLanguage() {
        return language;
    }
}
