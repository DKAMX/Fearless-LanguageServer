# Fearless Language Server

A language server for the Fearless programming language. The server leverages LSP4J and the Fearless compiler API to provide code completion, diagnostics and syntax highlighting. Particularly, use added `base.Id.id` method in the standard library to help infer expression type.

## How to use the language server

Build the jar file: `mvn package`.  
There are two ways to communicate with the language server:

1. Run at local and communicate through the standard input/output stream. This is usually start by the extension/client side.
2. Run at remote (or as localhost) and communicate through the TCP socket.

To change communication method, go to [FearlessLauncher](src/main/java/io/github/dkamx/fearless/FearlessLauncher.java) and change the launch method invoked in the main method. Run with standard input/output would be `launch(System.in, System.out)`. Remember to change the corresponding IDE extension to accept preferred communication method.

## The use of Fearless API

In general, code completion breaks into two parts: static information and dynamic information. Static information refers to information that can be directly retrieved from an AST (e.g., type/method declaration). Dynamic information specifically refers to expressions or local variables inside lambda that don't have type information yet. To work around this, we add a special type into `lang.fear` of the Fearless standard library:
```
Id: {
    mut .id[P:iso,mut,imm,mutH,readH,read, 
            R:iso,mut,imm,mutH,readH,read](x: P): R -> this.id(x),
    }
```
This method can help compiler to do type inference against method parameter (target expression we want to resolve).