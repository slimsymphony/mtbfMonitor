package com.nokia.testingservice.austere.model;

public class IowCard extends AustereModel<IowCard> {

	public IowCard( String id, String type, String subType ) {
		this.id = id;
		this.type = type;
		this.subType = subType;
	}

	public IowCard( String content ) {
		// 100041CD - UniSwitch_01
		if ( content.indexOf( "-" ) > 0 ) {
			content = content.trim();
			this.id = content.substring( 0, content.indexOf( "-" ) ).trim();
			String typeS = content.trim().substring( content.indexOf( "-" ) + 1 ).trim();
			if ( typeS.indexOf( "_" ) > 0 ) {
				this.type = typeS.substring( 0, typeS.indexOf( "_" ) ).trim();
				this.subType = typeS.substring( typeS.indexOf( "_" ) + 1 ).trim();
			} else {
				this.type = typeS.trim();
			}
		}
	}

	private String id;
	private String type;
	private String subType;

	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType( String type ) {
		this.type = type;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType( String subType ) {
		this.subType = subType;
	}

	public boolean equals( IowCard obj ) {
		if ( obj == null )
			return false;

		if ( ( this.id != null && obj.getId() == null ) || ( this.type != null && obj.getType() == null ) || ( this.subType != null && obj.getSubType() == null )
				|| ( this.id == null && obj.getId() != null ) || ( this.type == null && obj.getType() != null ) || ( this.subType == null && obj.getSubType() != null ) )
			return false;

		if ( !this.id.equalsIgnoreCase( obj.getId() ) || !this.type.equalsIgnoreCase( obj.getType() ) || !this.id.equalsIgnoreCase( obj.getSubType() ) )
			return false;

		return true;
	}
}
