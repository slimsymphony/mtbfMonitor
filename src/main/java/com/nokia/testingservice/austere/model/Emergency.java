package com.nokia.testingservice.austere.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.nokia.testingservice.austere.util.CommonUtils;

/**
 * Emergency PoJo.
 * 
 * @author Frank Wang
 * @since Jun 12, 2012
 */
public class Emergency extends AustereModel<Emergency> {
	public static enum EmergencyType {
		ScheduleEmergency, DatabaseEmergency, ServiceEmergency, OtherEmergency;
		public static EmergencyType parse( String str ) {
			for ( EmergencyType type : EmergencyType.values() ) {
				if ( type.name().equals( str ) )
					return type;
			}
			return OtherEmergency;
		}
	}

	public static enum EmergencyLevel {
		Info, Warning, Error, Fatal;
		public static EmergencyLevel parse( String str ) {
			for ( EmergencyLevel level : EmergencyLevel.values() ) {
				if ( level.name().equals( str ) )
					return level;
			}
			return Info;
		}
	}

	private EmergencyType type;
	private EmergencyLevel level;
	private String source;
	private String detail;
	private Date emergencyTime;
	private List<String> stakeholders = new ArrayList<String>();

	public EmergencyType getType() {
		return type;
	}

	public void setType( EmergencyType type ) {
		this.type = type;
	}

	public EmergencyLevel getLevel() {
		return level;
	}

	public void setLevel( EmergencyLevel level ) {
		this.level = level;
	}

	public String getSource() {
		return source;
	}

	public void setSource( String source ) {
		this.source = source;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail( String detail ) {
		this.detail = detail;
	}

	public Date getEmergencyTime() {
		return emergencyTime;
	}

	public void setEmergencyTime( Date emergencyTime ) {
		this.emergencyTime = emergencyTime;
	}

	public List<String> getStakeholders() {
		return stakeholders;
	}

	public void setStakeholders( List<String> stakeholders ) {
		this.stakeholders = stakeholders;
	}

	public void addStakeholder( String holder ) {
		for ( String sh : stakeholders ) {
			if ( sh.equalsIgnoreCase( holder ) )
				return;
		}
		this.stakeholders.add( holder );
	}

	public void addStakeholders( Collection<String> stakeholder ) {
		for ( String nsh : stakeholder ) {
			addStakeholder( nsh );
		}
	}
	
	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
