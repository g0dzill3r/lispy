# Scheme in Kotlin

A simple scheme interpreter written in Kotlin.

See https://web.mit.edu/6.001/6.037/sicp.pdf 

## Working with lists

```agsl
repl> (list 1 2 3 4 5)
-> (1 2 3 4 5)
repl> (list (list 1 2) (list 3 4) 5)
-> ((1 2) (3 4) 5)
```

### Logical Operations

```agsl
repl> #t
-> true
repl> #f
-> false
repl> (and #t #f)
-> false
repl> (and #t 1)
-> 1
repl> (or #f "lee")
-> "lee"
repl> (not #f)
-> true
```

## Math operations

```agsl
repl> (+ 1 2 3 4 5)
-> 15
repl> (* 1 2 3 4 5)
-> 120
repl> (/ 10 2.5)
-> 4.0
repl> (% 123 2)
-> 1
repl> (< 1 2)
-> true
repl> (= 12 23)
-> false
repl> (* (+ 1 2) (- 2 3) (/ 4 6))
-> 0
```

## Defining symbols

```agsl
repl> (define radius 3)
-> nil
repl> (define pi 3.14159)
-> nil
repl> (define circumference (* pi (* radius radius)))
-> nil
repl> circumference
-> 28.274311
```

## Lists

```agsl
repl> (define foo '(1 2 3 4 5 6 7))
-> nil
repl> (car foo)
-> 1
repl> (cdr foo)
-> (2 3 4 5 6 7)
repl> (car (cdr foo))
-> 2
repl> (car (cdr (cdr foo)))
-> 3
repl> (length foo)
-> 7
repl> (null? foo)
-> false
repl> (null? '())
-> true
```
## Bound Functions

```agsl
repl> (define (square x) (* x x))
-> nil
repl> (define (cube x) (* x (square x)))
-> nil
repl> (square 3)
-> 9
(cube 3)
-> 27
```

## Lambdas

```agsl
repl> (define identity (lambda (x) x))
-> nil
repl> (identity 123)
-> 123
repl> (identity '(1 2 3))
-> (1 2 3)
repl> (mapcar (lambda (x) (* x x)) '(1 2 3))
-> (1 4 9)
```

## Utility

```agsl
repl> ($reset)
-> true
repl> ($scope)
-> nil
repl> ($env)
else: true
$scope: builtin:$scope
$env: builtin:$env
$reset: builtin:$reset
noop: builtin:noop
dump: builtin:dump
quote: builtin:quote
define: builtin:define
display: builtin:display
lambda: builtin:lambda
+: builtin:+
*: builtin:*
.
.
.
```
