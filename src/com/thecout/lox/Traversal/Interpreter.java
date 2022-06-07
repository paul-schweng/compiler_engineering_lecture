package com.thecout.lox.Traversal;


import com.thecout.lox.Parser.Expr.*;
import com.thecout.lox.Parser.Stmts.*;
import com.thecout.lox.Token;
import com.thecout.lox.Traversal.InterpreterUtils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Interpreter implements ExprVisitor<Object>, StmtVisitor<Void> {

    public final Environment globals = new Environment();
    private Environment environment = globals;


    public Interpreter() {
        globals.define("clock", new LoxCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter,
                               List<Object> arguments) {
                return (double) System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });
    }

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            error.printStackTrace();
        }
    }

    public void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    public void execute(Stmt stmt) {
        stmt.accept(this);
    }


    @Override
    public Object visitAssignExpr(Assign expr) {
        if(expr.name != null)
            this.environment.assign(expr.name, evaluate(expr.value));
        else
            evaluate(expr.value);

        return evaluate(expr.value);
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {

        return switch (expr.operator.type) {
            case BANG_EQUAL -> evaluate(expr.left) != evaluate(expr.right);
            case EQUAL_EQUAL -> evaluate(expr.left) == evaluate(expr.right);
            case GREATER -> (double) evaluate(expr.left) > (double) evaluate(expr.right);
            case GREATER_EQUAL -> (double) evaluate(expr.left) >= (double) evaluate(expr.right);
            case LESS -> (double) evaluate(expr.left) < (double) evaluate(expr.right);
            case LESS_EQUAL -> (double) evaluate(expr.left) <= (double) evaluate(expr.right);
            case PLUS -> (double) evaluate(expr.left) + (double) evaluate(expr.right);
            case MINUS -> (double) evaluate(expr.left) - (double) evaluate(expr.right);
            case STAR -> (double) evaluate(expr.left) * (double) evaluate(expr.right);
            case SLASH -> (double) evaluate(expr.left) / (double) evaluate(expr.right);
            default -> null;
        };
    }

    @Override
    public Object visitCallExpr(Call expr) {

        Object obj = evaluate(expr.callee);

        if(obj instanceof LoxCallable)
            return ((LoxCallable)obj).call(this, new ArrayList<Object>(expr.arguments));

        if(obj instanceof Expr)
            return evaluate((Expr) obj);

        return obj;
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return null;
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Logical expr) {

        return switch (expr.operator.type) {
            case OR -> (boolean) evaluate(expr.left) || (boolean) evaluate(expr.right);
            case AND -> (boolean) evaluate(expr.left) && (boolean) evaluate(expr.right);
            default -> null;
        };
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        Object right = evaluate(expr.right);

        if(expr.operator == null)
            return evaluate(expr.right);

        return switch (expr.operator.type) {
            case BANG -> !(boolean) right;
            case MINUS -> -(double) right;
            default -> null;
        };
    }

    @Override
    public Object visitVariableExpr(Variable expr) {
        return environment.get(expr.name);
    }








    @Override
    public Void visitBlockStmt(Block stmt) {
        executeBlock(stmt.statements, new Environment(this.environment));

        return null;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Function stmt) {
        this.environment.define(
                stmt.name.lexeme,
                new LoxFunction(stmt, this.environment)
        );

        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) {

        if((boolean) evaluate(stmt.condition))
            execute(stmt.thenBranch);
        else if(stmt.elseBranch != null)
            execute(stmt.elseBranch);

        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {

        Object obj = evaluate(stmt.expression);

        if(obj instanceof Double && (double)obj - ((Double) obj).intValue() == 0)
            obj = ((Double) obj).intValue();

        System.out.println(obj);

        return null;
    }

    @Override
    public Void visitReturnStmt(Return stmt) {
        Object value = null;

        if(stmt.value != null)
            value = evaluate(stmt.value);

        throw new LoxReturn(value);
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        this.environment.define(stmt.name.lexeme, stmt.initializer);
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        while ((boolean) evaluate(stmt.condition)) {
            execute(stmt.body);
        }
        return null;
    }

}
