package com.saxifrages.hyperion.camshaft;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.essbase.api.base.EssException;
import com.essbase.api.dataquery.IEssCubeView;
import com.essbase.api.dataquery.IEssMdDataSet;
import com.essbase.api.dataquery.IEssOpMdxQuery;
import com.essbase.api.datasource.IEssCube;
import com.essbase.api.datasource.IEssOlapServer;
import com.essbase.api.session.IEssbase;
import com.saxifrages.hyperion.camshaft.delegates.HtmlTableOutputDelegate;
import com.saxifrages.hyperion.camshaft.delegates.PrintStreamCamshaftOutputDelegate;

/**
 * Assumptions: axis0 is slicer, axis1 is columns, axis2 is rows
 * 
 * @author Jason W. Jones
 * 
 */
public class Camshaft {

	private static Logger logger = LoggerFactory.getLogger(Camshaft.class);

	/**
	 * Possible ways to run this:
	 * 
	 * Pull with supplied query
	 * 
	 * Supply server, username, password, app, db (needed I guess)
	 * 
	 * @param args
	 */
	
	public static void main(String[] args) throws IOException {
		PullCommand pull = new PullCommand();
		JCommander jc = new JCommander(pull);
		try {
			jc.parse(args);
		} catch (ParameterException e) {
			jc.usage();
			System.exit(1);
		}
		
		String server = pull.getServer();
		String username = pull.getUsername();
		String password = pull.getPassword();
		String app = pull.getApplication();
		String db = pull.getDatabase();
		String query = pull.getQuery();
		
		PrintOptions printOptions = new PrintOptions();
		printOptions.setLinePerHeaderTupleMember(pull.isLinePerHeader());
		printOptions.setSuppressHeader(pull.isSuppressHeader());
		printOptions.setMissingText(pull.getMissingText());
		printOptions.setIndentHeader(!pull.isNoIdentHeader());
		printOptions.setHeaderTupleSeparator(pull.getTupleSeparator());
		
		// Default is to print to console; override if file specified
		OutputStream outputStream = System.out;
		
		if (pull.getOutFile() != null) {
			try {
				outputStream = new FileOutputStream(pull.getOutFile());
			} catch (FileNotFoundException e) {
				logger.error("Unable to open file: {}", e);
			}
		} 
		
		try {
			CamshaftOutputDelegate delegate = createOutputDelegate(pull.getOutputEngine(), outputStream, pull.getParamsAsProperties());
			boolean result = Camshaft.pull(server, username, password, app, db, query, printOptions, delegate);
			System.exit(result ? 0 : 1);
		} catch (Exception e) {
			logger.error("Error generating data: {}", e.getMessage());
		} finally {
			outputStream.close();	
		}		
	}
	
	public static boolean pull(String server, String username, String password, String app, String db, String queryText, PrintOptions options, CamshaftOutputDelegate delegate) {
		IEssbase essbase = null;
		IEssOlapServer olapServer = null;
		try {
			essbase = IEssbase.Home.create(IEssbase.JAPI_VERSION);
			logger.info("Connecting to Essbase server");
			olapServer = essbase.signOn(username, password, false, null, "embedded", server);
			logger.info("Connected to server");

			IEssCube cube = olapServer.getApplication(app).getCube(db);
			IEssCubeView cubeView = cube.openCubeView("Camshaft MDX Query");
			IEssOpMdxQuery query = cubeView.createIEssOpMdxQuery();
			query.setQuerySpec(queryText);
			cubeView.performOperation(query);
			
			IEssMdDataSet md = cubeView.getMdDataSet();
			
			DataSetPrinter printer = new DataSetPrinter(options);
			printer.printMdDataSetInGridForm(md, delegate);
			
			cubeView.close();
		} catch (EssException e) {
			logger.error("Essbase error: {}", e.getMessage());
			return false;
		} finally {
			try {
				logger.info("Disconnecting from server...");
				olapServer.disconnect();
				essbase.signOff();
			} catch (EssException e) {
				logger.error("Secondary Essbase error: {}", e.getMessage());
				return false;
			}
		}
		return true;
	}
	
	public static CamshaftOutputDelegate createOutputDelegate(String specifier, OutputStream outputStream, Properties properties) {
		if (specifier.equalsIgnoreCase("html")) {
			return new HtmlTableOutputDelegate(outputStream, properties);
		}
		return new PrintStreamCamshaftOutputDelegate(outputStream, properties);
	}
	
}