package com.saxifrages.hyperion.camshaft.mdxtest;

import com.essbase.api.base.EssException;
import com.essbase.api.dataquery.IEssMdAxis;
import com.essbase.api.dataquery.IEssMdCluster;
import com.essbase.api.dataquery.IEssMdMember;

public class AxisPropertyWrapper implements IEssMdAxis {

	@Override
	public IEssMdCluster[] getAllClusters() throws EssException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEssMdMember[] getAllDimensions() throws EssException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEssMdMember[] getAllTupleMembers(int arg0) throws EssException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getClusterCount() throws EssException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDimensionCount() throws EssException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTupleCount() throws EssException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isSlicerAxis() throws EssException {
		// TODO Auto-generated method stub
		return false;
	}

}
