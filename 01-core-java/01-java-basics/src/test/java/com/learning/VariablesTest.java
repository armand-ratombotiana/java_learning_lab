package com.learning;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive test suite for Variables class.
 * Tests variable types, scope, and naming conventions.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
@DisplayName("Variables Tests")
class VariablesTest {
    
    @Test
    @DisplayName("Should demonstrate variables without errors")
    void testDemonstrateVariables() {
        assertThatCode(() -> Variables.demonstrateVariables())
            .doesNotThrowAnyException();
    }
    
    @Test
    @DisplayName("Should get instance variable value")
    void testGetInstanceVar() {
        Variables vars = new Variables();
        assertThat(vars.getInstanceVar()).isEqualTo(100);
    }
    
    @Test
    @DisplayName("Should set instance variable value")
    void testSetInstanceVar() {
        Variables vars = new Variables();
        vars.setInstanceVar(200);
        assertThat(vars.getInstanceVar()).isEqualTo(200);
    }
    
    @Test
    @DisplayName("Should get instance string value")
    void testGetInstanceString() {
        Variables vars = new Variables();
        assertThat(vars.getInstanceString()).isEqualTo("Instance Variable");
    }
    
    @Test
    @DisplayName("Should get static variable value")
    void testGetStaticVar() {
        assertThat(Variables.getStaticVar()).isEqualTo(200);
    }
    
    @Test
    @DisplayName("Should get constant value")
    void testGetConstant() {
        assertThat(Variables.getConstant()).isEqualTo("CONSTANT_VALUE");
    }
    
    @Test
    @DisplayName("Should maintain instance variable independence")
    void testInstanceVariableIndependence() {
        Variables vars1 = new Variables();
        Variables vars2 = new Variables();
        
        vars1.setInstanceVar(100);
        vars2.setInstanceVar(200);
        
        assertThat(vars1.getInstanceVar()).isEqualTo(100);
        assertThat(vars2.getInstanceVar()).isEqualTo(200);
    }
    
    @Test
    @DisplayName("Should share static variable across instances")
    void testStaticVariableSharing() {
        Variables vars1 = new Variables();
        Variables vars2 = new Variables();
        
        // Static variable is shared
        assertThat(vars1.getStaticVar()).isEqualTo(vars2.getStaticVar());
    }
    
    @Test
    @DisplayName("Should have immutable constant")
    void testConstantImmutability() {
        String constant1 = Variables.getConstant();
        String constant2 = Variables.getConstant();
        
        assertThat(constant1).isEqualTo(constant2);
        assertThat(constant1).isEqualTo("CONSTANT_VALUE");
    }
    
    @Test
    @DisplayName("Should handle negative instance variable values")
    void testNegativeInstanceVariable() {
        Variables vars = new Variables();
        vars.setInstanceVar(-100);
        assertThat(vars.getInstanceVar()).isEqualTo(-100);
    }
    
    @Test
    @DisplayName("Should handle zero instance variable value")
    void testZeroInstanceVariable() {
        Variables vars = new Variables();
        vars.setInstanceVar(0);
        assertThat(vars.getInstanceVar()).isEqualTo(0);
    }
    
    @Test
    @DisplayName("Should handle maximum integer value")
    void testMaximumIntegerValue() {
        Variables vars = new Variables();
        vars.setInstanceVar(Integer.MAX_VALUE);
        assertThat(vars.getInstanceVar()).isEqualTo(Integer.MAX_VALUE);
    }
    
    @Test
    @DisplayName("Should handle minimum integer value")
    void testMinimumIntegerValue() {
        Variables vars = new Variables();
        vars.setInstanceVar(Integer.MIN_VALUE);
        assertThat(vars.getInstanceVar()).isEqualTo(Integer.MIN_VALUE);
    }
}