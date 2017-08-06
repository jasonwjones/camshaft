package com.saxifrages.hyperion.camshaft.mdxtest;

import com.essbase.api.base.EssException;
import com.essbase.api.dataquery.IEssMdMember;

public class MdxEssMember implements MdxMember {

	private IEssMdMember member;
	
	public MdxEssMember(IEssMdMember member) {
		this.member = member;
	}
	
	@Override
	public String getName() {
		try {
			return member.getName();
		} catch (EssException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isDimension() {
		try {
			return member.isDimension();
		} catch (EssException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isCalculated() {
		try {
			return member.isCalced();
		} catch (EssException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isSynthetic() {
		return false;
	}

	public static MdxEssMember[] from(IEssMdMember[] members) {
		MdxEssMember[] mdxMembers = new MdxEssMember[members.length];
		for (int index = 0; index < members.length; index++) {
			mdxMembers[index] = new MdxEssMember(members[index]);
		}
		return mdxMembers;
	}
	
}
