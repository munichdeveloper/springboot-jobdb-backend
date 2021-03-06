package de.jgh.spring.jobdb.backend.jobdbbackend.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduledJobResult {
	private String jobType;
	private int importCnt;
	private Set<Long> identifiers = new HashSet<>();
	private boolean logResult = true;

	public int incrementAndGetImportCnt(int incrementBy) {
		this.importCnt += incrementBy;
		return this.importCnt;
	}

	public void addIdentifiers(Set<Long> additionalIdentifiers) {
		this.identifiers.addAll(additionalIdentifiers);
	}
}
