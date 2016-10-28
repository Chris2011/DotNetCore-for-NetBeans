package org.chrisle.netbeans.plugins.csharp4netbeans.filetypes.cs.lexer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author chrl
 */
class CsLanguageHierarchy extends LanguageHierarchy<CsTokenId> {

    private static List<CsTokenId> tokens;
    private static Map<Integer, CsTokenId> idToToken;

    private static void init() {
        tokens = Arrays.<CsTokenId>asList(new CsTokenId[]{
            new CsTokenId("EOF", "whitespace", 0),
            new CsTokenId("WHITESPACE", "whitespace", 1),
            new CsTokenId("SINGLE_LINE_COMMENT", "comment", 4),
            new CsTokenId("FORMAL_COMMENT", "comment", 5),
            new CsTokenId("MULTI_LINE_COMMENT", "comment", 6),
            new CsTokenId("ABSTRACT", "keyword", 8),
            new CsTokenId("AS", "keyword", 9),
            new CsTokenId("BASE", "keyword", 10),
            new CsTokenId("BOOL", "keyword", 11),
            new CsTokenId("BREAK", "keyword", 12),
            new CsTokenId("BYTE", "keyword", 13),
            new CsTokenId("CASE", "keyword", 14),
            new CsTokenId("CATCH", "keyword", 15),
            new CsTokenId("CHAR", "keyword", 16),
            new CsTokenId("CHECKED", "keyword", 17),
            new CsTokenId("CLASS", "keyword", 18),
            new CsTokenId("CONST", "keyword", 19),
            new CsTokenId("CONTINUE", "keyword", 20),
            new CsTokenId("DECIMAL", "keyword", 21),
            new CsTokenId("_DEFAULT", "keyword", 22),
            new CsTokenId("DELEGATE", "keyword", 23),
            new CsTokenId("DO", "keyword", 24),
            new CsTokenId("DOUBLE", "keyword", 25),
            new CsTokenId("ELSE", "keyword", 26),
            new CsTokenId("ENUM", "keyword", 27),
            new CsTokenId("EVENT", "keyword", 28),
            new CsTokenId("EXPLICIT", "keyword", 29),
            new CsTokenId("EXTERN", "keyword", 30),
            new CsTokenId("FALSE", "keyword", 31),
            new CsTokenId("FINALLY", "keyword", 32),
            new CsTokenId("FIXED", "keyword", 33),
            new CsTokenId("FLOAT", "keyword", 34),
            new CsTokenId("FOR", "keyword", 35),
            new CsTokenId("FOREACH", "keyword", 36),
            new CsTokenId("GET", "keyword", 151),
            new CsTokenId("GOTO", "keyword", 37),
            new CsTokenId("IF", "keyword", 38),
            new CsTokenId("IMPLICIT", "keyword", 39),
            new CsTokenId("IN", "keyword", 40),
            new CsTokenId("INT", "keyword", 41),
            new CsTokenId("INTERFACE", "keyword", 42),
            new CsTokenId("INTERNAL", "keyword", 43),
            new CsTokenId("IS", "keyword", 44),
            new CsTokenId("LOCK", "keyword", 45),
            new CsTokenId("LONG", "keyword", 46),
            new CsTokenId("NAMESPACE", "keyword", 47),
            new CsTokenId("NEW", "keyword", 48),
            new CsTokenId("NULL", "keyword", 49),
            new CsTokenId("OBJECT", "keyword", 50),
            new CsTokenId("OPERATOR", "keyword", 51),
            new CsTokenId("OUT", "keyword", 52),
            new CsTokenId("OVERRIDE", "keyword", 53),
            new CsTokenId("PARAMS", "keyword", 54),
            new CsTokenId("PARTIAL", "keyword", 152),
            new CsTokenId("PRIVATE", "keyword", 55),
            new CsTokenId("PROTECTED", "keyword", 56),
            new CsTokenId("PUBLIC", "keyword", 57),
            new CsTokenId("READONLY", "keyword", 58),
            new CsTokenId("REF", "keyword", 59),
            new CsTokenId("RETURN", "keyword", 60),
            new CsTokenId("SBYTE", "keyword", 61),
            new CsTokenId("SEALED", "keyword", 62),
            new CsTokenId("SET", "keyword", 150),
            new CsTokenId("SHORT", "keyword", 63),
            new CsTokenId("SIZEOF", "keyword", 64),
            new CsTokenId("STACKALLOC", "keyword", 65),
            new CsTokenId("STATIC", "keyword", 66),
            new CsTokenId("STRING", "keyword", 67),
            new CsTokenId("STRUCT", "keyword", 68),
            new CsTokenId("SWITCH", "keyword", 69),
            new CsTokenId("THIS", "keyword", 70),
            new CsTokenId("THROW", "keyword", 71),
            new CsTokenId("TRUE", "keyword", 72),
            new CsTokenId("TRY", "keyword", 73),
            new CsTokenId("TYPEOF", "keyword", 74),
            new CsTokenId("UINT", "keyword", 75),
            new CsTokenId("ULONG", "keyword", 76),
            new CsTokenId("UNCHECKED", "keyword", 77),
            new CsTokenId("UNSAFE", "keyword", 78),
            new CsTokenId("USHORT", "keyword", 79),
            new CsTokenId("USING", "keyword", 80),
            new CsTokenId("VAR", "keyword", 149),
            new CsTokenId("VIRTUAL", "keyword", 81),
            new CsTokenId("VOID", "keyword", 82),
            new CsTokenId("VOLATILE", "keyword", 83),
            new CsTokenId("WHILE", "keyword", 84),
            new CsTokenId("INTEGER_LITERAL", "keyword", 85),
            new CsTokenId("DECIMAL_LITERAL", "keyword", 86),
            new CsTokenId("HEX_LITERAL", "keyword", 87),
            new CsTokenId("OCTAL_LITERAL", "keyword", 88),
            new CsTokenId("FLOATING_POINT_LITERAL", "keyword", 89),
            new CsTokenId("DECIMAL_FLOATING_POINT_LITERAL", "keyword", 90),
            new CsTokenId("DECIMAL_EXPONENT", "keyword", 91),
            new CsTokenId("HEXADECIMAL_FLOATING_POINT_LITERAL", "keyword", 92),
            new CsTokenId("HEXADECIMAL_EXPONENT", "keyword", 93),
            new CsTokenId("CHARACTER_LITERAL", "keyword", 94),
            new CsTokenId("STRING_LITERAL", "keyword", 95),
            new CsTokenId("IDENTIFIER", "identifier", 96),
            new CsTokenId("LETTER", "identifier", 97),
            new CsTokenId("PART_LETTER", "identifier", 98),
            new CsTokenId("LPAREN", "separator", 99),
            new CsTokenId("RPAREN", "separator", 100),
            new CsTokenId("LBRACE", "separator", 101),
            new CsTokenId("RBRACE", "separator", 102),
            new CsTokenId("LBRACKET", "separator", 103),
            new CsTokenId("RBRACKET", "separator", 104),
            new CsTokenId("SEMICOLON", "separator", 105),
            new CsTokenId("COMMA", "separator", 106),
            new CsTokenId("DOT", "separator", 107),
            new CsTokenId("AT", "separator", 108),
            new CsTokenId("ASSIGN", "operator", 109),
            new CsTokenId("LT", "operator", 110),
            new CsTokenId("BANG", "operator", 111),
            new CsTokenId("TILDE", "operator", 112),
            new CsTokenId("HOOK", "operator", 113),
            new CsTokenId("COLON", "operator", 114),
            new CsTokenId("EQ", "operator", 115),
            new CsTokenId("LE", "operator", 116),
            new CsTokenId("GE", "operator", 117),
            new CsTokenId("NE", "operator", 118),
            new CsTokenId("SC_OR", "operator", 119),
            new CsTokenId("SC_AND", "operator", 120),
            new CsTokenId("INCR", "operator", 121),
            new CsTokenId("DECR", "operator", 122),
            new CsTokenId("PLUS", "operator", 123),
            new CsTokenId("MINUS", "operator", 124),
            new CsTokenId("STAR", "operator", 125),
            new CsTokenId("SLASH", "operator", 126),
            new CsTokenId("BIT_AND", "operator", 127),
            new CsTokenId("BIT_OR", "operator", 128),
            new CsTokenId("XOR", "operator", 129),
            new CsTokenId("REM", "operator", 130),
            new CsTokenId("LSHIFT", "operator", 131),
            new CsTokenId("PLUSASSIGN", "operator", 132),
            new CsTokenId("MINUSASSIGN", "operator", 133),
            new CsTokenId("STARASSIGN", "operator", 134),
            new CsTokenId("SLASHASSIGN", "operator", 135),
            new CsTokenId("ANDASSIGN", "operator", 136),
            new CsTokenId("ORASSIGN", "operator", 137),
            new CsTokenId("XORASSIGN", "operator", 138),
            new CsTokenId("REMASSIGN", "operator", 139),
            new CsTokenId("LSHIFTASSIGN", "operator", 140),
            new CsTokenId("RSIGNEDSHIFTASSIGN", "operator", 141),
            new CsTokenId("RUNSIGNEDSHIFTASSIGN", "operator", 142),
            new CsTokenId("ELLIPSIS", "operator", 143),
            new CsTokenId("STUFF_TO_IGNORE", "comment", 145), // FIXME
            new CsTokenId("DEFAULT", "comment", 146), // FIXME
            new CsTokenId("IN_FORMAL_COMMENT", "comment", 147),
            new CsTokenId("IN_MULTI_LINE_COMMENT", "comment", 148)
        }
        );
        idToToken = new HashMap<Integer, CsTokenId>();
        for (CsTokenId token : tokens) {
            idToToken.put(token.ordinal(), token);
        }
    }

    static synchronized CsTokenId getToken(int id) {
        if (idToToken == null) {
            init();
        }
        return idToToken.get(id);
    }

    @Override
    protected synchronized Collection<CsTokenId> createTokenIds() {
        if (tokens == null) {
            init();
        }
        return tokens;
    }

    @Override
    protected synchronized Lexer<CsTokenId> createLexer(LexerRestartInfo<CsTokenId> info) {
        return new CsLexer(info);
    }

    @Override
    protected String mimeType() {
        return "text/x-cs";
    }
}
