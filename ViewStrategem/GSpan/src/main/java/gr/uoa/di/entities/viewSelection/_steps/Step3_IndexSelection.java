package gr.uoa.di.entities.viewSelection._steps;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import gr.uoa.di.databaseConnectors.Neo4jConnector;
import gr.uoa.di.entities.dictionary.Dictionary;
import gr.uoa.di.entities.graph.NodeVS;
import gr.uoa.di.entities.graph.PatternVS;
import gr.uoa.di.entities.graph.TripleVS;
import gr.uoa.di.entities.trie.TrieMetadata;
import gr.uoa.di.entities.trie.containment.mapping.ContainmentMapping;
import gr.uoa.di.entities.viewSelection.edgeRewriting.TripleMap;
import gr.uoa.di.entities.viewSelection.hierarchy.PatternHierarchy;
import gr.uoa.di.entities.viewSelection.materialization.TrieIndexForMaterialization;
import gr.uoa.di.entities.viewSelection.queryRewriting.PatternMetadata;
import gr.uoa.di.entities.viewSelection.queryRewriting.ViewForRewriting;
import gr.uoa.di.interfaceAdapters.controllers.randomAccessDiskMap.patterns.RNDAccessPatterns;
import gr.uoa.di.translators.cypher.CypherQuery;
import gr.uoa.di.translators.cypher.CypherTranslator;
import gr.uoa.di.usecases.constants.experiments.ConstantForExperiments;
import gr.uoa.di.usecases.constants.materialization.Constants;
import gr.uoa.di.usecases.constants.materialization.ConstantsSharable;
import gr.uoa.di.viewTemplates.*;
import org.apache.jena.atlas.lib.Pair;
import org.neo4j.driver.exceptions.DatabaseException;

public class Step3_IndexSelection {

	public static <C extends ConstantsSharable & ConstantForExperiments> void selectViews(C constants,
																									  double tolerancePercentage) throws IOException {
		Dictionary dict = constants.deserializeDictionary();
		PatternHierarchy hierarchy = constants.deserializeHierarchy(dict);
		AtomicInteger count = new AtomicInteger();

		//Create a converter to convert patterns to cypher queries
		CypherTranslator translator = new CypherTranslator();

		Neo4jConnector connector = new Neo4jConnector(constants.getDatabaseMainURI(), constants.getUsernameMain(), constants.getPasswordMain());
		Neo4jConnector viewConnector = new Neo4jConnector(constants.getDatabaseViewURI(), constants.getUsernameView(), constants.getPasswordView());

		//Load all pattern metadata
		List<PatternMetadata> patternMetadata = new ArrayList<>();
		File f = new File(constants.getRoot()+File.separator+"stats.csv");
		if(f.exists() && !f.isDirectory()) {
			try {
				BufferedReader reader;
				reader = new BufferedReader(new FileReader(constants.getRoot()+File.separator+"stats.csv"));
				//skip headers
				reader.readLine();
				String line = reader.readLine();

				while (line != null) {
					PatternMetadata metadata = new PatternMetadata(line);
					metadata.initialize(hierarchy,translator,connector,viewConnector,constants.getImportDirectory());
					patternMetadata.add(metadata);
					// read next line
					line = reader.readLine();
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("\n\n\n----------------------------------------------------\n\n\n");


		hierarchy.forEachPatternContainedPatterns((pattern, containments) -> {
			count.getAndIncrement();
			//System.out.println(pattern.toFullString());
			//System.out.println(pattern.getId() + " " + translator.convert(pattern));

			PatternMetadata metadata = getPattern(patternMetadata,pattern);

			//System.out.println("-------------------------------------------");
			if(metadata!=null && count.get()>239) {
				try {
					if(metadata.getSubgraphView()!=null) {
						metadata.getSubgraphView().materialize();
						metadata.getSubgraphView().executeThroughView(pattern);
						metadata.getSubgraphView().delete();
					}
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException  | DatabaseException e) {
					e.printStackTrace();
				}

				/*try {
					if(metadata.getEdgeLabelView()!=null)
						metadata.getEdgeLabelView().executeThroughView(pattern);
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException  | DatabaseException e) {
					e.printStackTrace();
				}

				try {
					if(metadata.getNodeLabelView()!=null)
						metadata.getNodeLabelView().executeThroughView(pattern);
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException  | DatabaseException e) {
					e.printStackTrace();
				}

				try {
					if(metadata.getShortcutView()!=null)
						metadata.getShortcutView().executeThroughView(pattern);
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException  | DatabaseException e) {
					e.printStackTrace();
				}

				try {
					if(metadata.getReificationView()!=null)
						metadata.getReificationView().executeThroughView(pattern);
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException  | DatabaseException e) {
					e.printStackTrace();
				}

				try {
					if(metadata.getSubgraphView()!=null) {
						metadata.getSubgraphView().materialize();
						metadata.getSubgraphView().executeThroughView(pattern);
						metadata.getSubgraphView().delete();
					}
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException  | DatabaseException e) {
					e.printStackTrace();
				}*/
			}
		});
	}

	private static PatternMetadata getPattern(List<PatternMetadata> patterns, PatternVS patternVS){
		for(PatternMetadata patternMetadata:patterns){
			if(patternMetadata.getPatternVS()==patternVS) return patternMetadata;
		}
		return null;
	}
}
