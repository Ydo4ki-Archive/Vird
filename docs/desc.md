# Vird Programming Language

<sup>Language description v1.0-prerelease-1</sup>

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
      * [Function Polymorphism and Resolution](#function-polymorphism-and-resolution)
    * [Templates](#templates)
    * [Macros](#macros)
    * [Compilation](#compilation)
  * [Constraints](#constraints)
    * [Core Concepts](#core-concepts)
    * [Creating Constrained Types](#creating-constrained-types)
    * [Constraint Propagation](#constraint-propagation)
    * [Function Arguments and Constraints](#function-arguments-and-constraints)
    * [Constraint Inference with Function Calls](#constraint-inference-with-function-calls)
    * [Complex Example: Multiple Constraints](#complex-example-multiple-constraints)
    * [Constraints and Polymorphism](#constraints-and-polymorphism)
    * [Practical Applications](#practical-applications)
  * [Standard library functions description](#standard-library-functions-description)
    * [fn](#fn)
    * [fnt](#fnt)
  * [Commandments](#commandments)
<!-- TOC -->

## Introduction

Vird is a programming language designed to combine the power of homoiconicity (code-as-data) with static typing and advanced metaprogramming capabilities.
It serves as both an embedded interpretable language and a compilable language that can generate optimized C code.

### Key Features

* **Simple yet Powerful Syntax**: The entire language is built upon just two fundamental constructs - Symbols and ExprLists
* **First-class AST Access**: Direct manipulation of Abstract Syntax Tree components during program execution
* **Unrestricted Naming**: No reserved keywords or special characters, providing maximum flexibility in identifier naming
* **Advanced Type System**: Strong static typing with sophisticated constraint mechanisms
* **Comprehensive Implicit Conversion System**: Intelligent type conversion including symbol resolution and numeric literal handling
* **Seamless Metaprogramming**: Templates and macros that work at compile-time to generate efficient code

## Syntax

At its core, a Vird program represents a data structure of type _Expr_ (Expression).
This structure is processed by an expression handler function - a function that takes an _Expr_ as input and produces any valid Vird value (_Val_) as output.

The _Expr_ type has two variants:
1. _Symbol_: Represents identifiers, literals, and operators
2. _ExprList_: Represents ordered collections of expressions

### Symbol

A _Symbol_ is the atomic unit of Vird syntax, defined by its textual content. Think of symbols as the "words" of the language.

**Delimiters**:
Symbols are separated by:
- Whitespace characters (spaces, newlines, tabs)
- Brackets (`()`, `[]`, `{}`)
- Special separator characters:
  * Comma (`,`)
  * Semicolon (`;`)
  
These separators always create distinct symbols regardless of surrounding context.

**Quoted Symbols**:
To create symbols containing whitespace, delimiters, or special characters:
- Use single quotes (`'...'`) or double quotes (`"..."`)
- Escape quotes within quoted text using `\` (e.g., `\'` or `\"`)

**Key Properties**:
- Case-sensitive (`foo` ≠ `Foo` ≠ `FOO`)
- No length restrictions
- Unicode support
- No reserved words or special meanings

**Examples with Explanation**:

| Source Code              | Interpretation                              | Explanation                                                |
|--------------------------|---------------------------------------------|------------------------------------------------------------|
| `Foo 24, i++51 f/*;4a`   | `Foo`, `24`, `,`, `i++51`, `f/*`, `;`, `4a` | Each space and special character creates a symbol boundary |
| `"Bar \' aa(5^\" \\ \""` | Single symbol: `Bar \' aa(5^" \ "`          | Quotes capture everything as one symbol, escapes preserved |
| `'Multi\nline'`          | Single symbol: `Multi\nline`                | `\n` is preserved literally, not converted to newline      |

### ExprList

An _ExprList_ serves as a container for ordered collections of expressions (both Symbols and other ExprLists). Think of ExprLists as the "sentences" and "paragraphs" of the language.

**Bracket Types**:
Vird distinguishes three types of ExprLists based on their enclosing brackets:
1. _ExprList()_: Parentheses `()` - typically used for function calls and grouping
2. _ExprList{}_ : Braces `{}` - often used for blocks and compound structures
3. _ExprList[]_ : Square brackets `[]` - commonly used for type parameters and indexing

**Key Properties**:
- Empty lists are valid (`()`, `[]`, `{}`)
- Nesting is allowed and common
- Brackets must match exactly (no mixing types)
- The semantic meaning of bracket types is determined by the expression handler

**Examples with Explanation**:

| Source Code        | Structure                         | Explanation                           |
|--------------------|-----------------------------------|---------------------------------------|
| `(1 2 3)`          | ExprList() of three symbols       | Simple flat list                      |
| `[4f {51} "foo("]` | ExprList[] containing three items | Demonstrates nesting with mixed types |
| `{[1 2] ()}`       | ExprList{} with two items         | Shows empty list as valid element     |

### Comments

Vird provides two comment styles that help document code without affecting its execution:

1. **Single-line Comments**:
   ```vird
   // This comment runs to the end of the line
   (foo bar) // Inline documentation
   ```

2. **Multi-line Comments**:
   ```vird
   /* This comment can span
      multiple lines and can contain
      any characters except */
   ```

Comments can appear anywhere whitespace is allowed and are completely ignored by the parser.

### Whitespace

Whitespace in Vird serves solely as a delimiter between Symbols. The language treats all whitespace characters (spaces, tabs, newlines) equivalently,
and multiple consecutive whitespace characters have the same effect as a single one.

This simplifies parsing while maintaining readability:
```vird
(fn [x y]    // Multiple spaces are same as one
    (+ x y)) // Indentation is for code readers
```

## Semantics

The semantic interpretation of Vird code is governed by the expression handler function, conventionally named _evaluate_. This function is the cornerstone of Vird's execution model,
determining how expressions are converted into values.

When _evaluate_ is called, it attempts to find an appropriate implicit conversion from the provided _Expr_ to a target _Val_ type
(executes this conversion within the current scope, which includes predefined standard names)

### Val

A _Val_ (Value) represents any data entity in Vird. Each Val is characterized by three fundamental aspects:

1. **Type** (represented by a TypeRef):
   - Defines the value's structure and behavior
   - Determines valid operations and conversions
   - Cannot be changed after creation

2. **Payload**:
   - The actual data content
   - Can be primitive data, functions, types, or other language constructs
   - May be mutable or immutable depending on the type

3. **Function Set**:
   - Collection of associated functions
   - Makes each Val potentially polymorphic
   - Functions are distinguished by their signatures (types of parameters and return value)
   - Can contain zero or more specific functions

Values can represent various entities including:
- Raw syntax expressions (Expr)
- Functions and templates
- Types and TypeRefs
- Data structures

**Immutability Rule**: Once a name is bound to a Val in a scope, that binding is permanent. The value itself may be mutable (if its type allows), but the name-to-value binding cannot be changed.

### Scope

A scope in Vird is a lexical environment that maps names to Val references. Think of it as a hierarchical dictionary where each level can see the entries of its parent levels.

**Scope Structure**:
- A new Scope is created for each function call or Expr evaluation
- Scopes form a hierarchy, with each scope having a single parent
- The root scope contains language primitives and standard library definitions

**Name Resolution Process**:
1. Search begins in the current scope
2. If name isn't found, search continues in the parent scope
3. Process repeats until either:
   - The name is found, or
   - The root scope is reached (resulting in an error)

**Dot Notation**:
When a name contains dot symbols (`.`), it's treated as a path through nested scopes:
```vird
math.functions.sin  // Looks for 'sin' in 'functions' scope, which is in 'math' scope
```

**Name Binding**:
Vird provides two primary functions for binding names to values:
```vird
(: <Type> <Name> <Val>)   // Explicit typing: Binds with type checking
(:: <Name> <Val>)         // Type inference: Automatically determines type
```

### Type System

Vird implements a strong static type system that is enforced at compile time. This means all type checking and verification happens before code execution.

**Core Concepts**:
1. Every Val has exactly one type
2. Types themselves are Vals
3. Types can have constraints that restrict valid values

**Standard Types**:
The standard expression handler (_evaluate_) predefines several fundamental types:
* `Expr` - Represents syntax expressions from source code
* `[Blob <N>]` - Raw memory blocks of N bytes (N ≤ 2⁶⁴-1)
* `[Mut <T>]` - Mutable container for type T
  - Not a reference type
  - Creates independent copies when passed to functions

#### TypeRef

A TypeRef (type reference) is a Val that bundles:
1. A reference to a base Type
2. Zero or more constraints on values of that type

TypeRefs can be created using the `@` function:
```vird
(@ <Type> <Constraints...>)
```
where each constraint is a function that:
- Takes one argument of the specified type
- Returns a Blob0 (empty blob, indicating success/failure)

**Type Conversion**:
TypeRefs can be implicitly converted to:
- Their referenced Type
- Other TypeRefs with compatible constraints

### Functions

A Function in Vird is a special type of Val that contains exactly one callable implementation in its function set. Each function is defined by its signature, which specifies:
1. Parameter types it accepts
2. Type of value it returns
3. Any constraints on those types

**Function Properties**:
- The signature serves as the function's type
- Parameter names must be unique within the function
- Arguments are evaluated and bound in the function's scope before the body executes

**Function Creation**:
Functions are created using the standard function factory `fn`:
```vird
(fn [<ParamNames...>] <Body>)
```

The type of the function (parameter and return types) is determined by the expected type in the context where it's defined. For example:
```vird
// Creates a function that adds two Blob4 values
(: (fnt [Blob4 Blob4] Blob4) add (fn [a b] (+ a b)))
```

#### Function Polymorphism and Resolution

Vird supports function polymorphism through function sets, allowing multiple implementations with different signatures to share the same name. The resolution process determines which implementation to use for a specific call.

**Resolution Algorithm**:
1. **Candidate Collection**: 
   - Gather all functions from the function set with the matching name
   - Search in current scope and parent scopes

2. **Type Compatibility**: 
   - Filter to functions whose parameter types are compatible with the arguments
   - Consider both exact matches and implicit conversions

3. **Constraint Evaluation**: 
   - Evaluate type constraints against the argument values
   - Eliminate functions whose constraints would be violated

4. **Specificity Ranking**: 
   - Rank remaining candidates by constraint specificity
   - More specific (restrictive) constraints take precedence
   - A constraint is "more specific" if it represents a strict subset of another constraint

**Example**:
```vird
// Handler for any positive number
(: (fnt [(@ Blob4 (fn [x] (> x 0)))] Blob4) 
   handler 
   (fn [x] (body1)))

// Handler for positive numbers less than 5
(: (fnt [(@ Blob4 (fn [x] (&& (> x 0) (< x 5))))] Blob4) 
   handler 
   (fn [x] (body2)))
```

When called with `(handler 3)`, the second implementation is selected because its constraints (`> 0 && < 5`) are more specific than just (`> 0`).

//CLARIFY: How are constraint relationships determined when they are not simple subsets?

### Templates

Templates are Vird's compile-time metaprogramming facility. Unlike functions, templates:
- Can accept any kind of parameter (values, types, templates, etc.)
- Have no predetermined return type
- Are evaluated during compilation
- Support automatic argument inference

**Template Creation**:
Templates are created using the standard template factory:
```vird
(template [<TemplateParamNames...>] <Body>)
```

**Template Invocation**:
Templates can be invoked in two ways:
1. **Explicit**: Using square brackets
   ```vird
   (TemplateName [<Args...>])
   ```

2. **Implicit**: Through type inference
   ```vird
   // Type parameters are inferred from context
   (: ListOf addStrings (fn [a b] (concat a b)))
   ```

**Examples**:
```vird
// Define a template for creating function types
(: (tmt [TypeRef]) ListOf 
   (template [T] (fnt [T T] T)))

// Use template explicitly
(: (ListOf [Blob4]) addNumbers (fn [a b] (+ a b)))

// Use template with type inference
(: ListOf addStrings (fn [a b] (concat a b)))
```

**Template Composition**:
Templates can be composed to create complex compile-time transformations:
```vird
// Template for creating pair types
(: (tmt [TypeRef TypeRef]) Pair 
   (template [T U] (fnt [T U] {T U})))

// Template for creating map accessor types
(: (tmt [TypeRef TypeRef]) Map 
   (template [K V] (fnt [(Pair [K V])] V)))

// Create a function using composed templates
(: (Map [String Blob4]) getValue (fn [pair] (second pair)))
```

### Macros

A macro is a specialized function that operates on syntax expressions. It:
- Takes zero or more Exprs as parameters
- Returns an Expr
- Executes during compilation
- Can manipulate code structure

**Example**:
```vird
// Macro that extracts the base type of an expression
(: (fnt [Expr]Expr) baseOf 
   (macro[x] (baseType (typeRefOf x))))
```

### Compilation

Vird supports compilation to C through the standard `compilec` function:
```vird
(compilec <Expr>) -> C source code
```

**Compilation Rules**:
1. Only code that cannot be evaluated at compile-time is included in the output
2. Functions requiring both compile-time and runtime features must be clearly separated
3. Mixing metaprogramming (Expr/Type manipulation) with runtime effects (I/O) inside the same context results in a compilation error


***

## Constraints

Constraints are a powerful feature of Vird's type system that enable fine-grained control over the values that can be assigned to a type.
They provide compile-time validation, enhancing both safety and optimization opportunities.

### Core Concepts

**What Are Constraints?**
- Predicates (boolean functions) attached to types
- Define valid value ranges or properties
- Verified at compile-time whenever possible
- Propagated through control flow

**Benefits of Constraints:**
- Catch errors earlier (at compile time)
- Enable more precise function overloading
- Allow compiler optimizations
- Make code self-documenting

### Creating Constrained Types

Constrained types are created using the `@` function:

```vird
// Basic syntax
(@ <BaseType> <Constraint1> <Constraint2> ...)

// Examples
(: TypeRef PositiveInt (@ Int (fn [x] (> x 0))))  // Positive integers
(: TypeRef NonEmptyString (@ String (fn [s] (> (length s) 0))))  // Non-empty strings
```

Each constraint is a function that:
1. Takes a value of the base type
2. Has no side effects (pure)
3. Returns a boolean result (true if constraint is satisfied)

> **Important**<br>
> *Constraints have nothing to do with runtime, if you have a function that consumes Int > 10 and function that consumes any Int
> and you got a random integer without performing any value checks, the second function will be invoked regardless of the actual value*

### Constraint Propagation

One of the most powerful aspects of Vird's constraint system is how constraints are automatically propagated through control flow.
When a condition is evaluated, Vird's type system analyzes the condition and applies appropriate constraints to variables in each branch.

**Example: Basic Constraint Propagation**

```vird
(: Int x 10)

(if (< x 5)
    // In this scope, x has type (@ Int (fn [n] (< n 5)))
    (println "x is less than 5")
// * * * else scope * * *
    // In this scope, x has type (@ Int (fn [n] (>= n 5)))
    (println "x is at least 5")
)
```

This propagation happens automatically without any explicit type annotations.

> **Important**<br>
> *Constraint propagation doesn't work with non-local mutable Vars, because evil other threads can change their state unexpectedly for us*

### Function Arguments and Constraints

Functions can declare constraints on their parameters, ensuring they only accept values that meet specific criteria:

```vird
// Function that only accepts positive integers
(: (fnt [(@ Int (fn [n] (> n 0)))] Int) 
   factorial 
   (fn [n] 
      (if (== n 1) 
          1 
          (* n (factorial (- n 1))))))

// This will be a compile-time error
(factorial 0)  // Constraint violation

// This will compile and run correctly
(factorial 5)  // Valid argument
```

### Constraint Inference with Function Calls

When you call a function with a condition, Vird can infer constraints for subsequent code blocks:

```vird
// Define a function that checks if a value is in range
(: (fnt [Int Int Int] Boolean) 
   inRange 
   (fn [value min max] 
      (&& (>= value min) (< value max))))

// Using the function in a condition
(: Int index (getUserInput))
(: (Vec Int) numbers {1 2 3 4 5})

(if (inRange index 0 (length numbers))
    // Here, index is automatically constrained to (@ Int (fn [i] (&& (>= i 0) (< i (length numbers)))))
    // This makes the following safe at compile time:
    (println (get numbers index))
    // Error handling
    (println "Index out of bounds")
)
```

### Complex Example: Multiple Constraints

This example demonstrates how multiple constraints can be combined and propagated:

```vird
// Define constrained types
(: TypeRef PositiveInt (@ Int (fn [n] (> n 0))))
(: TypeRef SmallInt (@ Int (fn [n] (< n 100))))

// Function requiring both constraints
(: (fnt [(@ Int (fn [n] (&& (> n 0) (< n 100))))] String)
   percentageString
   (fn [n] (concat (toString n) "%")))

// Using constraints with control flow
(: Int value (getUserInput))

(cond
    // Each branch adds different constraints
    ((< value 0) 
        (println "Negative values not allowed"))
    ((>= value 100) 
        (println "Value too large"))
    (true 
        // Here value has both constraints: positive and < 100
        (println (percentageString value)))
)
```

### Constraints and Polymorphism

Constraints play a crucial role in function overloading resolution.
When multiple function implementations match by name and basic parameter types, the one with the most specific (restrictive) constraints wins:

```vird
// General handler for any integer
(: (fnt [Int] String) 
   formatNumber 
   (fn [n] (toString n)))

// Handler for positive integers
(: (fnt [(@ Int (fn [n] (> n 0)))] String) 
   formatNumber 
   (fn [n] (concat "+" (toString n))))

// Handler for negative integers
(: (fnt [(@ Int (fn [n] (< n 0)))] String) 
   formatNumber 
   (fn [n] (toString n)))  // No need for sign, it's already included

// Usage examples
(formatNumber 42)   // Calls the positive version: "+42"
(formatNumber -13)  // Calls the negative version: "-13"
(formatNumber 0)    // Calls the general version: "0"
```

If there is two or more functions to which the argument value is suitable, and they are not comparable, this is considered as an ambiguous call (compile-time error)

### Practical Applications

Constraints are particularly useful for:

1. **Array Bounds Checking**: Ensuring indices are within valid ranges
2. **Input Validation**: Verifying user inputs meet requirements
3. **Resource Management**: Guaranteeing resources are properly initialized
4. **State Machines**: Enforcing valid state transitions
5. **Domain-Specific Rules**: Implementing business logic constraints

***
## Standard library functions description

### fn
Returns a new function
Syntax:

`fn [%ParamTypes...%][%ParamNames...%]%ReturnType% %Body%`
`fn [%ParamNames%] %Body%` - applicable only when expected function type in known
> %ParamTypes...% is a list of TypeRefs<br>
> %ParamNames...% is a list of Symbols<br>
> %Body is an Expr

### fnt
Returns a function type
Syntax:
`fnt [%ParamTypes...%]%ReturnType%`

So the following declarations are equal:
```vird
(: fnt[Blob2]Blob2 bar fn[x] x)
(:: bar fn[Blob2][x]Blob2 x)
```

Further, function signatures will be described in a similar way


## Commandments

These fundamental rules govern the entire language and cannot be violated by any part of the system:

1. **Name Uniqueness**: All defined names in scopes must be unique and cannot be redefined
   - Only value modifications through mutable types are allowed
   - Name shadowing in child scopes is permitted

2. **Type Immutability**: All defined names in scopes have a specific type that cannot be changed
   - The type association is permanent
   - The value may be mutable if its type allows

3. **Type Safety**: All operations must be type-safe and verified at compile-time
   - No implicit type conversions without explicit conversion functions
   - All template instantiations must be valid
    
//CLARIFY: Are there additional fundamental rules that should be included?
