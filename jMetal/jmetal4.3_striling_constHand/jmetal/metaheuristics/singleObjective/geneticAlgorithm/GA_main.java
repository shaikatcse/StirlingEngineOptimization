//  GA_main.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.metaheuristics.singleObjective.geneticAlgorithm;

import java.util.HashMap;

import jmetal.core.*;
import jmetal.operators.crossover.*;
import jmetal.operators.mutation.*;
import jmetal.operators.selection.*;
import jmetal.problems.singleObjective.*;
import jmetal.util.JMException;
import jmetal.encodings.variable.Real;
import jmetal.metaheuristics.singleObjective.geneticAlgorithm.ElitistGA;
import jmetal.operators.crossover.*;
import jmetal.operators.mutation.*;
import jmetal.operators.crossover.ModifiedSBXCrossoverForFinDesignProblem;

import matlabcontrol.*;
//for mysql connections
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class runs a single-objective genetic algorithm (GA). The GA can be a
 * steady-state GA (class ssGA), a generational GA (class gGA), a synchronous
 * cGA (class scGA) or an asynchronous cGA (class acGA). The OneMax problem is
 * used to test the algorithms.
 */
public class GA_main {

	public static void main(String[] args) throws JMException,
			ClassNotFoundException, MatlabConnectionException,
			MatlabInvocationException, IOException {
		
		MatlabProxyFactory factory;
		MatlabProxy proxy;
		
		
		Problem problemReal; // The problem to solve
		Algorithm algorithm; // The algorithm to use
		Operator crossover; // Crossover operator
		Operator mutation; // Mutation operator
		Operator selection; // Selection operator

		Variable var[];

		// int bits ; // Length of bit string in the OneMax problem
		HashMap parameters; // Operator parameters

		MysqlOperation mysql = new MysqlOperation();
		mysql.connectDb();

		factory = new MatlabProxyFactory();
		proxy = factory.getProxy();
		
		int populationSize = 100;
		int maxEvaluations = 5000;
		
		int NumberOfRun = 20;
		String alName = "SeritEngine2";
		mysql.writeAlgorithmTable(alName);
		
		for (int i = 0; i < NumberOfRun; i++) {

			problemReal = new StrilingEngineHeatExchangerDesignProblemReal("Real", proxy);
			
			// calculate currrent time
			java.util.Date dt = new java.util.Date();

			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String runTime = sdf.format(dt);

			String runId = i + runTime;

			mysql.writeRunTable(runId, runTime, "", -1, alName,i+1);
		
			algorithm = new ElitistGA(problemReal, runId, mysql);

			/* Algorithm parameters */
			algorithm.setInputParameter("populationSize", populationSize);
			algorithm.setInputParameter("maxEvaluations", maxEvaluations);

			// Mutation and Crossover for Real codification

			parameters = new HashMap();
			parameters.put("probability", 0.9);
			parameters.put("distributionIndex", 20.0);
			crossover = CrossoverFactory.getCrossoverOperator(
					"SBXCrossover", parameters);

			parameters = new HashMap();
			parameters.put("probability",
					1.0 / problemReal.getNumberOfVariables());
			parameters.put("distributionIndex", 20.0);
			mutation = MutationFactory.getMutationOperator(
					"PolynomialMutation", parameters);

			/* Selection Operator */
			parameters = null;
			selection = SelectionFactory.getSelectionOperator(
					"BinaryTournament", parameters);
			
			/* Add the operators to the algorithm*/
		    algorithm.addOperator("crossover",crossover);
		    algorithm.addOperator("mutation",mutation);
		    algorithm.addOperator("selection",selection);
			

			/* Execute the Algorithm */
			long initTime = System.currentTimeMillis();
			SolutionSet population = algorithm.execute();
			long estimatedTime = System.currentTimeMillis() - initTime;
			System.out.println("Total execution time: " + estimatedTime);

			/* Log messages */
			System.out
					.println("Objectives values have been writen to file FUN");
			population.printObjectivesToFile("FUN");
			System.out
					.println("Variables values have been writen to file VAR");
			population.printVariablesToFile("VAR");

			// sol=population.get(0).getDecisionVariables();

			// for(int i;i<)

			System.out.println("Type: Real,  Number of Run:"+i+"efficiency  "+population.get(0).getObjective(0));

			var = population.get(0).getDecisionVariables();
			String resultIndv = "";
			for (int j = 0; j < var.length; j++) {
				resultIndv = resultIndv + var[j].getValue() + " ";
			}

			mysql.updateRunTable(runId, resultIndv, -1*population.get(0).getObjective(0),estimatedTime,population.get(0).getEnginePOwer() );
						
				
			}
		
		proxy.exit();
		mysql.closeDb();
	} // main
} // GA_main
