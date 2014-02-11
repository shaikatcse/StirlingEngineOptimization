package jmetal.metaheuristics.singleObjective.geneticAlgorithm;

import java.sql.Connection;

import jmetal.metaheuristics.singleObjective.geneticAlgorithm.MysqlOperation;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Comparator;

import jmetal.core.*;
import jmetal.operators.*;
import jmetal.problems.*;
import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.Operator;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.comparators.ObjectiveComparator;
import jmetal.util.JMException;

//import ro.ulbsibiu.acaps.mapper.ga.GeneticAlgorithmMapper;
//import ro.ulbsibiu.acaps.mapper.ga.jmetal.base.TrackedAlgorithm;
//import ro.ulbsibiu.acaps.mapper.ga.jmetal.base.operator.crossover.NocPositionBasedCrossover;


/**
 * Elitist genetic algorithm. Actually this is the jMetal version of
 * {@link GeneticAlgorithmMapper}.
 * 
 * @author shaikat
 * @author cipi
 * 
 */
public class ElitistGA /*extends TrackedAlgorithm*/ extends Algorithm{
       // private Problem problem_;

		String runId ;
		MysqlOperation mysql;
		
		Variable var[];
	 	
	 	public ElitistGA(Problem problem, String runId, MysqlOperation mysql) {
               super(problem);
               this.runId = runId;
               this.mysql=mysql;
	 	}

        
        /**
         * Execute the elitist genetic algorithm algorithm
         * 
         * @throws JMException
         */
        public SolutionSet execute() throws JMException, ClassNotFoundException {
                int maxEvaluations;
                int populationSize;
                int evaluations;

                SolutionSet population;
                SolutionSet offspringPopulation;

                Operator mutationOperator;
                Operator crossoverOperator;
                Operator selectionOperator;
                Comparator comparator;

                comparator = new DominanceComparator(); // Single objective comparator

                // Read the parameter
                maxEvaluations = ((Integer) this.getInputParameter("maxEvaluations"))
                                .intValue();
                populationSize = ((Integer) this.getInputParameter("populationSize"))
                                .intValue();

                // Initialize the variables
                population = new SolutionSet(populationSize);
                offspringPopulation = new SolutionSet(2 * populationSize);
                evaluations = 0;

                // Read the operators
                mutationOperator = this.operators_.get("mutation");
                crossoverOperator = this.operators_.get("crossover");
                selectionOperator = this.operators_.get("selection");

                // Create the initial population
                Solution newIndividual;
                for (int i = 0; i < populationSize; i++) {
                        newIndividual = new Solution(problem_);
                        problem_.evaluate(newIndividual);
                        problem_.evaluateConstraints(newIndividual);
                        evaluations++;
                        population.add(newIndividual);
                } // for
                
                /*algorithmTracker.processIntermediateSolution("generations",
                                Integer.toString(evaluations / populationSize),
                                population.get(0));
*/
                // main loop
                while (evaluations < maxEvaluations) {

                        for (int i = 0; i < populationSize / 2; i++) {

                                Solution[] parents = new Solution[2];

                                // Selection
                                parents[0] = (Solution) selectionOperator.execute(population);
                                parents[1] = (Solution) selectionOperator.execute(population);

                                //for random selection
                                //parents = (Solution[]) selectionOperator.execute(population);
                                
                                // if crossover operator is NocpositionBasedCrossover the
                                // crossover return one offspring in each call of the function
                              /*  if (NocPositionBasedCrossover.class
                                                .isAssignableFrom(crossoverOperator.getClass())) {

                                        // crossover
                                        Solution[] offsprings = (Solution[]) crossoverOperator
                                                        .execute(parents);

                                        // Mutation
                                        mutationOperator.execute(offsprings[0]);
                                        
                                        problem_.evaluate(offsprings[0]);
                                        offspringPopulation.add(offsprings[0]);

                                        //selection of next set of parents
                                        parents[0] = (Solution) selectionOperator.execute(population);
                                        parents[1] = (Solution) selectionOperator.execute(population);
                                        // crossover
                                        offsprings = (Solution[]) crossoverOperator
                                                        .execute(parents);

                                        // Mutation
                                        mutationOperator.execute(offsprings[0]);
                                        
                                        problem_.evaluate(offsprings[0]);
                                        offspringPopulation.add(offsprings[0]);

                                } else {*/

                                        // crossover
                                        Solution[] offsprings = (Solution[]) crossoverOperator
                                                        .execute(parents);

                                        // Mutation
                                        mutationOperator.execute(offsprings[0]);
                                        mutationOperator.execute(offsprings[1]);

                                        problem_.evaluate(offsprings[0]);
                                        problem_.evaluateConstraints(offsprings[0]);
                                        problem_.evaluate(offsprings[1]);
                                        problem_.evaluateConstraints(offsprings[1]);
                                        
                                        offspringPopulation.add(offsprings[0]);
                                        offspringPopulation.add(offsprings[1]);

                               // }

                                evaluations += 2;

                        }

                        // copy the population to offspring population
                        for (int i = 0; i < populationSize; i++) {
                                offspringPopulation.add(population.get(i));
                        } // for

                        population.clear();

                        // sort the offspring population according to the fitness
                        offspringPopulation.sort(comparator);
                        

                        // take best populationsize number of individuals from the whole
                        // population (population + offspring population)
                        for (int i = 0; i < populationSize; i++)
                                population.add(offspringPopulation.get(i));

                        offspringPopulation.clear();
                        
                 /*       algorithmTracker.processIntermediateSolution("generations",
                                        Integer.toString(evaluations / populationSize),
                                        population.get(0));*/
                        java.util.Date dt = new java.util.Date();

        				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
        						"yyyy-MM-dd HH:mm:ss");
        				String genTime = sdf.format(dt);

                        String genId= ((int) evaluations / populationSize) +genTime; 
                        int genNo = (int) evaluations / populationSize;
                        
                        var = population.get(0).getDecisionVariables();
        				String genIndv = "";
        				for (int j = 0; j < var.length; j++) {
        					genIndv = genIndv + var[j].getValue() + " ";
        				}
                        
                        mysql.writeGenerationTable(genId, genNo, genTime,genIndv ,-1*population.get(0).getObjective(0), runId, population.get(0).getEnginePOwer());
                        System.out.println(population.get(0).getObjective(0));
                        
                }

                // Return a population with the best individual
                SolutionSet resultPopulation = new SolutionSet(1);
                resultPopulation.add(population.get(0));

                return resultPopulation;
        }
}




class MysqlOperationForGeneration {
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;

	public void connectDb() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/test?"
							+ "user=root&password=root123");
		} catch (Exception e) {
			System.out.println("Exception occoured in Database in connection");
		}

	}

	public void writeDb(int generationNo, double fitness) {
		try {
			preparedStatement = connect
					.prepareStatement("insert into test.GenerationFitness values (?,?)");
			preparedStatement.setInt(1, generationNo);
			preparedStatement.setDouble(2,fitness);
			preparedStatement.executeUpdate();
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


