package jmetal.metaheuristics.singleObjective.geneticAlgorithm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class MysqlOperation {
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;

	public void connectDb() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/strilingengineHeatExchangerProbwithConstrainshandling?"
							+ "user=root&password=root123");
		} catch (Exception e) {
			System.out.println("Exception occoured in Database in connection");
		}

	}

	public void writeAlgorithmTable(String alName) {
		try {
			// DATE TIME convertion bettwen java and mysql

			Statement st = connect.createStatement();
			String Insertquery = "insert into strilingengineHeatExchangerProbwithConstrainshandling.algorithm (alName ) values ('"
					+ alName + "')";
			/*
			 * preparedStatement = connect
			 * .prepareStatement("insert into test.findeelitistga values (?,?)"
			 * ); preparedStatement.setDouble(1, fitness);
			 * preparedStatement.setString(2, indv);
			 * preparedStatement.executeUpdate();
			 */
			st.executeUpdate(Insertquery);

		} catch (Exception e) {
			System.out.println("Exception occoured in Database in writing");
			e.printStackTrace();
		}
	}

	public void writeRunTable(String runId, String runTime, String resultIndv,
			double resultFitness, String alname, int runNumber) {
		try {
			// DATE TIME convertion bettwen java and mysql

			Statement st = connect.createStatement();
			String Insertquery = "insert into strilingengineHeatExchangerProbwithConstrainshandling.run (runId, runTime, resultIndv,resultFitness, alname, runNumber ) values ("
					+ "'"
					+ runId
					+ "'"
					+ ","
					+ "'"
					+ runTime
					+ "'"
					+ ","
					+ "'"
					+ resultIndv
					+ "'"
					+ ","
					+ resultFitness
					+ ","
					+ "'"
					+ alname + "'," + runNumber + ")";
			/*
			 * preparedStatement = connect
			 * .prepareStatement("insert into test.findeelitistga values (?,?)"
			 * ); preparedStatement.setDouble(1, fitness);
			 * preparedStatement.setString(2, indv);
			 * preparedStatement.executeUpdate();
			 */
			st.executeUpdate(Insertquery);

		} catch (Exception e) {
			System.out.println("Exception occoured in Database in writing");
		}
	}

	public void writeGenerationTable(String genId, int genNo, String genTime,
			String genIndv, double genFitness, String runId, double genPower) {
		try {
			// DATE TIME convertion bettwen java and mysql

			Statement st = connect.createStatement();
			String Insertquery = "insert into strilingengineHeatExchangerProbwithConstrainshandling.generation (genId, genNo, genTime,genIndv,genFitness, runId, genPower ) values ("
					+ "'"
					+ genId
					+ "'"
					+ ","
					+ genNo
					+ ","
					+ "'"
					+ genTime
					+ "'"
					+ ","
					+ "'"
					+ genIndv
					+ "'"
					+ ","
					+ genFitness
					+ ","
					+ "'" + runId + "'" +"," + genPower+ ")";
			/*
			 * preparedStatement = connect
			 * .prepareStatement("insert into test.findeelitistga values (?,?)"
			 * ); preparedStatement.setDouble(1, fitness);
			 * preparedStatement.setString(2, indv);
			 * preparedStatement.executeUpdate();
			 */
			st.executeUpdate(Insertquery);

		} catch (Exception e) {
			System.out.println("Exception occoured in Database in writing");
		}
	}

	public void updateRunTable(String runId, String resultIndv,
			double resultFitness, long estimatedTime, double runPower) {
		try {
			Statement st = connect.createStatement();
			String updateQuery = "update strilingengineHeatExchangerProbwithConstrainshandling.run set resultFitness="
					+ resultFitness + ", resultIndv='" + resultIndv
					+ "', estimatedTime=" + estimatedTime + ",runPower="+runPower + " where runId='" + runId
					+ "'";
			st.executeUpdate(updateQuery);
		} catch (Exception e) {
			System.out.println("Exception occoured in Database in writing");
		}

	}

	public void closeDb() {
		try {
			connect.close();
		} catch (Exception e) {
			System.out.println("Exception occoured in Database in closing");
		}

	}
}
