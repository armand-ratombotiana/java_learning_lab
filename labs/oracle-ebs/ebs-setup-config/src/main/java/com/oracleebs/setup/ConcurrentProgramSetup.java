package com.oracleebs.setup;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ConcurrentProgramSetup {
    public enum ExecutionMethod { SQL, PLSQL, JAVA, HOST }
    public enum RequestPhase { PENDING, RUNNING, COMPLETED }

    public static class ConcurrentProgram {
        private final String shortName;
        private final String application;
        private final ExecutionMethod method;
        private final String executable;
        private final boolean enabled;

        public ConcurrentProgram(String shortName, String application, ExecutionMethod method, String executable, boolean enabled) {
            this.shortName = shortName;
            this.application = application;
            this.method = method;
            this.executable = executable;
            this.enabled = enabled;
        }

        public String getShortName() { return shortName; }
        public String getApplication() { return application; }
        public ExecutionMethod getMethod() { return method; }
        public String getExecutable() { return executable; }
        public boolean isEnabled() { return enabled; }
    }

    public static class ConcurrentRequest {
        private final long requestId;
        private final String programShortName;
        private final RequestPhase phase;
        private final String status;
        private final Instant submitted;
        private final Map<String, String> parameters;

        public ConcurrentRequest(long requestId, String program, RequestPhase phase, String status, Map<String, String> params) {
            this.requestId = requestId;
            this.programShortName = program;
            this.phase = phase;
            this.status = status;
            this.submitted = Instant.now();
            this.parameters = params;
        }

        public long getRequestId() { return requestId; }
        public String getProgramShortName() { return programShortName; }
        public RequestPhase getPhase() { return phase; }
        public String getStatus() { return status; }
        public Instant getSubmitted() { return submitted; }
        public Map<String, String> getParameters() { return parameters; }
    }

    private final Map<String, ConcurrentProgram> programs;
    private final Map<Long, ConcurrentRequest> requests;
    private final AtomicLong requestIdSeq;

    public ConcurrentProgramSetup() {
        this.programs = new ConcurrentHashMap<>();
        this.requests = new ConcurrentHashMap<>();
        this.requestIdSeq = new AtomicLong(1000);
    }

    public void registerProgram(ConcurrentProgram program) {
        programs.put(program.getShortName(), program);
    }

    public long submitRequest(String programShortName, Map<String, String> parameters) {
        ConcurrentProgram prog = programs.get(programShortName);
        if (prog == null) throw new IllegalArgumentException("Program not found: " + programShortName);
        if (!prog.isEnabled()) throw new IllegalStateException("Program is disabled: " + programShortName);
        long id = requestIdSeq.incrementAndGet();
        ConcurrentRequest req = new ConcurrentRequest(id, programShortName, RequestPhase.PENDING, "NORMAL", parameters);
        requests.put(id, req);
        return id;
    }

    public Optional<ConcurrentRequest> getRequest(long requestId) {
        return Optional.ofNullable(requests.get(requestId));
    }

    public List<ConcurrentRequest> getRequestsByPhase(RequestPhase phase) {
        return requests.values().stream().filter(r -> r.getPhase() == phase).toList();
    }

    public void cancelRequest(long requestId) {
        requests.computeIfPresent(requestId, (k, v) -> new ConcurrentRequest(k, v.getProgramShortName(), RequestPhase.COMPLETED, "CANCELLED", v.getParameters()));
    }

    public static ConcurrentProgramSetup createDefault() {
        ConcurrentProgramSetup setup = new ConcurrentProgramSetup();
        setup.registerProgram(new ConcurrentProgram("GLPOST", "General Ledger", ExecutionMethod.PLSQL, "GL_POST", true));
        setup.registerProgram(new ConcurrentProgram("APINV", "Payables", ExecutionMethod.JAVA, "ap.invoice.InvoiceProcessor", true));
        setup.registerProgram(new ConcurrentProgram("MRPWORK", "Manufacturing", ExecutionMethod.HOST, "mrp_worker.sh", false));
        setup.registerProgram(new ConcurrentProgram("PURLOAD", "Purchasing", ExecutionMethod.SQL, "po_interface_load.sql", true));
        return setup;
    }
}
