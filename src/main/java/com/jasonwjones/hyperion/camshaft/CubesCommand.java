package com.jasonwjones.hyperion.camshaft;

import java.util.ArrayList;
import java.util.List;

import com.appliedolap.essbase.EssApplication;
import com.appliedolap.essbase.EssCube;
import com.appliedolap.essbase.EssServer;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * Lists the cubes, in one application or all of them.
 */
@Command(name = "cubes", mixinStandardHelpOptions = true, description = "Lists the cubes in an application, or in all of them.")
public class CubesCommand extends ListingCommand {

	@Parameters(index = "0", arity = "0..1", paramLabel = "APP", description = "The application; all of them if not given")
	String application;

	@Override
	Table list(EssServer server) {
		List<EssApplication> applications = application == null ? server.getApplications() : List.of(server.getApplication(application));
		List<Object[]> rows = new ArrayList<>();
		for (EssApplication app : applications) {
			for (EssCube cube : app.getCubes()) {
				rows.add(new Object[] { app.getName(), cube.getName() });
			}
		}
		return table(List.of(text("APPLICATION"), text("CUBE")), rows);
	}

}
