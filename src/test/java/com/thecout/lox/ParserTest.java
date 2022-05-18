package com.thecout.lox;

import com.thecout.lox.Parser.Parser;
import com.thecout.lox.Parser.Stmts.Function;
import com.thecout.lox.Parser.Stmts.Print;
import com.thecout.lox.Parser.Stmts.Stmt;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest {
    static final String program = """
            fun printSum(a,b) {
                print a+b;
                var x;
                var z = 2 * 3 + 4; // punkt vor strich sollte klappen
                
                for(var y = 0;y<3 and y>-1;y=y+1){
                    if(y==2){
                        return false;
                    }
                    else
                        x = 10-5 == 5 or -(-5*2) == 7;
                }
                printSum(x+1,y/2);
                
                fun innerFunction(){
                    print "i am an inner function";
                }
                
                var text = "hello world";
                
                print text + "!";;;;;;;
                
                {
                    var privateVar = 69;
                }
                
            }
            print 25+60;
            """;

    @Test
    void parseTest() {
        Scanner scanner = new Scanner(program);
        List<Token> actual = scanner.scan();
        Parser parser = new Parser(actual);
        List<Stmt> statements = parser.parse();
        assertTrue(statements.get(0) instanceof Function, "Expected Type Function got " + actual.get(0).getClass().getName());
        assertTrue(statements.get(1) instanceof Print, "Expected Type Print got " + actual.get(0).getClass().getName());
        assertTrue(((Function) statements.get(0)).body.get(0) instanceof Print, "Expected Type Print in function");
        assertEquals(((Function) statements.get(0)).parameters.get(0).type, TokenType.IDENTIFIER, "Expected first function parameter to be identifier");

    }
}
