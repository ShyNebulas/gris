package com.github.gris.resolver;

/**
 * Enum representing the states of a variable within a program.
 */
public enum VariableState {
    /**
     * Indicates that the variable has been declared but not yet defined or initialized.
     */
    DECLARED,

    /**
     * Indicates that the variable has been defined or initialized.
     */
    DEFINED,

    /**
     * Indicates that the variable has been used within the program.
     */
    USED
}
