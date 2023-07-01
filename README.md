Jungle
======

A toy programming language built for the Java Virtual Machine.

```bash
# Scan from Source
cat program.source | jungle scan --output program

# Parse from Tokens
cat program.tokens | jungle parse --output program

# Compile from AST
cat program.ast | jungle compile --output Entrypoint

# Run program
java Entrypoint
```

```bash
# Example program to count down

echo '
i = 3
loop (i) {
  i = - i 1
  print(+ 1 i)
  print("...\n")
}
print("Blast off!\n")
'  | ./run.bash scan --output - \
   | ./run.bash parse --output - \
   | ./run.bash compile --output Entrypoint

java Entrypoint
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

## Ideas

- Should I store the symbol table in the entrypoint class?
    - Would it be ok if the user could interact with the symbol table?
