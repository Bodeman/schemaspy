package org.schemaspy.cli;

import com.beust.jcommander.JCommander;
import org.schemaspy.Config;
import org.schemaspy.SchemaSpyConfiguration;
import org.schemaspy.util.DbSpecificConfig;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class uses {@link JCommander} to parse the SchemaSpy command line arguments represented by {@link CommandLineArguments}.
 */
public class CommandLineArgumentParser {

    private static final Logger LOGGER = Logger.getLogger(CommandLineArgumentParser.class.getName());

    private final JCommander jCommander;

    private final PropertyFileDefaultProvider defaultProvider;

    public CommandLineArgumentParser(PropertyFileDefaultProvider defaultProvider) {
        this.defaultProvider = defaultProvider;
        jCommander = createJCommander();
    }

    public CommandLineArguments parse(String... localArgs) {
        CommandLineArguments arguments = new CommandLineArguments();
        jCommander.addObject(arguments);

        jCommander.parse(localArgs);
        return arguments;
    }

    private JCommander createJCommander() {
        return JCommander.newBuilder()
                .acceptUnknownOptions(true)
                .programName("java -jar " + Config.getLoadedFromJar())
                .columnSize(120)
                .defaultProvider(defaultProvider)
                .build();
    }

    /**
     * Prints documentation about the usage of command line arguments to the console.
     * <p>
     */
    //TODO consider extracting dump generation to other class
    public void printUsage() {
        StringBuilder builder = new StringBuilder();

        jCommander.usage(builder);

        builder.append(System.lineSeparator());
        builder.append("Go to http://schemaspy.org for a complete list/description of additional parameters.");
        builder.append(System.lineSeparator());
        builder.append(System.lineSeparator());
        builder.append("Sample usage using the default database type (implied -t ora):");
        builder.append(System.lineSeparator());
        builder.append(System.lineSeparator());
        builder.append(" java -jar schemaSpy.jar -db mydb -s myschema -u devuser -p password -o output");
        builder.append(System.lineSeparator());
        builder.append(System.lineSeparator());

        LOGGER.log(Level.INFO, builder.toString());
    }

    /**
     * Prints information of supported database types to the console.
     * <p>
     */
    public void printDatabaseTypesHelp() {
        String schemaspyJarFileName = Config.getLoadedFromJar();

        LOGGER.log(Level.INFO,"Built-in database types and their required connection parameters:");
        for (String type : Config.getBuiltInDatabaseTypes(schemaspyJarFileName)) {
            new DbSpecificConfig(type).dumpUsage();
        }
        LOGGER.log(Level.INFO,"You can use your own database types by specifying the filespec of a .properties file with -t.");
        LOGGER.log(Level.INFO,"Grab one out of " + schemaspyJarFileName + " and modify it to suit your needs.");
    }
}
