Jungle
======

[![Build Status](https://github.com/eddiecorrigall/jungle/actions/workflows/main.yaml/badge.svg)](https://github.com/eddiecorrigall/jungle/actions/workflows/main.yaml)

A toy programming language built for the Java Virtual Machine.

## TODO

- casting: integer, float, character
- sleep

## Features

Most features are inherited from JVM.

- Automatic garbage collection
- Concurrency
- Objects
- Multi-platform
- [JIT](https://en.wikipedia.org/wiki/Just-in-time_compilation)

This project is for educational purposes (but mostly to have fun). There are a couple of goals for this language.

- JVM programming accessibility for command-line
- Fully transparent compiling process
- Simplified Java language feature
    - no object oriented programming
    - no `null`

## Setup

A Java Runtime Environment (JRE) is required with minimum version `1.8`.
Either install the OpenJDK or Oracle JRE.

```bash
# Make the command-line interface available to shell.
source setup.bash

# Test command.
jungle --help
```

## Demo

Our standard [Hello, World!](https://en.wikipedia.org/wiki/%22Hello,_World!%22_program) program.

```shell
echo 'print "Hello, World!\n"' | jungle run
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
For each stage of the compiler, you can provide input and inspect output:
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

### Example Programs

Want to get a sense of the language syntax and its capabilities? Ok, then checkout the [programs folder](./programs/).

Lets generate an ASCII visualization called a [Mandelbrot set](https://en.wikipedia.org/wiki/Mandelbrot_set).

```shell
cat programs/mandelbrot.source | jungle run
```

Expected output:
```
1111111111111111111111122222222222222222222222222222222222222222222222222222222222222222222222222211111
1111111111111111111122222222222222222222222222222222222222222222222222222222222222222222222222222222211
1111111111111111112222222222222222222222222222222222222222222222222222222222222222222222222222222222222
1111111111111111222222222222222222233333333333333333333333222222222222222222222222222222222222222222222
1111111111111112222222222222333333333333333333333333333333333333222222222222222222222222222222222222222
1111111111111222222222233333333333333333333333344444456655544443333332222222222222222222222222222222222
1111111111112222222233333333333333333333333444444445567@@6665444444333333222222222222222222222222222222
11111111111222222333333333333333333333334444444445555679@@@@7654444443333333222222222222222222222222222
1111111112222223333333333333333333333444444444455556789@@@@98755544444433333332222222222222222222222222
1111111122223333333333333333333333344444444445556668@@@    @@@76555544444333333322222222222222222222222
1111111222233333333333333333333344444444455566667778@@      @987666555544433333333222222222222222222222
111111122333333333333333333333444444455556@@@@@99@@@@@@    @@@@@@877779@5443333333322222222222222222222
1111112233333333333333333334444455555556679@   @@@               @@@@@@ 8544333333333222222222222222222
1111122333333333333333334445555555556666789@@@                        @86554433333333322222222222222222
1111123333333333333444456666555556666778@@ @                         @@87655443333333332222222222222222
111123333333344444455568@887789@8777788@@@                            @@@@65444333333332222222222222222
111133334444444455555668@@@@@@@@@@@@99@@@                              @@765444333333333222222222222222
111133444444445555556778@@@         @@@@                                @855444333333333222222222222222
11124444444455555668@99@@             @                                 @655444433333333322222222222222
11134555556666677789@@                                                @86655444433333333322222222222222
111                                                                 @@876555444433333333322222222222222
11134555556666677789@@                                                @86655444433333333322222222222222
11124444444455555668@99@@             @                                 @655444433333333322222222222222
111133444444445555556778@@@         @@@@                                @855444333333333222222222222222
111133334444444455555668@@@@@@@@@@@@99@@@                              @@765444333333333222222222222222
111123333333344444455568@887789@8777788@@@                            @@@@65444333333332222222222222222
1111123333333333333444456666555556666778@@ @                         @@87655443333333332222222222222222
1111122333333333333333334445555555556666789@@@                        @86554433333333322222222222222222
1111112233333333333333333334444455555556679@   @@@               @@@@@@ 8544333333333222222222222222222
111111122333333333333333333333444444455556@@@@@99@@@@@@    @@@@@@877779@5443333333322222222222222222222
1111111222233333333333333333333344444444455566667778@@      @987666555544433333333222222222222222222222
1111111122223333333333333333333333344444444445556668@@@    @@@76555544444333333322222222222222222222222
1111111112222223333333333333333333333444444444455556789@@@@98755544444433333332222222222222222222222222
11111111111222222333333333333333333333334444444445555679@@@@7654444443333333222222222222222222222222222
1111111111112222222233333333333333333333333444444445567@@6665444444333333222222222222222222222222222222
1111111111111222222222233333333333333333333333344444456655544443333332222222222222222222222222222222222
1111111111111112222222222222333333333333333333333333333333333333222222222222222222222222222222222222222
1111111111111111222222222222222222233333333333333333333333222222222222222222222222222222222222222222222
1111111111111111112222222222222222222222222222222222222222222222222222222222222222222222222222222222222
1111111111111111111122222222222222222222222222222222222222222222222222222222222222222222222222222222211
```

### Multitasking

Ok, let's get this party started.

Jungle supports Java [threads](https://en.wikipedia.org/wiki/Thread_(computing)) using the keyword `multitask`.
To use this keyword, supply a `*.class` file/binary path (excluding extension).

Ensure the class for `multitask`:
- has a default constructor
- implements the `java.lang.Runnable` interface
- compiled with the `JUNGLE_CLASSPATH` environment variable with the classpath

---

This program demonstrates: 
- Multi-threading is possible with this language
- Threads print to the same standard output
- Runnable classes are compiled from a single program

The program describes 2 threads:
1. The first thread pauses program execution for a short duration and then prints out `thread!`
2. The second thread immediately prints out `Inline...`

```shell
cat programs/multitask-inline.source | jungle run
```

Expected output:
```
Inline... thread!
```

## Environment Variables

| Name                | Default | For Compile | For Runtime | Description |
|---------------------|---------|-------------|-------------|---|
| `JUNGLE_CLASSPATH`  | `.`     | yes         | yes         | Similar to the JVM `CLASSPATH`. Used to specify class dependencies required for compiling and runtime of the program. |
| `JUNGLE_TARGETPATH` | `.`     | yes         | no          | Used to specify a target output folder for classes generated when compiling. |

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
