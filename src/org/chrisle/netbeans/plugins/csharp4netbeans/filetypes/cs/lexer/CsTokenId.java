package org.chrisle.netbeans.plugins.csharp4netbeans.filetypes.cs.lexer;

import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

/**
 *
 * @author Chrizzly
 */
public class CsTokenId implements TokenId {
    private final String _name;
    private final String _primaryCategory;
    private final int _id;

    public CsTokenId(String name, String primaryCategory, int id) {
        this._name = name;
        this._primaryCategory = primaryCategory;
        this._id = id;
    }
    
    @Override
    public String name() {
        return _name;
    }

    @Override
    public int ordinal() {
        return _id;
    }

    @Override
    public String primaryCategory() {
        return _primaryCategory;
    } 
    
    public static Language<CsTokenId> getLanguage() {
        return new CsLanguageHierarchy().language();
    }
}