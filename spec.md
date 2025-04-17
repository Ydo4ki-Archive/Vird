# Vird Programming Language

<sup>Short specification v1.0-prerelease-1</sup>

<!-- TOC -->
* [Vird Programming Language](#vird-programming-language)
  * [Introduction](#introduction)
    * [Key Features:](#key-features)
  * [Syntax](#syntax)
    * [Comments](#comments)
    * [Whitespace](#whitespace)
  * [Semantics](#semantics)
<!-- TOC -->

## Introduction

Vird — minimalistic homoiconicity programming language with Lisp-like syntax,
static typing and an emphasis on metaprogramming. Vird works as an embedded interpretable language
(runs by host program, which can be pure interpreter itself),
but files and code snippets can be compiled to C via standard library functions.

### Key Features:

* Simple syntax, consists only from Symbols and ExprLists with different types of brackets
* Ability to work with values in early stages of AST (Symbols and ExprLists)
* No keywords, reserved symbols or special characters
* No naming limits
* Implicit conversions (even symbol resolving or numeric literals creation is an
  implicit conversion from Symbol to the expected type)

## Syntax

Creating a program in Vird is a description of a data structure of the _Expr_ type,
which can then be processed by a specific function (Expr handler)

_Expr_ is one of two possible structures: _Symbol_ and _ExprList_

***
_Symbol_ is defined by a text that it contains. In source code symbols are separated with whitespace characters
(spaces, return symbols, and tabs), brackets (for more information on brackets, see further), "," and ";"
characters which are always represent separate symbols regardless of delimiters.
You can also use quotes (') and double quotes (") to capture a raw text to the Symbol
(except `\<quote symbol you used>` to capture quotes themselves).

Symbols are case-sensitive, meaning `foo`, `Foo`, and `FOO` are three distinct symbols.

Examples:

| source                  | meaning                                                                                                                       |
|-------------------------|-------------------------------------------------------------------------------------------------------------------------------|
| `Foo 24, i++51 f/*;4a`  | sequence of symbols `Foo`, `24`, `,`, `i++51`, `f/*`, `;` and `4a`                                                            |
| `"Bar \' aa(5^\" \\ \"` | a single symbol `"Bar \' aa(5^" \\ \"` (quotes are included to symbol payload)                                                |
| `'Multiline\nStrin\g'`  | a single symbol  `'Multiline\nStrin\g'` (`\n` does not converts to line breaks, you must put an actual line break to do  that |

***
_ExprList_ is an ordered list of _Expr_ objects and is defined by _Expr_ objects
this list contains and type of brackets (`()`, `{}` or `[]`).<br>
Will be further designated by the type of brackets _ExprList()_, _ExprList{}_ and _ExprList[]_ respectively.

Empty ExprLists are syntactically valid: `()`, `[]`, and `{}` are all valid expressions.

The semantic difference between different types of brackets (if any) is defined by the Expr handler function.

Closing brackets of incorrect types (if the last open bracket is of a different type) are prohibited.

Examples:

| source             | meaning                                                                                       |
|--------------------|-----------------------------------------------------------------------------------------------|
| `(1 2 3)`          | ExprList() consisting of Symbols `1` `2` and `3`                                              |
| `[4f {51} "foo("]` | ExprList[] consisting of expressions `4f` (Symbol), `{51}` (ExprList{}) and `"foo("` (Symbol) |
| `()`               | Empty ExprList()                                                                              |
| `{[1 2] ()}`       | ExprList{} containing ExprList[] and empty ExprList()                                         |

***

### Comments

Vird supports two types of comments:

- Single-line comments: Start with `//` and continue until the end of the line
- Multi-line comments: Start with `/*` and end with `*/`

Comments are ignored by the parser and can appear anywhere in the code where whitespace is allowed.

Example:

```vird
// This is a single-line comment
(foo bar) // Comment after code
/* This is a
   multi-line comment */
(baz /* inline comment */ qux)
```

### Whitespace

Whitespace characters (spaces, tabs, newlines) are insignificant except when used as delimiters between Symbols.
Multiple consecutive whitespace characters are equivalent to a single whitespace.
***

## Semantics

The following description refers to the standard Expr handler function (evaluate), since the semantics are completely
determined by it
Expr handler function is a function that takes _Expr_ as an argument and returns any type



