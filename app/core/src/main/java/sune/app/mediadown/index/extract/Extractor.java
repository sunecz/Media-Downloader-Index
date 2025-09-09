package sune.app.mediadown.index.extract;

import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import sune.app.mediadown.index.Website;

@Component
public class Extractor {

	private static final Logger logger = LoggerFactory.getLogger(Extractor.class);

	private final Deque<ExtractNode> nodes = new ConcurrentLinkedDeque<>();
	private final ExtractionContext context;

	public Extractor(ExtractionContext context) {
		this.context = context;
	}

	protected void loop() {
		for(ExtractNode node;
				!Thread.currentThread().isInterrupted()
					&& (node = nodes.pollFirst()) != null;) {
			extract(node);
		}
	}

	protected void extract(ExtractNode node) {
		try {
			node.extract(this, context);
		} catch(Exception ex) {
			logger.error("", ex);
		}
	}

    public void extract(List<Website> websites) throws Exception {
		Objects.requireNonNull(websites);
		websites.stream().map(WebsiteExtractNode::new).forEachOrdered(this::addNode);
		loop();
    }

	public void prependNode(ExtractNode node) {
		nodes.addFirst(Objects.requireNonNull(node));
	}

	public void addNode(ExtractNode node) {
		nodes.addLast(Objects.requireNonNull(node));
	}
}