package com.final_project.math;

import java.util.Map;

public class PropositionalLogicStatement extends LogicalStatement {
    private String statement;

    public PropositionalLogicStatement(String statement) {
        this.statement = statement;
    }

    @Override
    public boolean evaluate(Map<String, Boolean> variableValues) {
        return evaluateStatement(statement, variableValues);
    }

    // Method to evaluate logical statements
    private boolean evaluateStatement(String statement, Map<String, Boolean> variableValues) {
        statement = statement.replaceAll("\\s+", ""); // Remove white spaces
        if (statement.startsWith("(") && statement.endsWith(")")) {
            // Remove outer parentheses and evaluate inner expression
            String innerStatement = statement.substring(1, statement.length() - 1);
            return evaluateStatement(innerStatement, variableValues);
        } else if (statement.startsWith("not(") && statement.endsWith(")")) {
            // Handle negation
            String innerStatement = statement.substring(4, statement.length() - 1);
            return !evaluateStatement(innerStatement, variableValues);
        } else if (statement.contains("or")) {
            // Handle OR operator
            int orIndex = statement.indexOf("or");
            String firstPart = statement.substring(0, orIndex);
            String secondPart = statement.substring(orIndex + 2); // Skip "or"
            return evaluateStatement(firstPart, variableValues) || evaluateStatement(secondPart, variableValues);
        } else if (statement.contains("and")) {
            // Handle AND operator
            int andIndex = statement.indexOf("and");
            String firstPart = statement.substring(0, andIndex);
            String secondPart = statement.substring(andIndex + 3); // Skip "and"
            return evaluateStatement(firstPart, variableValues) && evaluateStatement(secondPart, variableValues);
        } else if (statement.contains("then")) {
            // Handle IF THEN operator
            int thenIndex = statement.indexOf("then");
            String antecedent = statement.substring(0, thenIndex);
            String consequent = statement.substring(thenIndex + 4); // Skip "then"
            return !evaluateStatement(antecedent, variableValues) || evaluateStatement(consequent, variableValues);
        } else if (statement.contains("iff")) {
            // Handle IF AND ONLY IF operator
            int iffIndex = statement.indexOf("iff");
            String firstPart = statement.substring(0, iffIndex);
            String secondPart = statement.substring(iffIndex + 3); // Skip "iff"
            boolean firstResult = evaluateStatement(firstPart, variableValues);
            boolean secondResult = evaluateStatement(secondPart, variableValues);
            return (firstResult && secondResult) || (!firstResult && !secondResult);
        } else {
            // Single variable or negation
            if (statement.startsWith("not")) {
                // Handle negation of single variable
                String innerStatement = statement.substring(3); // Remove "not" from the statement
                return !getVariableValue(innerStatement, variableValues);
            } else {
                // Single variable
                return getVariableValue(statement, variableValues);
            }
        }
    }

    // Helper method to get variable value with default false if variable not found
    private boolean getVariableValue(String variable, Map<String, Boolean> variableValues) {
        return variableValues.getOrDefault(variable, false);
    }
    
 // Method to identify the type of logical statement
    public String identifyStatementType(Map<String, Boolean> variableValues) {
        // Evaluate the statement for all possible truth value combinations of variables
        int numVariables = variableValues.size();
        int totalCombinations = 1 << numVariables; // 2^numVariables
        boolean isTautology = true;
        boolean isContradiction = true;

        for (int i = 0; i < totalCombinations; i++) {
            // Generate truth values for each variable combination
            int varIndex = 0;
            for (String varName : variableValues.keySet()) {
                boolean value = ((i >> varIndex) & 1) == 1;
                variableValues.put(varName, value);
                varIndex++;
            }

            // Evaluate the statement
            boolean result = evaluateStatement(statement, variableValues);

            // Update tautology and contradiction flags
            if (!result) {
                isTautology = false;
            } else {
                isContradiction = false;
            }

            // If it's not yet determined, it's a contingency
            if (!isTautology && !isContradiction) {
                return "Contingency";
            }
        }

        // If it reaches here, it's either a tautology or contradiction
        if (isTautology) {
            return "Tautology";
        } else {
            return "Contradiction";
        }
    }

}
