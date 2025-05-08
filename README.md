[//]: # ([![SVG Banners]&#40;https://svg-banners.vercel.app/api?type=luminance&text1=&#40;Vird&#41;&#41;]&#40;https://github.com/Akshay090/svg-banners&#41;)

# Vird Programming Language

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)

[//]: # (![C]&#40;https://img.shields.io/badge/C-A8B9CC?style=flat&logo=c&logoColor=black&#41;)

Research project, programming language aimed at
finding maximum flexibility (both syntactically and semantically)
with the simplest possible language model
So its development basically is
"do multiple complex things, then spend weeks simplifying them until
it starts to seem obvious ~~what took you so long~~"

The language syntax is pretty similar to Lisp (but it's not its dialect)

It also is the safest scripting language because it does not have runtime errors at all and all the code passes
validation before running
~~(surely you can add them in user code but why would you do that)~~

***
Pre-releases are not backwards compatible (unlike releases)
***

Floors of abstraction (contingently, just notes for me)

| Floor             | Stable | Constructions                                                 |
|-------------------|--------|---------------------------------------------------------------|
| Syntax (.base)    | +      | `Symbol` \| `ExprList(() \| {} \| [])`                        |
| Core (.lang)      | ~      | `Val` \| `Constraint` (All the syntax constructions are Vals) |
| Functional (.lib) | -      | Pre-defined extern functions                                  |
