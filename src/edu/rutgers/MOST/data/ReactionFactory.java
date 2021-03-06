package edu.rutgers.MOST.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

public class ReactionFactory {
	private String sourceType;
	private String databaseName;
	private String columnName;
	
	public ReactionFactory(String sourceType, String databaseName) {
		this.sourceType = sourceType;
		this.databaseName = databaseName;
	}

	public ModelReaction getReactionById(Integer reactionId){
		if("SBML".equals(sourceType)){
			SBMLReaction reaction = new SBMLReaction();
			reaction.setDatabaseName(databaseName);
			reaction.loadById(reactionId);
			return reaction;
		}
		return new SBMLReaction(); //Default behavior.
	}

	// TODO : check if method is actually used. Probably replaced with MetabolitesUsedMap 
	public int reactantUsedCount(Integer id) {
		int count = 0;
		String queryString = "jdbc:sqlite:" + databaseName + ".db"; 
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(queryString);
			PreparedStatement prep = conn.prepareStatement("select count(metabolite_id) from reaction_reactants where metabolite_id=?;");
			prep.setInt(1, id);
			ResultSet rs = prep.executeQuery();
			count = rs.getInt("count(metabolite_id)");					
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return count;
	}

	// TODO : check if method is actually used. Probably replaced with MetabolitesUsedMap 
	public int productUsedCount(Integer id) {
		int count = 0;
		String queryString = "jdbc:sqlite:" + databaseName + ".db"; 
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(queryString);
			PreparedStatement prep = conn.prepareStatement("select count(metabolite_id) from reaction_products where metabolite_id=?;");
			prep.setInt(1, id);
			ResultSet rs = prep.executeQuery();
			count = rs.getInt("count(metabolite_id)");						
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return count;
	}

	public Vector<ModelReaction> getAllReactions() {
		Vector<ModelReaction> reactions = new Vector<ModelReaction>();

		if("SBML".equals(sourceType)){
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return reactions;
			}
			Connection conn;
			try {
				conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db"); // TODO:
				PreparedStatement prep = conn
				.prepareStatement("select id, knockout, reaction_abbreviation, reaction_name, "
						+ " reaction_equn_abbr, reaction_equn_names, reversible, biological_objective, "
						+ " lower_bound, upper_bound, flux_value, gene_associations from reactions;");
				conn.setAutoCommit(true);
				ResultSet rs = prep.executeQuery();
				while (rs.next()) {
					SBMLReaction reaction = new SBMLReaction();
					reaction.setId(rs.getInt("id"));
					reaction.setKnockout(rs.getString("knockout"));
					reaction.setFluxValue(rs.getDouble("flux_value"));
					reaction.setReactionAbbreviation(rs.getString("reaction_abbreviation"));
					reaction.setReactionName(rs.getString("reaction_name"));
					reaction.setReactionEqunAbbr(rs.getString("reaction_equn_abbr"));
					reaction.setReactionEqunNames(rs.getString("reaction_equn_names"));
					reaction.setReversible(rs.getString("reversible"));
					reaction.setBiologicalObjective(rs.getDouble("biological_objective"));
					reaction.setLowerBound(rs.getDouble("lower_bound"));
					reaction.setUpperBound(rs.getDouble("upper_bound"));
					reaction.setGeneAssociations(rs.getString("gene_associations"));
					
					reactions.add(reaction);
				}
				rs.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return reactions;
			}
		}

