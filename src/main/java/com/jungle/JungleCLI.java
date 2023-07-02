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
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JungleCLI {

    @Nullable
    private static Logger logger = null;

    @NotNull
    public static Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(JungleCLI.class.getSimpleName());
            logger.setLevel(Level.INFO);
            logger.setUseParentHandlers(false); // disable parent console logging
            try {
                logger.addHandler(new FileHandler("jungle.log"));
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return logger;
    }

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
            getLogger().log(Level.SEVERE, "failed to scan", e);
            System.exit(1);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    getLogger().log(Level.SEVERE, "failed to close token writer", e);
                    System.exit(1);
                }
            }
            try {
                reader.close();
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "failed to close source reader", e);
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
            getLogger().log(Level.SEVERE, "failed to save ast", e);
            System.exit(1);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    getLogger().log(Level.SEVERE, "failed to close ast writer", e);
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
            getLogger().log(Level.SEVERE, "failed to load ast", e);
            System.exit(1);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "failed to close ast reader", e);
                System.exit(1);
            }
        }
        String outputFileName = cli.getOptionValue("output");
        Compiler compiler = new Compiler();
        try {
            compiler.compile(outputFileName, new MainVisitor(), ast);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "failed to compile", e);
            System.exit(1);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        Options options = new Options();
        options.addOption("h", "help", false, "Show help options.");
        options.addOption("o", "output", true, "Output file name.");

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
        if (args.length == 0) {
            System.err.println("Missing command-line argument");
            helpCommand(options);
            System.exit(1);
        }
        String command = args[0];
        switch (command) {
            case "scan": scanCommand(cli); break;
            case "parse": parseCommand(cli); break;
            case "compile": {
                String output = cli.getOptionValue("output");
                boolean isEmpty = output == null || output.equals("");
                if (isEmpty) {
                    // Note: output is only required for compile
                    System.err.println("Missing required option - output");
                    helpCommand(options);
                    System.exit(1);
                } else {
                    compileCommand(cli);
                }
            } break;
            default: {
                System.err.println("Unknown command-line argument - " + command);
                helpCommand(options);
                System.exit(1);
            } break;
        }
    }
}
