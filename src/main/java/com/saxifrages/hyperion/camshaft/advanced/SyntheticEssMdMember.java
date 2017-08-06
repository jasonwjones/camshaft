package com.saxifrages.hyperion.camshaft.advanced;

import com.essbase.api.base.EssException;
import com.essbase.api.base.IEssIterator;
import com.essbase.api.base.IEssValueAny;
import com.essbase.api.base.IEssValueAny.EEssDataType;
import com.essbase.api.dataquery.IEssMdMember;

public class SyntheticEssMdMember implements IEssMdMember {

	private String name;
	
	public SyntheticEssMdMember(String name) {
		this.name = name;
	}

	@Override
	public int getCountProperties() throws EssException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public EEssDataType getPropertyDataType(int arg0) throws EssException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EEssDataType getPropertyDataType(String arg0) throws EssException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EEssDataType[] getPropertyDataTypes() throws EssException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPropertyId(String arg0) throws EssException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public EEssPropertyMode getPropertyMode(int arg0) throws EssException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EEssPropertyMode getPropertyMode(String arg0) throws EssException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EEssPropertyMode[] getPropertyModes() throws EssException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPropertyName(int arg0) throws EssException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getPropertyNames() throws EssException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEssValueAny getPropertyValueAny(String arg0) throws EssException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEssValueAny getPropertyValueAny(int arg0) throws EssException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refreshPropertyValues() throws EssException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPropertyValueAny(String arg0, IEssValueAny arg1) throws EssException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPropertyValueAny(int arg0, IEssValueAny arg1) throws EssException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updatePropertyValues() throws EssException {
		// TODO Auto-generated method stub

	}

	@Override
	public IEssMdMember getDimension() throws EssException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEssIterator getLinkedObjects() throws EssException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() throws EssException {
		return name;
	}

	@Override
	public boolean isCalced() throws EssException {
		return false;
	}

	@Override
	public boolean isDimension() throws EssException {
		return false;
	}

	@Override
	public boolean isDrillThrough() throws EssException {
		return false;
	}

}
