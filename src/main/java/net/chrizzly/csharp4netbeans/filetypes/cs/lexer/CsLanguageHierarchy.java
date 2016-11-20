package net.chrizzly.csharp4netbeans.filetypes.cs.lexer;

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
            new CsTokenId("SINGLE_LINE_COMMENT", "comment", 3),
            new CsTokenId("MULTI_LINE_COMMENT", "comment", 4),
            new CsTokenId("ABSTRACT", "keyword", 6),
            new CsTokenId("AS", "keyword", 7),
            new CsTokenId("BASE", "keyword", 8),
            new CsTokenId("BOOL", "keyword", 9),
            new CsTokenId("BREAK", "keyword", 10),
            new CsTokenId("BYTE", "keyword", 11),
            new CsTokenId("CASE", "keyword", 12),
            new CsTokenId("CATCH", "keyword", 13),
            new CsTokenId("CHAR", "keyword", 14),
            new CsTokenId("CHECKED", "keyword", 15),
            new CsTokenId("CLASS", "keyword", 16),
            new CsTokenId("CONST", "keyword", 17),
            new CsTokenId("CONTINUE", "keyword", 18),
            new CsTokenId("DECIMAL", "keyword", 19),
            new CsTokenId("_DEFAULT", "keyword", 20),
            new CsTokenId("DELEGATE", "keyword", 21),
            new CsTokenId("DO", "keyword", 22),
            new CsTokenId("DOUBLE", "keyword", 23),
            new CsTokenId("ELSE", "keyword", 24),
            new CsTokenId("ENUM", "keyword", 25),
            new CsTokenId("EVENT", "keyword", 26),
            new CsTokenId("EXPLICIT", "keyword", 27),
            new CsTokenId("EXTERN", "keyword", 28),
            new CsTokenId("FALSE", "keyword", 29),
            new CsTokenId("FINALLY", "keyword", 30),
            new CsTokenId("FIXED", "keyword", 31),
            new CsTokenId("FLOAT", "keyword", 32),
            new CsTokenId("FOR", "keyword", 33),
            new CsTokenId("FOREACH", "keyword", 34),
            new CsTokenId("GET", "keyword", 35),
            new CsTokenId("GOTO", "keyword", 36),
            new CsTokenId("IF", "keyword", 37),
            new CsTokenId("IMPLICIT", "keyword", 38),
            new CsTokenId("IN", "keyword", 39),
            new CsTokenId("INT", "keyword", 40),
            new CsTokenId("INTERFACE", "keyword", 41),
            new CsTokenId("INTERNAL", "keyword", 42),
            new CsTokenId("IS", "keyword", 43),
            new CsTokenId("LOCK", "keyword", 44),
            new CsTokenId("LONG", "keyword", 45),
            new CsTokenId("NAMESPACE", "keyword", 46),
            new CsTokenId("NEW", "keyword", 47),
            new CsTokenId("NULL", "keyword", 48),
            new CsTokenId("OBJECT", "keyword", 49),
            new CsTokenId("OPERATOR", "keyword", 50),
            new CsTokenId("OUT", "keyword", 51),
            new CsTokenId("OVERRIDE", "keyword", 52),
            new CsTokenId("PARAMS", "keyword", 53),
            new CsTokenId("PARTIAL", "keyword", 54),
            new CsTokenId("PRIVATE", "keyword", 55),
            new CsTokenId("PROTECTED", "keyword", 56),
            new CsTokenId("PUBLIC", "keyword", 57),
            new CsTokenId("READONLY", "keyword", 58),
            new CsTokenId("REF", "keyword", 59),
            new CsTokenId("RETURN", "keyword", 60),
            new CsTokenId("SBYTE", "keyword", 61),
            new CsTokenId("SEALED", "keyword", 62),
            new CsTokenId("SET", "keyword", 63),
            new CsTokenId("SHORT", "keyword", 64),
            new CsTokenId("SIZEOF", "keyword", 65),
            new CsTokenId("STACKALLOC", "keyword", 66),
            new CsTokenId("STATIC", "keyword", 67),
            new CsTokenId("STRING", "keyword", 68),
            new CsTokenId("STRUCT", "keyword", 69),
            new CsTokenId("SWITCH", "keyword", 70),
            new CsTokenId("THIS", "keyword", 71),
            new CsTokenId("THROW", "keyword", 72),
            new CsTokenId("TRUE", "keyword", 73),
            new CsTokenId("TRY", "keyword", 74),
            new CsTokenId("TYPEOF", "keyword", 75),
            new CsTokenId("UINT", "keyword", 76),
            new CsTokenId("ULONG", "keyword", 77),
            new CsTokenId("UNCHECKED", "keyword", 78),
            new CsTokenId("UNSAFE", "keyword", 79),
            new CsTokenId("USHORT", "keyword", 80),
            new CsTokenId("USING", "keyword", 81),
            new CsTokenId("VAR", "keyword", 82),
            new CsTokenId("VIRTUAL", "keyword", 83),
            new CsTokenId("VOID", "keyword", 84),
            new CsTokenId("VOLATILE", "keyword", 85),
            new CsTokenId("WHILE", "keyword", 86),
            new CsTokenId("INTEGER_LITERAL", "keyword", 87),
            new CsTokenId("DECIMAL_LITERAL", "keyword", 88),
            new CsTokenId("HEX_LITERAL", "keyword", 89),
            new CsTokenId("OCTAL_LITERAL", "keyword", 90),
            new CsTokenId("FLOATING_POINT_LITERAL", "keyword", 91),
            new CsTokenId("DECIMAL_FLOATING_POINT_LITERAL", "keyword", 92),
            new CsTokenId("DECIMAL_EXPONENT", "keyword", 93),
            new CsTokenId("HEXADECIMAL_FLOATING_POINT_LITERAL", "keyword", 94),
            new CsTokenId("HEXADECIMAL_EXPONENT", "keyword", 95),
            new CsTokenId("CHARACTER_LITERAL", "keyword", 96),
            new CsTokenId("STRING_LITERAL", "keyword", 97),
            new CsTokenId("IDENTIFIER", "identifier", 98),
            new CsTokenId("LETTER", "identifier", 99),
            new CsTokenId("PART_LETTER", "identifier", 100),
            new CsTokenId("LPAREN", "separator", 101),
            new CsTokenId("RPAREN", "separator", 102),
            new CsTokenId("LBRACE", "separator", 103),
            new CsTokenId("RBRACE", "separator", 104),
            new CsTokenId("LBRACKET", "separator", 105),
            new CsTokenId("RBRACKET", "separator", 106),
            new CsTokenId("SEMICOLON", "separator", 107),
            new CsTokenId("COMMA", "separator", 108),
            new CsTokenId("DOT", "separator", 109),
            new CsTokenId("AT", "separator", 110),
            new CsTokenId("ASSIGN", "operator", 111),
            new CsTokenId("LT", "operator", 112),
            new CsTokenId("BANG", "operator", 113),
            new CsTokenId("TILDE", "operator", 114),
            new CsTokenId("HOOK", "operator", 115),
            new CsTokenId("COLON", "operator", 116),
            new CsTokenId("EQ", "operator", 117),
            new CsTokenId("LE", "operator", 118),
            new CsTokenId("GE", "operator", 119),
            new CsTokenId("NE", "operator", 120),
            new CsTokenId("SC_OR", "operator", 121),
            new CsTokenId("SC_AND", "operator", 122),
            new CsTokenId("INCR", "operator", 123),
            new CsTokenId("DECR", "operator", 124),
            new CsTokenId("PLUS", "operator", 125),
            new CsTokenId("MINUS", "operator", 126),
            new CsTokenId("STAR", "operator", 127),
            new CsTokenId("SLASH", "operator", 128),
            new CsTokenId("BIT_AND", "operator", 129),
            new CsTokenId("BIT_OR", "operator", 130),
            new CsTokenId("XOR", "operator", 131),
            new CsTokenId("REM", "operator", 132),
            new CsTokenId("LSHIFT", "operator", 133),
            new CsTokenId("PLUSASSIGN", "operator", 134),
            new CsTokenId("MINUSASSIGN", "operator", 135),
            new CsTokenId("STARASSIGN", "operator", 136),
            new CsTokenId("SLASHASSIGN", "operator", 137),
            new CsTokenId("ANDASSIGN", "operator", 138),
            new CsTokenId("ORASSIGN", "operator", 139),
            new CsTokenId("XORASSIGN", "operator", 140),
            new CsTokenId("REMASSIGN", "operator", 141),
            new CsTokenId("LSHIFTASSIGN", "operator", 142),
            new CsTokenId("RSIGNEDSHIFTASSIGN", "operator", 143),
            new CsTokenId("RUNSIGNEDSHIFTASSIGN", "operator", 144),
            new CsTokenId("ELLIPSIS", "operator", 145),
            new CsTokenId("STUFF_TO_IGNORE", "comment", 147)
//            new CsTokenId("DEFAULT", "comment", 146), // FIXME
//            new CsTokenId("IN_MULTI_LINE_COMMENT", "comment", 148)
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
