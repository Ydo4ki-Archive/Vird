# Vird Programming Language

<sup>Specification v1.0-prerelease-1</sup>

<!-- TOC -->
* [Vird Programming Language](#vird-programming-language)
  * [Introduction](#introduction)
    * [Key Features](#key-features)
  * [Syntax](#syntax)
    * [Symbol](#symbol)
    * [ExprList](#exprlist)
    * [Comments](#comments)
    * [Whitespace](#whitespace)
  * [Semantics](#semantics)
    * [Val](#val)
    * [Scope](#scope)
    * [Type System](#type-system)
      * [TypeRef](#typeref)
    * [Functions](#functions)
    * [Templates](#templates)
    * [Macros](#macros)
<!-- TOC -->

## Introduction

Vird is a homoiconic programming language with Lisp-like syntax, static typing, and an emphasis on metaprogramming. It functions as an embedded interpretable language (executed by a host program, which may itself be a pure interpreter), with the capability to compile files and code snippets to C via standard library functions.

### Key Features

* Simple syntax consisting only of Symbols and ExprLists with different bracket types
* First-class access to AST components (Symbols and ExprLists) during evaluation
* No reserved keywords, allowing complete naming freedom
* Comprehensive implicit conversion system (including symbol resolution and numeric literal creation)

## Syntax

A Vird program is fundamentally a description of a data structure of the _Expr_ type, which is subsequently processed by an expression handler function.

_Expr_ is a union type with two possible variants: _Symbol_ and _ExprList_.

### Symbol

A _Symbol_ is defined by its textual content. In source code, symbols are delimited by:
- Whitespace characters (spaces, newlines, tabs)
- Brackets (`(`, `)`, `[`, `]`, `{`, `}`)
- Special characters (`,` and `;`), which always represent separate symbols regardless of surrounding delimiters

Quoted text can be used to create symbols containing whitespace or special characters:
- Single quotes (`'...'`) or double quotes (`"..."`) capture raw text into a Symbol
- To include the quote character itself within quoted text, use the escape sequence `\<quote character>`

**Properties:**
- Symbols are case-sensitive (`foo`, `Foo`, and `FOO` are distinct symbols)
- There is no length limitation for symbols
- Symbols may contain any valid Unicode characters when quoted

**Examples:**

| Source Code             | Interpretation                                                                 |
|-------------------------|--------------------------------------------------------------------------------|
| `Foo 24, i++51 f/*;4a`  | Sequence of symbols: `Foo`, `24`, `,`, `i++51`, `f/*`, `;`, `4a`               |
| `"Bar \' aa(5^\" \\ \"` | A single symbol: `"Bar \' aa(5^" \\ \"` (quotes included in symbol content)    |
| `'Multiline\nString'`   | A single symbol: `'Multiline\nString'` (`\n` is not converted to a line break) |
***
### ExprList

An _ExprList_ is an ordered collection of _Expr_ objects (Symbols or nested ExprLists) enclosed by matching brackets. There are three types of ExprLists, distinguished by their bracket style:
- _ExprList()_: Enclosed in parentheses `()`
- _ExprList{}_ : Enclosed in braces `{}`
- _ExprList[]_ : Enclosed in square brackets `[]`

**Properties:**
- Empty ExprLists (`()`, `[]`, `{}`) are syntactically valid
- The semantic difference between bracket types is determined by the expression handler function
- Brackets must be properly matched; closing with an incorrect bracket type is a syntax error

**Examples:**

| Source Code        | Interpretation                                                                             |
|--------------------|--------------------------------------------------------------------------------------------|
| `(1 2 3)`          | ExprList() containing symbols `1`, `2`, and `3`                                            |
| `[4f {51} "foo("]` | ExprList[] containing: symbol `4f`, ExprList{} containing symbol `51`, and symbol `"foo("` |
| `()`               | Empty ExprList()                                                                           |
| `{[1 2] ()}`       | ExprList{} containing: ExprList[] with symbols `1` and `2`, and an empty ExprList()        |
***
### Comments

Vird supports two comment styles that are ignored by the parser:

1. **Single-line comments**: Begin with `//` and continue until the end of the line
2. **Multi-line comments**: Begin with `/*` and end with `*/`

Comments may appear anywhere in the code where whitespace is permitted.

**Examples:**

```vird
// This is a single-line comment
(foo bar) // Comment after code

/* This is a
   multi-line comment */
   
(baz /* inline comment */ qux)
```
***
### Whitespace

Whitespace characters (spaces, tabs, newlines) serve only as delimiters between Symbols and have no semantic significance beyond this role. Multiple consecutive whitespace characters are equivalent to a single whitespace character.
***
## Semantics

The semantic interpretation of Vird code is determined by the expression handler function (_evaluate_). This function processes _Expr_ objects and returns values according to the language's evaluation rules.

When _evaluate_ is called, it attempts to find an appropriate implicit conversion function from the provided _Expr_ to a target _Val_ type within the current scope. The standard scope contains predefined names that are available before any user code is evaluated.
***
### Val

A _Val_ (value) is the fundamental unit of data in Vird, characterized by:

1. Type (represented by a TypeRef)
2. Payload (the actual data content)
3. A set of associated functions (making each Val potentially polymorphic, function set can contain zero or more specific functions, distinguished by their signatures a.k.a. function types)

Values can represent various entities including:
- Raw syntax expressions (Expr)
- Functions
- Types or TypeRefs
- Other language constructs

Once a name is bound to a Val in a scope, it cannot be redefined within that same scope.
***
### Scope

A scope is a lexical environment that maps names to Val references. Each Val possesses its own scope of local defined names.

**Name Resolution Process:**
1. When resolving a name, the current scope is searched first
2. If the name is not found, the search continues in the parent scope
3. This process repeats until either the name is found or the root scope is reached

Vird provides two primary binding functions for associating names with values:
- `(: <Type> <Name> <Val>)` - Binds a name with explicit type checking
- `(:: <Name> <Val>)` - Binds a name with automatic type inference

### Type System

The standard expression handler (_evaluate_) predefines several fundamental types:
* `Expr` - Represents a syntax expression from source code

#### TypeRef

A TypeRef (type reference) is a Val that encapsulates:
1. A reference to a Type
2. Constraints that apply to values of that type

### Functions

A Function is a Val containing exactly one function in its function set (itself). Each function has a signature that specifies:
1. The types of parameters it accepts
2. The type of value it returns

This signature serves as the function's Val type.
Parameter names must be unique, arguments themselves are defined in function scope by given values before function body is evaluated.

**Function Creation:**
Functions are created by calling a function-producing function. The standard function factory is `fn`, with the syntax:
```
(fn [<ParamNames...>] <Body>)
```

The type of the function (parameter types and return type) is determined by the expected type in the context where it's defined.

### Templates

Templates are a metaprogramming mechanism that can generate types, functions, or other templates at compile time. Unlike functions, templates:
- Can accept any kind of parameter (values, types, other templates, etc.)
- Do not have a predetermined return type
- Are evaluated during compilation rather than at runtime

**Template Declaration:**

Templates are created by calling a template-producing function. The standard template factory is `template`, with the syntax:
```
(template [<TemplateParamNames...>] <Body>)
```

**Template Invocation:**
Templates can be explicitly invoked using square brackets:
```
(TemplateName [<Args...>])
```

A distinctive feature of templates is automatic argument inference. When template arguments can be deduced from context, explicit invocation becomes optional. In such cases, using just the template name is sufficient, and it will be treated as the entity (function, type, etc.) that the template generates.

**Examples:**

```vird
// Define a template that creates a function type
(: (tmt [TypeRef]) ListOf (template [T] (fnt [T T] T)))

// Explicit template usage with Blob4 type
(: (ListOf [Blob4]) addNumbers (fn [a b] (+ a b)))

// Implicit template usage where type is inferred
(: ListOf addStrings (fn [a b] (concat a b)))
```

In the second example, the template argument is inferred to be String based on the context (the function body using string concatenation).

Templates support composition and nesting for advanced metaprogramming:

```vird
(: (tmt [TypeRef TypeRef]) Pair (template [T U] (fnt [T U] {T U})))
(: (tmt [TypeRef TypeRef]) Map (template [K V] (fnt [(Pair [K V])] V)))

// Creates a function that takes a key-value pair and returns the value
(: (Map [String Blob4]) getValue (fn [pair] (second pair)))
```

### Macros
Macro is just a function that takes 0 or more Exprs as parameters and returns an Expr.

Example:

```vird
(: (fnt [Expr]Expr) baseOf (macro[x] (baseType (typeRefOf x))))
```
