package sune.app.mediadown.index.entity;

import java.time.OffsetDateTime;

public interface Changeable {

	void setIsActive(boolean value);
	void setChangedDate(OffsetDateTime dateTime);
	boolean isActive();
	OffsetDateTime getChangedDate();
}