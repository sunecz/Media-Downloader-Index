package sune.app.mediadown.index.extract;

import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

    public void process() {
		loop(); // Run the loop in-place in the same thread
    }

	public void addNode(ExtractNode node) {
		nodes.addLast(Objects.requireNonNull(node));
	}

	public ExtractionContext context() {
		return context;
	}
}