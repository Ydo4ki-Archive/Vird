This project currently has too many changes so it lives in multiple private repositories, but the development is still active (10/5/2025).

[//]: # ([![SVG Banners]&#40;https://svg-banners.vercel.app/api?type=luminance&text1=&#40;Vird&#41;&#41;]&#40;https://github.com/Akshay090/svg-banners&#41;)

# Vird Programming Language

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)

[//]: # (![C]&#40;https://img.shields.io/badge/C-A8B9CC?style=flat&logo=c&logoColor=black&#41;)

Research project, programming language aimed at
finding maximum flexibility (both syntactically and semantically)
with the simplest possible language model<br>
So its development basically is
"do multiple complex things, then spend weeks simplifying them until
it starts to seem obvious ~~what took you so long~~"

The pure language syntax is pretty similar to Lisp (but it's not its dialect)

It also is the safest scripting language because it does not have runtime errors at all and all the code passes
validation before running
~~(surely you can add them in user code but why would you do that)~~

***
Pre-releases are not backwards compatible (unlike releases)
***

Floors of abstraction (contingently, just notes for me)

| Floor              | Stable | Constructions                                                               | Drawbacks                                                      |
|--------------------|--------|-----------------------------------------------------------------------------|----------------------------------------------------------------|
| Structural (.base) | +      | `Symbol` \| `ExprList(BracketType)`                                         | Ignorance of whitespaces amount (not important)                |
| Core (.lang)       | ~      | `Val` \| `Constraint` (All the syntax constructions are Vals)               | `Contraint` might be `Val`; `Scope` is probably too high-level |
| Functional (.lib)  | -      | Raw invocation syntax, pre-defined extern functions                         |                                                                |
| Syntactic          |        | More convenient syntax (fixes code ugliness from the pure functional floor) |                                                                |

Function call:

```
(sum 3 5)
  |
  +-> Symbol(sum) -> resolve to extern function
  +-> literals 3, 5 -> Symbol(3), Symbol(5)
  |
  +-> Constraint on result: EqualityConstraint(Blob(8))
```
