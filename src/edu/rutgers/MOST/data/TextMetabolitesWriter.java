package edu.rutgers.MOST.data;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;

import au.com.bytecode.opencsv.CSVWriter;

public class TextMetabolitesWriter {

	public void write(String file, String databaseName) {
		int row = 1;
		//BufferedReader CSVFile;
		String queryString = "jdbc:sqlite:" + databaseName + ".db";
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Connection conn =
				DriverManager.getConnection(queryString);
			PreparedStatement prep = conn
			.prepareStatement("SELECT MAX(id) FROM metabolites;");
			conn.setAutoCommit(true);
			ResultSet rs1 = prep.executeQuery();
			int numMetabolites = rs1.getInt("MAX(id)");
			rs1.close();		
			//String extension = ".csv";
			CSVWriter writer;
			try {
				writer = new CSVWriter(new FileWriter(file), GraphicalInterface.getSplitCharacter());

				String headerNames = "";
				//start with 1 to avoid reading database id
				for (int i = 1; i < GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES.length; i++) {
					headerNames += GraphicalInterfaceConstants.METABOLITES_COLUMN_NAMES[i] + "#";
				}

				MetabolitesMetaColumnManager metabolitesMetaColumnManager = new MetabolitesMetaColumnManager();

				int metaColumnCount = metabolitesMetaColumnManager.getMetaColumnCount(LocalConfig.getInstance().getDatabaseName());
				for (int j = 1; j < metaColumnCount + 1; j++) {
					headerNames += metabolitesMetaColumnManager.getColumnName(LocalConfig.getInstance().getDatabaseName(), j) + "#";
				}

				String [] header = (headerNames.substring(0, headerNames.length() - 1)).split("#");

				writer.writeNext(header);
				for (int n = 0; n < numMetabolites; n++) {
					PreparedStatement prep1 = conn
					.prepareStatement("select id, metabolite_abbreviation, metabolite_name, charge, compartment, " 
							+ " boundary, meta_1, meta_2, meta_3, meta_4, meta_5, "
							+ " meta_6, meta_7, meta_8, meta_9, meta_10, "
							+ " meta_11, meta_12, meta_13, meta_14, meta_15 from metabolites where id = ?;");
					prep1.setInt(1, n + 1);
					conn.setAutoCommit(true);
					ResultSet rs = prep1.executeQuery();

					String metaboliteName = "";
					if (rs.getString("metabolite_name") == null || rs.getString("metabolite_name").length() == 0) {
						metaboliteName = " ";
					} else {
						metaboliteName = rs.getString("metabolite_name");
					}
					String charge = "";
					if (rs.getString("charge") == null || rs.getString("charge").length() == 0) {
						charge = " ";
					} else {
						charge = rs.getString("charge");
					}
					String compartment = "";
					if (rs.getString("compartment") == null || rs.getString("compartment").length() == 0) {
						compartment = " ";
					} else {
						compartment = rs.getString("compartment");
					}
					String boundary = "";
					if (rs.getString("boundary") == null || rs.getString("boundary").length() == 0) {
						boundary = " ";
					} else {
						boundary = rs.getString("boundary");
					}

					String metaString = "";
					if (metaColumnCount > 0) {
						String meta1 = "";
						if (rs.getString("meta_1") == null || rs.getString("meta_1").length() == 0) {
							meta1 = " ";
						} else {
							meta1 = rs.getString("meta_1");
						}
						metaString += meta1 + "#";
					}
					if (metaColumnCount > 1) {
						String meta2 = "";
						if (rs.getString("meta_2") == null || rs.getString("meta_2").length() == 0) {
							meta2 = " ";
						} else {
							meta2 = rs.getString("meta_2");
						}
						metaString += meta2 + "#";
					}
					if (metaColumnCount > 2) {
						String meta3 = "";
						if (rs.getString("meta_3") == null || rs.getString("meta_3").length() == 0) {
							meta3 = " ";
						} else {
							meta3 = rs.getString("meta_3");
						}
						metaString += meta3 + "#";
					}
					if (metaColumnCount > 3) {
						String meta4 = "";
						if (rs.getString("meta_4") == null || rs.getString("meta_4").length() == 0) {
							meta4 = " ";
						} else {
							meta4 = rs.getString("meta_4");
						}
						metaString += meta4 + "#";
					}
					if (metaColumnCount > 4) {
						String meta5 = "";
						if (rs.getString("meta_5") == null || rs.getString("meta_5").length() == 0) {
							meta5 = " ";
						} else {
							meta5 = rs.getString("meta_5");
						}
						metaString += meta5 + "#";
					}
					if (metaColumnCount > 5) {
						String meta6 = "";
						if (rs.getString("meta_6") == null || rs.getString("meta_6").length() == 0) {
							meta6 = " ";
						} else {
							meta6 = rs.getString("meta_6");
						}
						metaString += meta6 + "#";
					}
					if (metaColumnCount > 6) {
						String meta7 = "";
						if (rs.getString("meta_7") == null || rs.getString("meta_7").length() == 0) {
							meta7 = " ";
						} else {
							meta7 = rs.getString("meta_7");
						}
						metaString += meta7 + "#";
					}
					if (metaColumnCount > 7) {
						String meta8 = "";
						if (rs.getString("meta_8") == null || rs.getString("meta_8").length() == 0) {
							meta8 = " ";
						} else {
							meta8 = rs.getString("meta_8");
						}
						metaString += meta8 + "#";
					}
					if (metaColumnCount > 8) {
						String meta9 = "";
						if (rs.getString("meta_9") == null || rs.getString("meta_9").length() == 0) {
							meta9 = " ";
						} else {
							meta9 = rs.getString("meta_9");
						}
						metaString += meta9 + "#";
					}
					if (metaColumnCount > 9) {
						String meta10 = "";
						if (rs.getString("meta_10") == null || rs.getString("meta_10").length() == 0) {
							meta10 = " ";
						} else {
							meta10 = rs.getString("meta_10");
						}
						metaString += meta10 + "#";
					}
					if (metaColumnCount > 10) {
						String meta11 = "";
						if (rs.getString("meta_11") == null || rs.getString("meta_11").length() == 0) {
							meta11 = " ";
						} else {
							meta11 = rs.getString("meta_11");
						}
						metaString += meta11 + "#";
					}
					if (metaColumnCount > 11) {
						String meta12 = "";
						if (rs.getString("meta_12") == null || rs.getString("meta_12").length() == 0) {
							meta12 = " ";
						} else {
							meta12 = rs.getString("meta_12");
						}
						metaString += meta12 + "#";
					}
					if (metaColumnCount > 12) {
						String meta13 = "";
						if (rs.getString("meta_13") == null || rs.getString("meta_13").length() == 0) {
							meta13 = " ";
						} else {
							meta13 = rs.getString("meta_13");
						}
						metaString += meta13 + "#";
					}
					if (metaColumnCount > 13) {
						String meta14 = "";
						if (rs.getString("meta_14") == null || rs.getString("meta_14").length() == 0) {
							meta14 = " ";
						} else {
							meta14 = rs.getString("meta_14");
						}
						metaString += meta14 + "#";
					}
					if (metaColumnCount > 14) {
						String meta15 = "";
						if (rs.getString("meta_15") == null || rs.getString("meta_15").length() == 0) {
							meta15 = " ";
						} else {
							meta15 = rs.getString("meta_15");
						}
						metaString += meta15 + "#";
					}

					if (metaString.length() > 0) {
						String [] entries = (rs.getString("metabolite_abbreviation") + "#" + metaboliteName + "#" + charge + "#" + compartment + "#" + boundary + "#" + metaString.substring(0, metaString.length() - 1)).split("#");
						writer.writeNext(entries);
						rs.close();
					} else {
						String [] entries = (rs.getString("metabolite_abbreviation") + "#" + metaboliteName + "#" + charge + "#" + compartment + "#" + boundary).split("#");
						writer.writeNext(entries);
						rs.close();
					}
					
					
				}	
				writer.close();
				conn.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] arg) throws Exception { 

	}
}

