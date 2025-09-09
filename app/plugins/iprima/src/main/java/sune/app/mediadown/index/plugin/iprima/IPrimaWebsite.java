package sune.app.mediadown.index.plugin.iprima;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sune.app.mediadown.index.Website;
import sune.app.mediadown.index.entity.Program;
import sune.app.mediadown.index.extract.ExtractionContext;
import sune.app.mediadown.index.extract.Extractor;
import sune.app.mediadown.index.extract.ProgramExtractNode;
import sune.app.mediadown.index.net.Web.Request;
import sune.app.mediadown.index.normalization.Normalizer;
import sune.app.mediadown.index.task.ListTask;
import sune.app.mediadown.index.util.Common;
import sune.app.mediadown.index.util.DefaultLinkedDataHydrator;
import sune.app.mediadown.index.util.LinkedDataHydrator;

public final class IPrimaWebsite implements Website {
	
	private final LinkedDataHydrator hydrator;
	private final IPrima[] webs;
	
	// Allow to create an instance when registering the engine
	public IPrimaWebsite(Common common, Normalizer normalizer) {
		this.hydrator = new IPrimaLinkedDataHydrator(common, normalizer);
		this.webs = new IPrima[] {
			new PrimaPlus(this),
		};
	}
	
	@Override
	public ListTask<ProgramExtractNode> getPrograms() throws Exception {
		return ListTask.of((task) -> {
			for(IPrima web : webs) {
				ListTask<Program> webTask = web.getPrograms();
				webTask.forwardAdd(task, IPrimaProgramExtractNode::new);
				webTask.startAndWait();
			}
		});
	}
	
	@Override
	public String name() {
		return "iprima";
	}

	@Override
	public String title() {
		return "iPrima";
	}

	static interface IPrima {
		
		ListTask<Program> getPrograms() throws Exception;
	}
	
	private static final class IPrimaLinkedDataHydrator extends DefaultLinkedDataHydrator {
		
		public IPrimaLinkedDataHydrator(Common common, Normalizer normalizer) {
			super(common, normalizer);
		}
		
		@Override
		protected Request createRequest(URI uri) {
			return Requests.of(uri).GET();
		}
	}
	
	private final class IPrimaProgramExtractNode extends ProgramExtractNode {
		
		private static final Logger logger = LoggerFactory.getLogger(IPrimaProgramExtractNode.class);
		
		public IPrimaProgramExtractNode(Program program) {
			super(program);
		}
		
		@Override
		public void extract(Extractor extractor, ExtractionContext context) throws Exception {
			logger.info("Processing {}: {}", program.getClass().getSimpleName(), program.getUri());
			Program hydrated = hydrator.hydrate(program);
			
			if(hydrated != null) {
				context.doUpdate(hydrated);
			}
		}
	}
}