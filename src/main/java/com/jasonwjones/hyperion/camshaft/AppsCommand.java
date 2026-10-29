package com.jasonwjones.hyperion.camshaft;

import java.util.ArrayList;
import java.util.List;

import com.appliedolap.essbase.EssApplication;
import com.appliedolap.essbase.EssServer;

import picocli.CommandLine.Command;

/**
 * Lists the applications.
 */
@Command(name = "apps", mixinStandardHelpOptions = true, description = "Lists the applications on the server.")
public class AppsCommand extends ListingCommand {

	@Override
	Table list(EssServer server) {
		List<Object[]> rows = new ArrayList<>();
		for (EssApplication application : server.getApplications()) {
			rows.add(new Object[] { application.getName() });
		}
		return table(List.of(text("APPLICATION")), rows);
	}

}