		return reactions;
	}
	/**
	 * @param args
	 */

	public Vector<Double> getObjective() {
		Vector<Double> objective = new Vector<Double>();

		if("SBML".equals(sourceType)){
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return objective;
			}
			Connection conn;
			try {
				conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db"); 

				Statement stat = conn.createStatement();
				ResultSet rs = stat.executeQuery("select biological_objective from reactions where length(reaction_abbreviation) > 0;");

				while (rs.next()) {
					objective.add(rs.getDouble("biological_objective")); 
				}
				rs.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return objective;
			}
		}

		return objective;
	}
	
	public void setFluxes(ArrayList<Double> fluxes) {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db"); 

			Statement stat = conn.createStatement();
			String query = "update reactions set flux_value = case id";
			for (int i = 0; i < fluxes.size(); i++) {
				query = query + " when " + (i + 1) + " then " + fluxes.get(i);
			}
			query = query + " end";
			stat.executeUpdate(query);
			
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public ArrayList<String> setKnockouts(List<Double> knockouts) {
		ArrayList<String> knockoutGenes = new ArrayList<String>();
		
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return knockoutGenes;
		}
		Connection conn;
		try {
			ArrayList<Double> kVector = new ArrayList<Double>();
			
			Vector<String> uniqueGeneAssociations = getUniqueGeneAssociations();
			Vector<String> geneAssocaitons = getGeneAssociations();
		
			String queryKVector = "";
			for (int i = 0; i < geneAssocaitons.size(); i++) {
				for (int j = 0; j < uniqueGeneAssociations.size(); j++) {
					if (geneAssocaitons.elementAt(i).equals(uniqueGeneAssociations.elementAt(j))) {
							kVector.add(knockouts.get(j).doubleValue());
					}
					
					if (knockouts.get(j).doubleValue() != 0.0) {
						knockoutGenes.add(uniqueGeneAssociations.elementAt(j));
					}
				}
				
				if(kVector.get(i).doubleValue() != 0.0) {
					queryKVector += " when " + (i + 1) + " then " + "\"" + GraphicalInterfaceConstants.BOOLEAN_VALUES[1] + "\"";
				} else {
					queryKVector += " when " + (i + 1) + " then " + "\"" + GraphicalInterfaceConstants.BOOLEAN_VALUES[0] + "\"";
				}
			}
			
			conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
			
			if (queryKVector.length() != 0) {
				Statement stat = conn.createStatement();
				String query = "update reactions set knockout = case id" + queryKVector + " end";
				stat.executeUpdate(query);
			}
			
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return knockoutGenes;
		}		
		
		return knockoutGenes;
	}
	
	/**
	 * @param args
	 */

	public Vector<String> getGeneAssociations() {
		Vector<String> geneAssociations = new Vector<String>();

		if("SBML".equals(sourceType)){
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return geneAssociations;
			}
			Connection conn;
			try {
				conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db"); 

				Statement stat = conn.createStatement();
				ResultSet rs = stat.executeQuery("select gene_associations from reactions where length(reaction_abbreviation) > 0;");

				while (rs.next()) {
					geneAssociations.add(rs.getString("gene_associations")); 
				}
				rs.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return geneAssociations;
			}
		}

		return geneAssociations;
	}
	
	public Vector<String> getUniqueGeneAssociations() {
		Vector<String> geneAssociations = new Vector<String>();

		if("SBML".equals(sourceType)){
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return geneAssociations;
			}
			Connection conn;
			try {
				conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db"); 

				Statement stat = conn.createStatement();
				ResultSet rs = stat.executeQuery("select distinct gene_associations from reactions where length(reaction_abbreviation) > 0;");

				System.out.println(rs.getRow());
				
				while (rs.next()) {
					geneAssociations.add(rs.getString("gene_associations")); 
				}
				rs.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return geneAssociations;
			}
		}

		return geneAssociations;
	}
	
	public Vector<Double> getSyntheticObjectiveVector() {
		Vector<Double> syntheticObjectiveVector = new Vector<Double>();

		if("SBML".equals(sourceType)){
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return syntheticObjectiveVector;
			}
			Connection conn;
			try {
				conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db"); 

				Statement stat = conn.createStatement();
//				ResultSet rs = stat.executeQuery("select synthetic_objective from reactions;");
				//System.out.println("ReactionFactory, columnName = " + columnName);
				ResultSet rs = stat.executeQuery("select " + columnName + " from reactions;");

				while (rs.next()) {
//					syntheticObjectiveVector.add(rs.getDouble("synthetic_objective"));
					syntheticObjectiveVector.add(rs.getDouble(columnName));
				}
				rs.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return syntheticObjectiveVector;
			}
		}

		return syntheticObjectiveVector;
	}
	
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public static void main(String[] args) {

	}

}
