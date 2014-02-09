//  SBXCrossover.java
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

package jmetal.operators.crossover;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import jmetal.core.*;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.encodings.variable.*;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.Configuration.*;
import jmetal.util.wrapper.XReal;

/**
 * This class allows to apply a SBX crossover operator using two parent
 * solutions.
 */
public class ModifiedSBXCrossoverForFinDesignProblem extends Crossover {
	/**
	 * EPS defines the minimum difference allowed between real values
	 */
	protected static final double EPS = 1.0e-14;

	public static final double ETA_C_DEFAULT_ = 20.0;
	private Double crossoverProbability_ = 0.9;
	private double distributionIndex_ = ETA_C_DEFAULT_;

	/**
	 * Valid solution types to apply this operator
	 */
	private static List VALID_TYPES = Arrays.asList(RealSolutionType.class);

	/**
	 * Constructor Create a new SBX crossover operator whit a default index
	 * given by <code>DEFAULT_INDEX_CROSSOVER</code>
	 */
	public ModifiedSBXCrossoverForFinDesignProblem(
			HashMap<String, Object> parameters) {
		super(parameters);

		if (parameters.get("probability") != null)
			crossoverProbability_ = (Double) parameters.get("probability");
		if (parameters.get("distributionIndex") != null)
			distributionIndex_ = (Double) parameters.get("distributionIndex");
	} // SBXCrossover

	/**
	 * Perform the crossover operation.
	 * 
	 * @param probability
	 *            Crossover probability
	 * @param parent1
	 *            The first parent
	 * @param parent2
	 *            The second parent
	 * @return An array containing the two offsprings
	 */
	public Solution[] doCrossover(double probability, Solution parent1,
			Solution parent2) throws JMException {

		Solution[] offSpring = new Solution[2];

		offSpring[0] = new Solution(parent1);
		offSpring[1] = new Solution(parent2);

		if (PseudoRandom.randDouble() <= probability) {

			int numberOfVariables = parent1.getType().problem_.getNumberOfVariables();
			
			int crossoverPoint= PseudoRandom.randInt(0, numberOfVariables-1);
			
			XReal p1  = new XReal(parent1);
			XReal p2  = new XReal(parent2);
			
			
			XReal offs1 = new XReal(offSpring[0]);
			XReal offs2 = new XReal(offSpring[1]);

			//1. first copy the first portion from opposite parent
			for(int j=0;j<crossoverPoint;j++){
				//offSpring[0].getDecisionVariables()[j]=parent2.getDecisionVariables()[j];
				//offSpring[2].getDecisionVariables()[j]=parent1.getDecisionVariables()[j];
				offs1.setValue(j, p2.getValue(j));
				offs2.setValue(j, p1.getValue(j));
				
				
			}
			
			//2. next make some change on crossover point 	

			//int i;
			double rand;
			double y1, y2, yL, yu;
			double c1, c2;
			double alpha, beta, betaq;
			double valueX1, valueX2;
			XReal x1 = new XReal(parent1);
			XReal x2 = new XReal(parent2);
			
			//for (i = 0; i < numberOfVariables; i++) {
				valueX1 = x1.getValue(crossoverPoint);
				valueX2 = x2.getValue(crossoverPoint);
				if (PseudoRandom.randDouble() <= 0.5) {
					if (java.lang.Math.abs(valueX1 - valueX2) > EPS) {

						if (valueX1 < valueX2) {
							y1 = valueX1;
							y2 = valueX2;
						} else {
							y1 = valueX2;
							y2 = valueX1;
						} // if

						yL = x1.getLowerBound(crossoverPoint);
						yu = x1.getUpperBound(crossoverPoint);
						rand = PseudoRandom.randDouble();
						beta = 1.0 + (2.0 * (y1 - yL) / (y2 - y1));
						alpha = 2.0 - java.lang.Math.pow(beta,
								-(distributionIndex_ + 1.0));

						if (rand <= (1.0 / alpha)) {
							betaq = java.lang.Math.pow((rand * alpha),
									(1.0 / (distributionIndex_ + 1.0)));
						} else {
							betaq = java.lang.Math.pow((1.0 / (2.0 - rand
									* alpha)),
									(1.0 / (distributionIndex_ + 1.0)));
						} // if

						c1 = 0.5 * ((y1 + y2) - betaq * (y2 - y1));
						beta = 1.0 + (2.0 * (yu - y2) / (y2 - y1));
						alpha = 2.0 - java.lang.Math.pow(beta,
								-(distributionIndex_ + 1.0));

						if (rand <= (1.0 / alpha)) {
							betaq = java.lang.Math.pow((rand * alpha),
									(1.0 / (distributionIndex_ + 1.0)));
						} else {
							betaq = java.lang.Math.pow((1.0 / (2.0 - rand
									* alpha)),
									(1.0 / (distributionIndex_ + 1.0)));
						} // if

						c2 = 0.5 * ((y1 + y2) + betaq * (y2 - y1));

						if (c1 < yL)
							c1 = yL;

						if (c2 < yL)
							c2 = yL;

						if (c1 > yu)
							c1 = yu;

						if (c2 > yu)
							c2 = yu;

						if (PseudoRandom.randDouble() <= 0.5) {
							offs1.setValue(crossoverPoint, c2) ;
							offs2.setValue(crossoverPoint, c1);
						} else {
							offs1.setValue(crossoverPoint, c1);
							offs2.setValue(crossoverPoint, c2);
						} // if
					} else {
						offs1.setValue(crossoverPoint, valueX1);
						offs2.setValue(crossoverPoint, valueX2);
					} // if
				} else {
					offs1.setValue(crossoverPoint, valueX2);
					offs2.setValue(crossoverPoint, valueX1);
				} // if
			} // if
	//	} // if

		return offSpring;
	} // doCrossover

	/**
	 * Executes the operation
	 * 
	 * @param object
	 *            An object containing an array of two parents
	 * @return An object containing the offSprings
	 */
	public Object execute(Object object) throws JMException {
		Solution[] parents = (Solution[]) object;

		if (parents.length != 2) {
			Configuration.logger_
					.severe("ModifiedSBXCrossoverForFinDesignProblem.execute: operator needs two "
							+ "parents");
			Class cls = java.lang.String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		} // if

		if (!(VALID_TYPES.contains(parents[0].getType().getClass()) && VALID_TYPES
				.contains(parents[1].getType().getClass()))) {
			Configuration.logger_.severe("ModifiedSBXCrossoverForFinDesignProblem.execute: the solutions "
					+ "type " + parents[0].getType()
					+ " is not allowed with this operator");

			Class cls = java.lang.String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		} // if

		Solution[] offSpring;
		offSpring = doCrossover(crossoverProbability_, parents[0], parents[1]);

		// for (int i = 0; i < offSpring.length; i++)
		// {
		// offSpring[i].setCrowdingDistance(0.0);
		// offSpring[i].setRank(0);
		// }
		return offSpring;
	} // execute
} // SBXCrossover
