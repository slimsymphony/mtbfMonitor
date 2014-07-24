package com.nokia.testingservice.austere.model;

public enum Milestone {
	PD1, PD2, PD3, PD4, PR1, PR2, PE, OTHER;

	public String toString() {
		return this.name();
	}
}
