package com.learning.ml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MLSolutionTest {

    private MLSolution solution;

    @BeforeEach
    void setUp() {
        solution = new MLSolution();
    }

    @Test
    void testSetClassIndex() {
        Instances mockData = mock(Instances.class);
        solution.setClassIndex(mockData, 0);
        verify(mockData).setClassIndex(0);
    }

    @Test
    void testSplitData() {
        Instances mockData = mock(Instances.class);
        when(mockData.numInstances()).thenReturn(100);
        when(mockData.numAttributes()).thenReturn(5);
        when(mockData.attribute(anyInt())).thenReturn(mock(weka.core.Attribute.class));
        
        Instances result = solution.splitData(mockData, 0.8);
        assertNotNull(result);
    }

    @Test
    void testDataPreprocessor() {
        MLSolution.DataPreprocessor preprocessor = new MLSolution.DataPreprocessor();
        assertNotNull(preprocessor);
    }
}