package com.jasonwjones.hyperion.camshaft;

import java.util.ArrayList;
import java.util.List;

import com.appliedolap.essbase.EssDimension;
import com.appliedolap.essbase.EssServer;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * Lists a cube's dimensions.
 */
@Command(name = "dims", mixinStandardHelpOptions = true, description = "Lists a cube's dimensions, with whether each is dense or sparse and how many members it has.")
public class DimensionsCommand extends ListingCommand {

	@Parameters(index = "0", paramLabel = "APP.CUBE", description = "The cube, such as Sample.Basic")
	String cube;

	@Override
	Table list(EssServer server) throws CamshaftException {
		CubeRef ref = CubeRef.parse(cube);
		List<Object[]> rows = new ArrayList<>();
		for (EssDimension dimension : server.getApplication(ref.application()).getCube(ref.cube()).getDimensions()) {
			EssDimension.StorageType storage = dimension.getStorageType();
			rows.add(new Object[] { dimension.getName(), storage == null ? null : storage.name().toLowerCase(),
					dimension.getMemberCount(), dimension.getStoredMemberCount() });
		}
		return table(List.of(text("DIMENSION"), text("STORAGE"), number("MEMBERS"), number("STORED")), rows);
	}

}
