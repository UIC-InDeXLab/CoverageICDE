package cli;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Cli {
	private static final Logger log = Logger.getLogger(Cli.class.getName());
	private String[] args = null;
	private Options options = new Options();
	private CommandLine cmd;

	public static final String CMD_HELP_SHORT = "h";
	public static final String CMD_FILE_SHORT = "f";
	public static final String CMD_NUM_RECORDS_SHORT = "n";
	public static final String CMD_NUM_DIMENSIONS_SHORT = "d";
	public static final String CMD_ALGORITHM_SHORT = "a";
	public static final String CMD_THRESHOLD_SHORT = "t";
	public static final String CMD_OUTPUT_SHORT = "o";
	public static final String CMD_LEVEL_SHORT = "l";

	public static final String CMD_HELP_LONG = "help";
	public static final String CMD_FILE_LONG = "file";
	public static final String CMD_NUM_RECORDS_LONG = "number";
	public static final String CMD_NUM_DIMENSIONS_LONG = "dimension";
	public static final String CMD_ALGORITHM_LONG = "algorithm";
	public static final String CMD_THRESHOLD_LONG = "threshold";
	public static final String CMD_OUTPUT_LONG = "output";

	public static final String CMD_HELP_MSG = "show help.";
	public static final String CMD_FILE_MSG = "specify the file name.";
	public static final String CMD_NUM_RECORDS_MSG = "specify the size of the data.";
	public static final String CMD_NUM_DIMENSIONS_MSG = "specify number of variables we account.";
	public static final String CMD_ALGORITHM_MSG = "specify the search algorithm.";
	public static final String CMD_THRESHOLD_MSG = "specify the threshold";
	public static final String CMD_OUTPUT_MSG = "specify the output file";
	
	public static final String[] commandTypes = new String[]{CMD_ALGORITHM_SHORT,CMD_NUM_RECORDS_SHORT,CMD_NUM_DIMENSIONS_SHORT,CMD_THRESHOLD_SHORT};

	public Cli(String[] args) {

		this.args = args;

		options.addOption(CMD_HELP_SHORT, CMD_HELP_LONG, false, CMD_HELP_MSG);
		options.addOption(CMD_FILE_SHORT, CMD_FILE_LONG, true, CMD_FILE_MSG);
		options.addOption(CMD_NUM_RECORDS_SHORT, CMD_NUM_RECORDS_LONG, true,
				CMD_NUM_RECORDS_MSG);
		options.addOption(CMD_NUM_DIMENSIONS_SHORT, CMD_NUM_DIMENSIONS_LONG,
				true, CMD_NUM_DIMENSIONS_MSG);
		options.addOption(CMD_ALGORITHM_SHORT, CMD_ALGORITHM_LONG, true,
				CMD_ALGORITHM_MSG);
		options.addOption(CMD_THRESHOLD_SHORT, CMD_THRESHOLD_LONG, true,
				CMD_THRESHOLD_MSG);
		options.addOption(CMD_OUTPUT_SHORT, CMD_OUTPUT_LONG, false,
				CMD_OUTPUT_MSG);

		parse();
	}

	public void parse() {
		CommandLineParser parser = new BasicParser();

		try {
			cmd = parser.parse(options, args);

			if (cmd.hasOption(CMD_HELP_SHORT))
				help();

			if (cmd.hasOption(CMD_FILE_SHORT)) {
				log.log(Level.INFO, "Using cli argument -f="
						+ cmd.getOptionValue(CMD_FILE_SHORT));

			} else {
				log.log(Level.SEVERE, "Missing f option");
				help();
			}

			if (cmd.hasOption(CMD_NUM_RECORDS_SHORT)) {
				log.log(Level.INFO, "Using cli argument -n="
						+ cmd.getOptionValue(CMD_NUM_RECORDS_SHORT));
			}

			if (cmd.hasOption(CMD_NUM_DIMENSIONS_SHORT)) {
				log.log(Level.INFO, "Using cli argument -d="
						+ cmd.getOptionValue(CMD_NUM_DIMENSIONS_SHORT));
			} 

			if (cmd.hasOption(CMD_ALGORITHM_SHORT)) {
				log.log(Level.INFO, "Using cli argument -a="
						+ cmd.getOptionValue(CMD_ALGORITHM_SHORT));

			}

			if (cmd.hasOption(CMD_THRESHOLD_SHORT)) {
				log.log(Level.INFO, "Using cli argument -t="
						+ cmd.getOptionValue(CMD_THRESHOLD_SHORT));

			}

		} catch (ParseException e) {
			log.log(Level.SEVERE, "Failed to parse comand line properties", e);
			help();
			System.exit(0);
		}
	}

	public String getArgument(String argName) {
		if (cmd.hasOption(argName))
			return cmd.getOptionValue(argName);
		return null;
	}
	
	public boolean checkArgument(String argName) {
		return cmd.hasOption(argName);
	}

	public Map<String, String> getArguments() {
		Map<String, String> commandlineInfo = new HashMap<String, String>();

		if (cmd.hasOption(CMD_FILE_SHORT)) {
			commandlineInfo.put(CMD_FILE_SHORT,
					cmd.getOptionValue(CMD_FILE_SHORT));
		}

		if (cmd.hasOption(CMD_NUM_RECORDS_SHORT)) {
			commandlineInfo.put(CMD_NUM_RECORDS_SHORT,
					cmd.getOptionValue(CMD_NUM_RECORDS_SHORT));
		}

		if (cmd.hasOption(CMD_NUM_DIMENSIONS_SHORT)) {
			commandlineInfo.put(CMD_NUM_DIMENSIONS_SHORT,
					cmd.getOptionValue(CMD_NUM_DIMENSIONS_SHORT));
		}

		if (cmd.hasOption(CMD_ALGORITHM_SHORT)) {
			commandlineInfo.put(CMD_ALGORITHM_SHORT,
					cmd.getOptionValue(CMD_ALGORITHM_SHORT));

		}

		if (cmd.hasOption(CMD_THRESHOLD_SHORT)) {
			commandlineInfo.put(CMD_THRESHOLD_SHORT,
					cmd.getOptionValue(CMD_THRESHOLD_SHORT));
		}

		return commandlineInfo;
	}

	private void help() {
		// This prints out some help
		HelpFormatter formater = new HelpFormatter();

		formater.printHelp("Main", options);
		System.exit(0);
	}
}