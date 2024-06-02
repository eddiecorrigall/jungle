Jungle
======

[![Build Status](https://github.com/eddiecorrigall/jungle/actions/workflows/main.yaml/badge.svg)](https://github.com/eddiecorrigall/jungle/actions/workflows/main.yaml)

A toy programming language built for the Java Virtual Machine.

## Features

Most features are inherited from JVM.

- Automatic garbage collection
- Concurrency
- Objects

This project is for educational purposes (but mostly to have fun). There are a couple of goals for this language.

- JVM programming accessibility for command-line
- fully transparent compiling process
- simplified Java language feature
    - no classes
    - no `null`

## Setup

A Java Runtime Environment (JRE) is required with minimum version `1.8`.
Either install the OpenJDK or Oracle JRE.

```bash
# Make the command-line interface available to shell.
source jungle-setup.bash

# Test command.
jungle --help
```

## Demo

Our standard [Hello, World!](https://en.wikipedia.org/wiki/%22Hello,_World!%22_program) program.

```shell
echo 'print "Hello, World!"' | jungle run
```

Expected output:
```
Hello, World!
```

### Count-down

Now a slightly more sophisticated program using a loop and variables to wow our reader.
This program performs a count-down.

```shell
echo '
    i = 3
    loop (greaterThan i 0) {
        print i
        print "...\n"
        i = - i 1
    }
    print("Blast off!\n")
' | jungle run
```

Expected output:
```
3...
2...
1...
Blast off!
```

### Inspection Friendly

Let's breakdown some commands in a short demo to help you understand their purpose.
The advantage here is that all inputs and outputs of these stages are inspection friendly.
For each stage of the compiler, you can provide input and inspect output for the
1. Stage 1: [scanner](https://en.wikipedia.org/wiki/Lexical_analysis#Scanner)
    - input is source code
    - output is tokens
2. Stage 2: [parser](https://en.wikipedia.org/wiki/Lexical_analysis#Lexer_generator)
    - input is tokens
    - output is [abstract syntax tree (AST)](https://en.wikipedia.org/wiki/Abstract_syntax_tree)
3. Stage 3: [compiler]()
    - input is AST
    - output is [Java bytecode](https://en.wikipedia.org/wiki/Java_bytecode) - a class file

```bash
# Scan from standard input Source file
cat programs/hello-world.source | jungle scan

# Parse from standard input Tokens file
cat programs/hello-world.tokens | jungle parse

# Compile from standard input AST file
cat programs/hello-world.ast | jungle compile --output HelloWorld

# Run program
java HelloWorld
```

### Multitasking

Ok, let's get this party started.

Jungle supports Java [threads](https://en.wikipedia.org/wiki/Thread_(computing)) using the keyword `multitask`.
To use this keyword, supply a `*.class` file/binary path (excluding extension).

Ensure the class for `multitask`:
- has a default constructor
- implements the `java.lang.Runnable` interface
- compiled with the `JUNGLEPATH` environment variable with the classpath

```shell
# Create a folder for out program example.
mkdir -p /tmp/demo/com/example

# Write a Runnable Java class
echo '
package com.example;

public class MultitaskRunnable implements Runnable {
    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(", world!");
    }
}
' > /tmp/demo/com/example/MultitaskRunnable.java

# Compile the Java class
javac /tmp/demo/com/example/MultitaskRunnable.java
```

```shell
# Declare the classpath before compiling,
# so that our Runnable class can be validated
export JUNGLEPATH=".:/tmp/demo"

# Compile and run the program
echo '
  multitask "com.example.MultitaskRunnable"
  print "Hello"
' | jungle scan \
  | jungle parse \
  | jungle compile --output Demo

java -classpath $JUNGLEPATH Demo
```

Expected output:
```
Hello, world!
```

## Compile the Compiler

1. Load project using VS Code
1. Select Terminal > Run Build Task...
1. Select JungleCLI

## Useful Commands

- Decompile a `*.class` file
    ```shell
    javap -c MyClass.class
    ```
- Run class file containing `main()` method
    ```shell
    java MyClass
    ```

## Useful resources

- https://blogs.oracle.com/javamagazine/post/real-world-bytecode-handling-with-asm

## Modifying Class Files

There are 2 common ways to generate class files.

**Option 1**: using a custom `VisitorClass`

- Read in the class file using a `ClassReader`
- Walk through the ASM representation of the class, using a custom `ClassVisitor`
- Write the Java bytecode back out as a byte[], using a `ClassWriter`
- Save the bytecode as a transformed class file

**Option 2**: in-order declaration

Generate bytecode sequentially by calling "visit*()" methods in one big call stack.

```java
void addMainMethod() {
    MethodVisitor mv = 
        cw.visitMethod(ACC_PUBLIC + ACC_STATIC, 
            "main", "([Ljava/lang/String;)V", null, null);
    mv.visitCode();
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", 
        "out", "Ljava/io/PrintStream;");
    mv.visitLdcInsn("Hello World!");
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", 
        "println", "(Ljava/lang/String;)V", false);
    mv.visitInsn(RETURN);
    mv.visitMaxs(3, 3);
    mv.visitEnd();
}
```

## Java Project

- IntelliJ
    - [Package into JAR](https://www.jetbrains.com/help/idea/compiling-applications.html#package_into_jar)
    - [Run Packaged JAR](https://www.jetbrains.com/help/idea/compiling-applications.html#run_packaged_jar)

## Ideas

- Should I store the symbol table in the entrypoint class?
    - Would it be ok if the user could interact with the symbol table?
