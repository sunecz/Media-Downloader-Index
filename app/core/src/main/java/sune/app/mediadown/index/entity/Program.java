package sune.app.mediadown.index.entity;

import java.net.URI;
import java.util.Set;

public interface Program extends Entity, Sourceable {
	
	void setUri(URI uri);
	void setTitle(String title);
	void setDescription(String description);
	void setImage(URI imageUri);
	void addCountry(Country country);
	void removeCountry(Country country);
	void addActor(Person person);
	void removeActor(Person person);
	void addDirector(Person person);
	void removeDirector(Person person);
	void addGenre(Genre genre);
	void removeGenre(Genre genre);

	URI getUri();
	String getTitle();
	String getDescription();
	URI getImage();
	Set<Country> getCountries();
	Set<Person> getActors();
	Set<Person> getDirectors();
	Set<Genre> getGenres();

	@Override
	default URI getIdentifier() {
		return getUri();
	}
}