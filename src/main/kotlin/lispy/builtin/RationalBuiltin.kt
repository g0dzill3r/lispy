package lispy.builtin

private val RATIONAL_EXTRAS = listOf (
    """
    (define (make-rat a b) 
        (let ((n (gcd a b)))
            (cons (/ a n) (/ b n))
        )
    )""",
    "(define (numer r) (car r))",
    "(define (denom r) (cdr r))",
    "(define (print-rat r) (format \"%d / %d\" (numer r) (denom r)))",
    "(define one-half (make-rat 1 2))",
    "(define one-third (make-rat 1 3))",
    """(define (add-rat x y)
      (make-rat
        (+ (* (numer x) (denom y)) (* (numer y) (denom x)))
        (* (denom x) (denom y))
      )
    )""",
    """(define (sub-rat x y)
      (make-rat
        (- (* (numer x) (denom y)) (* (numer y) (denom x)))
        (* (denom x) (denom y))
      )
    )""",
    """(define (mul-rat x y)
      (make-rat
        (* (numer x)(numer y))
        (* (denom y)(denom x))
      )
    )""",
    """(define (div-rat x y)
      (make-rat
        (* (numer x)(denom y))
        (* (numer y)(denom x))
       )
    )""",
    """(define (equal-rat? x y)
      (= (* (numer x)(denom y))
         (* (numer y)(denom x))
      )
    )"""
)

object RationalBuiltin: OpSource {
    override val extras: List<String>
        get() = RATIONAL_EXTRAS

    override val buildins: List<Invokable>
        get() = listOf ()

}

// EOF