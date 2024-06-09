package com.jungle;

import com.jungle.ast.INode;
import com.jungle.ast.Node;
import com.jungle.compiler.Compiler;
import com.jungle.parser.Parser;
import com.jungle.scanner.Scanner;
import com.jungle.token.IToken;
import com.jungle.token.Token;
import com.jungle.compiler.visitor.*;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Iterator;
import java.util.List;

public class JungleCLI {
    // NOTE:
    // Standard out (stdout) is used to print a string file format if and only if the command runs successfully
    // For this reason, no stdout is used for debug, info, warn, etc.

    @NotNull
    protected static BufferedReader getStandardInputBufferedReader() {
        // Read from standard input
        return new BufferedReader(new InputStreamReader(System.in));
    }

    @NotNull
    protected static BufferedWriter getBufferedWriter(@NotNull CommandLine cli) throws IOException {
        String outputFileName = cli.getOptionValue("output");
        if (outputFileName == null || outputFileName.equals("-")) {
            // Write to standard output
            return new BufferedWriter(new OutputStreamWriter(System.out));
        } else {
            // Write to file
            return new BufferedWriter(new FileWriter(outputFileName));
        }
    }

    public static String DEFAULT_JUNGLEPATH = ".";

    @NotNull
    protected static String getJunglePath(@NotNull CommandLine cli) {
        /* The junglepath should be independent of classpath,
         * since the dependencies of the compiler should be isolated from the program
         */
        String cliValue = cli.getOptionValue("junglepath");
        if (cliValue != null) return cliValue;
        String environmentValue = System.getenv("JUNGLEPATH");
        if (environmentValue != null) return environmentValue;
        return DEFAULT_JUNGLEPATH;
    }

    protected static void helpCommand(@NotNull Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("jungle (scan|parse|compile|run) OPTIONS", options);
    }

    protected static void scanCommand(@NotNull CommandLine cli) {
        BufferedReader reader = getStandardInputBufferedReader();
        Iterator<String> lineIterator = reader.lines().iterator();
        Scanner scanner = new Scanner(lineIterator);
        Iterable<IToken> tokenList = scanner.scan();
        BufferedWriter writer = null;
        try {
            writer = getBufferedWriter(cli);
            Token.save(writer, tokenList);
        } catch (IOException e) {
            System.err.println("failed to scan - " + e.getMessage());
            System.exit(1);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println("failed to close token writer - " + e.getMessage());
                    System.exit(1);
                }
            }
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println("failed to close source reader - " + e.getMessage());
                System.exit(1);
            }
        }
    }

    protected static void parseCommand(@NotNull CommandLine cli) {
        BufferedReader reader = getStandardInputBufferedReader();
        List<IToken> tokenList = Token.load(reader);
        Parser parser = new Parser(tokenList);
        INode ast = parser.parse();
        BufferedWriter writer = null;
        try {
            writer = getBufferedWriter(cli);
            Node.save(writer, ast);
        } catch (IOException e) {
            System.err.println("failed to parse - " + e.getMessage());
            System.exit(1);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println("failed to close ast writer - " + e.getMessage());
                    System.exit(1);
                }
            }
        }
    }

    protected static void compileCommand(@NotNull CommandLine cli) {
        BufferedReader reader = getStandardInputBufferedReader();
        INode ast = Node.load(reader);
        try {
            reader.close();
        } catch (IOException e) {
            System.err.println("failed to close ast reader - " + e.getMessage());
            System.exit(1);
        }
        String outputFileName = cli.getOptionValue("output", "Entrypoint");
        String junglePath = getJunglePath(cli);
        Compiler compiler = new Compiler();
        compiler.compileMain(outputFileName, new MainVisitor(junglePath), ast);
    }

    protected static void runCommand(@NotNull CommandLine cli) {
        BufferedReader reader = getStandardInputBufferedReader();
        // Scan...
        Iterator<String> lineIterator = reader.lines().iterator();
        Scanner scanner = new Scanner(lineIterator);
        Iterable<IToken> tokenList = scanner.scan();
        // Parse...
        Parser parser = new Parser(tokenList);
        INode ast = parser.parse();
        // Compile...
        String entrypointClassName = cli.getOptionValue("output", "Entrypoint");
        String junglePath = getJunglePath(cli);
        Compiler compiler = new Compiler();
        compiler.compileMain(entrypointClassName, new MainVisitor(junglePath), ast);
        // Run...
        /* Problem:
         * Loading the new class using reflection and invoking main appears to work in some cases.
         * However, when a custom classpath is provided then URLClassLoader::loadClass() fails to find the classes that are in the classpath.
         * This happens even when the classpath appears to be valid and passed available to the running code.
         * 
         * Solution:
         * Execute the java program in a new process using exec.
         */
        
        String[] command = {
            "java",
            "-classpath", getJunglePath(cli),
            entrypointClassName
        };
        Process process;
        ProcessBuilder processBuilder = new ProcessBuilder(command).inheritIO();
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            int exitCode = process.waitFor();
            System.exit(exitCode);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("h", "help", false, "Show help options.");
        options.addOption("k", "keywords", false, "Show keywords.");
        options.addOption("o", "output", true, "Output file name.");
        options.addOption("p", "junglepath", true, "Class path of the jungle program.");

        CommandLineParser cliParser = new DefaultParser();
        CommandLine cli = null;
        try {
            cli = cliParser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Failed to parse command-line arguments - " + e.getMessage());
            helpCommand(options);
            System.exit(1);
        }
        if (cli.hasOption("help")) {
            helpCommand(options);
            System.exit(0);
        }
        if (cli.hasOption("keywords")) {
            System.out.println(String.join(" ", Scanner.KEYWORDS));
            System.exit(0);
        }
        if (args.length == 0) {
            System.err.println("Missing command-line argument");
            helpCommand(options);
            System.exit(1);
        }
        String command = args[0];
        switch (command) {
            case "scan": scanCommand(cli); break;
            case "parse": parseCommand(cli); break;
            case "compile": compileCommand(cli); break;
            case "run": runCommand(cli); break;
            default: {
                System.err.println("Unknown command-line argument - " + command);
                helpCommand(options);
                System.exit(1);
            } break;
        }
    }
}
