package com.final_project.math;

import java.util.Map;

public abstract class LogicalStatement {
    public abstract boolean evaluate(Map<String, Boolean> variableValues);
}
