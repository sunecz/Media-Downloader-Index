package sune.app.mediadown.index.sitemap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import sune.app.mediadown.index.Shared;

public final class SitemapUrlIterator
		implements Iterator<SitemapUrlEntry>, AutoCloseable {
	
	private final XMLEventReader reader;
	private SitemapUrlEntry next;
	private SitemapUrlEntry.Builder builder = new SitemapUrlEntry.Builder();
	private String lastName;
	private boolean inUrl;
	
	public SitemapUrlIterator(InputStream in) throws IOException {
		XMLInputFactory f = XMLInputFactory.newFactory();
		f.setProperty(XMLInputFactory.IS_COALESCING, true);
		f.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		
		XMLEventReader r;
		try {
			r = f.createXMLEventReader(in, Shared.CHARSET.name());
		} catch(XMLStreamException ex) {
			throw new IOException(ex);
		}
		
		this.reader = r;
		advance();
	}
	
	private void advance() {
		next = null;
		
		try {
			while(reader.hasNext()) {
				XMLEvent event = reader.nextEvent();
				
				switch(event.getEventType()) {
					case XMLStreamConstants.START_ELEMENT: {
						String name = event.asStartElement().getName().getLocalPart();
						lastName = name;
						
						if("url".equals(name)) {
							inUrl = true;
							builder.reset();
						}
						
						break;
					}
					case XMLStreamConstants.CHARACTERS: {
						if(inUrl && !event.asCharacters().isWhiteSpace()) {
							String text = event.asCharacters().getData().trim();
							
							switch(lastName) {
								case "loc": builder.setLoc(text);
								case "lastmod": builder.setLastmod(text);
							}
						}
						
						break;
					}
					case XMLStreamConstants.END_ELEMENT: {
						String name = event.asEndElement().getName().getLocalPart();
						lastName = null;
						
						if("url".equals(name)) {
							inUrl = false;
							next = builder.build();
							return; // Entry is ready
						}
						
						break;
					}
				}
			}
			
			reader.close();
		} catch(XMLStreamException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public boolean hasNext() {
		return next != null;
	}
	
	@Override
	public SitemapUrlEntry next() {
		if(next == null) {
			throw new NoSuchElementException();
		}
		
		SitemapUrlEntry out = next;
		advance();
		return out;
	}
	
	@Override
	public void close() throws Exception {
		reader.close();
	}
}
