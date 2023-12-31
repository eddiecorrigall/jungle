Jungle
======

[![Build Status](https://github.com/eddiecorrigall/jungle/actions/workflows/main.yaml/badge.svg)](https://github.com/eddiecorrigall/jungle/actions/workflows/main.yaml)

A toy programming language built for the Java Virtual Machine.

## Setup

Requires a Java Runtime Environment (JRE) with minimum version `1.8`.
Either install the OpenJDK or Oracle JRE.

```bash
# Make the command-line interface available to shell.
source jungle-setup.bash

# Test command.
jungle --help
```

## Demo

Breakdown of commands.

```bash
# Scan from standard input Source file
cat program.source | jungle scan --output program.tokens

# Parse from standard input Tokens file
cat program.tokens | jungle parse --output program.ast

# Compile from standard input AST file
cat program.ast | jungle compile --output Entrypoint

# Run program
java Entrypoint
```

### Working example

Compile and run a countdown program.

```bash
echo '
i = 3
loop (greaterThan i 0) {
  print i
  print "...\n"
  i = - i 1
}
print("Blast off!\n")
'  | jungle scan \
   | jungle parse \
   | jungle compile

# Run class file produced by compiler
java Entrypoint
```

Output of countdown program.

```
3...
2...
1...
Blast off!
```

## Useful Commands

- Decompile a `*.class` file: `javap -c MyClass.class`
- Run class file containing `main()` method: `java MyClass`

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
