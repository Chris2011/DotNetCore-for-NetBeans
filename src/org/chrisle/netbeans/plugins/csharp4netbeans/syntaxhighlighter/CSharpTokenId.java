package org.chrisle.netbeans.plugins.csharp4netbeans.syntaxhighlighter;

import org.netbeans.api.lexer.TokenId;

/**
 *
 * @author chrl
 */
public class CSharpTokenId implements TokenId {
    private final String name;
    private final String primaryCategory;
    private final int id;

    public CSharpTokenId(String name, String primaryCategory, int id) {
        this.name = name;
        this.primaryCategory = primaryCategory;
        this.id = id;
    }
    
    @Override
    public String name() {
        return name;
    }

    @Override
    public int ordinal() {
        return id;
    }

    @Override
    public String primaryCategory() {
        return primaryCategory;
    }   
}