package com.saxifrages.hyperion.camshaft;

import com.essbase.api.base.EssException;
import com.essbase.api.dataquery.IEssCubeView;
import com.essbase.api.dataquery.IEssMdDataSet;
import com.essbase.api.dataquery.IEssOpMdxQuery;
import com.essbase.api.datasource.IEssCube;
import com.saxifrages.essbase.EssCubeTemplate;
import com.saxifrages.essbase.primitives.CubeTarget;
import com.saxifrages.hyperion.camshaft.mdxtest.AdvancedDataSetPrinter;
import com.saxifrages.hyperion.camshaft.mdxtest.AdvancedPrinterConfiguration;

public class MdxExecutor extends EssCubeTemplate {
	
	private String queryText;
	
	private AdvancedPrinterConfiguration configuration;
	
	protected MdxExecutor(CubeTarget cubeTarget) throws EssException {
		super(cubeTarget);
	}

	@Override
	public void doCubeOperation(IEssCube cube) throws EssException {
		IEssCubeView cubeView = cube.openCubeView("Camshaft MDX Query");
		
		//cubeView.setAliasNames(true);
		//cubeView.setAliasTable("Long Names");
		//cubeView.updatePropertyValues();
		
		IEssOpMdxQuery query = cubeView.createIEssOpMdxQuery();
		query.setQuerySpec(queryText);
		cubeView.performOperation(query);

		IEssMdDataSet md = cubeView.getMdDataSet();
				
		TextDataSetDelegate delegate = new TextDataSetDelegate();
				
		AdvancedDataSetPrinter p = new AdvancedDataSetPrinter();
		p.print(md, configuration, delegate);
		cubeView.close();
	}

}
