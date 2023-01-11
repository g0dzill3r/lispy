package lispy.builtin

import lispy.*
import java.lang.IllegalArgumentException


private val LIST_EXTRAS = listOf (
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
                          (cdr seq))))""",
    """
    (define (last-pair l) 
      (if (null? (cdr l))
          l
          (last-pair (cdr l))
      )
    )""",
    """
    (define (append list1 list2)
      (if (null? list1)
        list2
        (cons (car list1) (append (cdr list1) list2))))
    """,
    """
    (define (reverse L) 
      (if (null? L)
        ()
        (append (reverse (cdr L)) (list (car L)))
      )
    )""",
    """
    (define (memq item L) 
      (cond 
        ((null? L) #f)
        ((eq? item (car L)) L)
        (else (memq item (cdr L)))
      )
    )""",
    """
    (define (last-pair x)
      (if (null? (cdr x)) x (last-pair (cdr x))))
    """,
    """
    (define (append! x y)
      (set-cdr! (last-pair x) y) 
      x
    )"""
)

private val LIST_BUILTINS = listOf (
    ListOp::class,
    CarOp::class,
    CdrOp::class,
    ConsOp::class,
    NullOp::class,
    AppendOp::class,
    SetCarOp::class,
    SetCdrOp::class,
    ListTailOp::class,
    ListRefOp::class
)

object ListBuiltins : OpSource {
    override val extras: List<String>
        get() = LIST_EXTRAS

    override val buildins: List<Invokable>
        get() = instances (LIST_BUILTINS)
}

class ListOp () : InvokableSupport ("list") {
    override fun invoke (cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp)
        return ConsPair.fromList (eval)
    }
}

class CarOp () : InvokableSupport ("car") {
    override fun invoke (cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp)
        if (eval.size != 1) {
            throw IllegalArgumentException ("Expected 1 argument; found ${eval.size}")
        }
        val arg = eval[0]
        if (arg !is ConsPair) {
            throw IllegalArgumentException ("Invalid argument type for car: ${eval::class.simpleName}")
        }
        return arg.car
    }
}

class CdrOp () : InvokableSupport ("cdr") {
    override fun invoke (cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp)
        if (eval.size != 1) {
            throw IllegalArgumentException ("Expected 1 argument; found ${eval.size}")
        }
        val arg = eval[0]
        if (arg !is ConsPair) {
            throw IllegalArgumentException ("Invalid argument type for car: ${eval::class.simpleName}")
        }
        return arg.cdr
    }
}

class ConsOp () : InvokableSupport ("cons") {
    override fun invoke (cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp, 2)
        val (cons, cdr) = eval
        return if (cdr.isNil) {
            ConsPair (cons)
        } else {
            ConsPair (cons, cdr)
        }
    }
}

class NullOp : InvokableSupport ("null?") {
    override fun invoke (cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp)
        if (eval.size != 1) {
            throw IllegalArgumentException ("Expected 1 argument; found ${eval.size}")
        }
        val isNull = eval[0] == ConsPair.NIL || eval[0] == NilValue
        return BooleanValue (isNull)
    }
}

class SetCarOp: InvokableSupport ("set-car!") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp, 2)
        val pair = requirePair (eval[0])
        pair.car = eval[1]
        return NilValue

    }

}

class SetCdrOp: InvokableSupport ("set-cdr!") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp, 2)
        val pair = requirePair (eval[0])
        pair.cdr = eval[1]
        return NilValue
    }
}

class AppendOp: InvokableSupport ("append") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp)
        if (eval.size != 2) {
            throw IllegalArgumentException ("Expected 2 arguments found ${eval.size} in ${cell}")
        }
        val (a, b) = eval
        val list = mutableListOf<Expression> ()
        list.addAll (a.asPair.toList ())
        list.addAll (b.asPair.toList ())
        return ConsPair.fromList (list)
    }
}

/**
 * Can we just return a pointer into the list instead of a copy?
 */

class ListTailOp : InvokableSupport("list-tail") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp, 2)
        val (list, index) = eval
        val sublist = buildList {
            val els = list.asPair.toList ()
            addAll (els.subList (index.asInt.value, els.size))
        }
        return ConsPair.fromList (sublist)
    }
}

class ListRefOp: InvokableSupport ("list-ref") {
    override fun invoke(cell: ConsPair, interp: Interpreter): Expression {
        val eval = evalList (cell, interp, 2)
        val (list, index) = eval
        return list.asPair.toList ()[index.asInt.value]
    }

}

// EOF