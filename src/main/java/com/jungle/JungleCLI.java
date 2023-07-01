package com.jungle;

import com.jungle.ast.INode;
import com.jungle.ast.Node;
import com.jungle.compiler.Compiler;
import com.jungle.parser.Parser;
import com.jungle.scanner.Scanner;
import com.jungle.token.IToken;
import com.jungle.token.Token;
import com.jungle.walker.*;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.List;

public class JungleCLI {

    protected static BufferedReader getStandardInputBufferedReader() {
        // Read from standard input
        return new BufferedReader(new InputStreamReader(System.in));
    }

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

    protected static void helpCommand(@NotNull Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("jungle (scan|parse|compile) OPTIONS", options);
    }

    protected static void scanCommand(@NotNull CommandLine cli) {
        BufferedReader reader = getStandardInputBufferedReader();
        BufferedWriter writer = null;
        try {
            writer = getBufferedWriter(cli);
            Scanner.tokenize(reader, writer, new Scanner());
        } catch (IOException e) {
            System.err.println("failed to scan - " + e.getMessage());
            System.exit(1);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println("failed to close writer - " + e.getMessage());
                    System.exit(1);
                }
            }
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println("failed to close reader - " + e.getMessage());
                System.exit(1);
            }
        }
    }

    protected static void parseCommand(@NotNull CommandLine cli) {
        BufferedReader reader = getStandardInputBufferedReader();
        List<IToken> tokenList = Token.load(reader);
        Parser parser = new Parser(tokenList.iterator());
        INode ast = parser.parse();
        BufferedWriter writer = null;
        try {
            writer = getBufferedWriter(cli);
            Node.save(writer, ast);
        } catch (IOException e) {
            System.err.println("failed to save ast - " + e.getMessage());
            System.exit(1);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println("failed to close writer - " + e.getMessage());
                    System.exit(1);
                }
            }
        }
    }

    protected static void compileCommand(@NotNull CommandLine cli) {
        BufferedReader reader = getStandardInputBufferedReader();
        INode ast = null;
        try {
            ast = Node.load(reader);
        } catch (IOException e) {
            System.err.println("failed to load ast - " + e.getMessage());
            System.exit(1);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println("failed to close reader - " + e.getMessage());
                System.exit(1);
            }
        }
        String outputFileName = cli.getOptionValue("output");
        Compiler compiler = new Compiler();
        try {
            compiler.compile(outputFileName, new MainVisitor(), ast);
        } catch (IOException e) {
            System.err.println("failed to compile - " + e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        Options options = new Options();
        options.addOption("h", "help", false, "Show help options.");
        options.addRequiredOption("o", "output", true, "Output file name.");

        CommandLineParser cliParser = new DefaultParser();
        CommandLine cli = null;
        try {
            cli = cliParser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("cli parsing failed - " + e.getMessage());
            System.exit(1);
        }
        if (cli.hasOption("help")) {
            helpCommand(options);
            return;
        }
        String command = args[0];
        switch (command) {
            case "scan": scanCommand(cli); break;
            case "parse": parseCommand(cli); break;
            case "compile": compileCommand(cli); break;
            default: {
                System.err.println("unknown command");
                System.exit(1);
            } break;
        }
    }
}
