package com.learning.annotations;

import org.junit.jupiter.api.Test;
import java.lang.reflect.*;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationTests {

    @Test
    void testCustomAnnotationExists() throws Exception {
        Method method = Lab.class.getMethod("sampleTestMethod");
        assertNotNull(method, "Method should exist");
    }

    @Test
    void testAnnotationRetention() {
        java.lang.reflect.Method method;
        try {
            method = Lab.class.getMethod("sampleTestMethod");
            Lab.Test test = method.getAnnotation(Lab.Test.class);
            assertNotNull(test, "@Test annotation should be present at runtime");
            assertEquals("This is a test method", test.description());
            assertEquals(1, test.priority());
        } catch (NoSuchMethodException e) {
            fail("Method should exist");
        }
    }

    @Test
    void testClassIntrospection() {
        Class<?> clazz = Lab.Person.class;

        assertTrue(clazz.getSimpleName().equals("Person"));
        assertFalse(clazz.isInterface());
        assertFalse(clazz.isEnum());

        Field[] fields = clazz.getDeclaredFields();
        assertTrue(fields.length >= 2, "Should have at least name and age fields");

        Method[] methods = clazz.getDeclaredMethods();
        assertTrue(methods.length >= 4, "Should have getters and setters");
    }

    @Test
    void testReflectionInvokeMethod() throws Exception {
        Lab.Person person = new Lab.Person("TestUser", 25);
        Method getName = Lab.Person.class.getMethod("getName");
        Object result = getName.invoke(person);
        assertEquals("TestUser", result);
    }

    @Test
    void testPrivateFieldAccess() throws Exception {
        Lab.Person person = new Lab.Person("PrivateName", 30);
        Field nameField = Lab.Person.class.getDeclaredField("name");
        nameField.setAccessible(true);
        assertEquals("PrivateName", nameField.get(person));
    }

    @Test
    void testServiceInfoAnnotation() throws Exception {
        Class<?> serviceClass = Lab.SampleService.class;
        assertTrue(serviceClass.isAnnotationPresent(Lab.ServiceInfo.class));

        Lab.ServiceInfo info = serviceClass.getAnnotation(Lab.ServiceInfo.class);
        assertEquals("SampleService", info.name());
        assertEquals("2.0", info.version());
    }
}