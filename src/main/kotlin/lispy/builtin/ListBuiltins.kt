package lispy.builtin

import lispy.*
import java.lang.IllegalArgumentException

class ListOp () : InvokableSupport ("list") {
    override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression {
        val eval = evalList (cell, interp)
        return ExpressionCell.fromList (eval)
    }
}

class CarOp () : InvokableSupport ("car") {
    override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression {
        val eval = evalList (cell, interp)
        if (eval.size != 1) {
            throw IllegalArgumentException ("Expected 1 argument; found ${eval.size}")
        }
        val arg = eval[0]
        if (arg !is ExpressionCell) {
            throw IllegalArgumentException ("Invalid argument type for car: ${eval::class.simpleName}")
        }
        return arg.car
    }
}

class CdrOp () : InvokableSupport ("cdr") {
    override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression {
        val eval = evalList (cell, interp)
        if (eval.size != 1) {
            throw IllegalArgumentException ("Expected 1 argument; found ${eval.size}")
        }
        val arg = eval[0]
        if (arg !is ExpressionCell) {
            throw IllegalArgumentException ("Invalid argument type for car: ${eval::class.simpleName}")
        }
        return arg.cdr
    }
}

class ConsOp () : InvokableSupport ("cons") {
    override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression {
        val eval = evalList (cell, interp)
        if (eval.size != 2) {
            throw IllegalArgumentException ("Invalid argument count; expected 2 found ${eval.size}")
        }
        return ExpressionCell (eval[0], eval[1])
    }
}

class NullOp : InvokableSupport ("null?") {
    override fun invoke (cell: ExpressionCell, interp: Interpreter): Expression {
        val eval = evalList (cell, interp)
        if (eval.size != 1) {
            throw IllegalArgumentException ("Expected 1 argument; found ${eval.size}")
        }
        val isNull = eval[0] == ExpressionCell.NIL || eval[0] == NilValue
        return BooleanValue (isNull)
    }
}

val LIST_EXTRAS = listOf (
    "(define (cadr x) (car (cdr x)))",
    "(define (caddr x) (car (cdr (cdr x))))",
    "(define (cadddr x) (car (cdr (cdr (cdr x)))))",
    "(define (mapcar f L) (if (null? L) '() (cons (f (car L)) (mapcar f (cdr L)))))",
    "(define (reverse x) (_reverse x '()))",
    "(define (_reverse a b) (if (null? a) b (_reverse (cdr a) (cons (car a) b))))",
    """(define (fold-right f init seq)
           (if (null? seq)
               init
               (f (car seq)
                  (fold-right f init (cdr seq)))))""",
    """(define (fold-left f init seq)
           (if (null? seq)
               init
               (fold-left f
                          (f init (car seq))
                          (cdr seq))))"""
)

// EOF