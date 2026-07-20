# Mockito Deep Dive -- Code Deep Dive
## Main Implementation
Package: com.javalab.03
### Mock Creation
@Mock creates mock, @InjectMocks injects dependencies
### Stubbing
when(mock.method()).thenReturn(value)
### Verification
verify(mock).method() confirms interaction
