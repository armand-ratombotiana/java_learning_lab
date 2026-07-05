package com.devops.docker;

public class DockerfileSimulation {
    public static class Dockerfile {
        private final StringBuilder content = new StringBuilder();

        public Dockerfile from(String baseImage) {
            content.append("FROM ").append(baseImage).append("\n");
            return this;
        }

        public Dockerfile copy(String src, String dest) {
            content.append("COPY ").append(src).append(" ").append(dest).append("\n");
            return this;
        }

        public Dockerfile run(String command) {
            content.append("RUN ").append(command).append("\n");
            return this;
        }

        public Dockerfile expose(int port) {
            content.append("EXPOSE ").append(port).append("\n");
            return this;
        }

        public Dockerfile entrypoint(String... cmd) {
            content.append("ENTRYPOINT [\"");
            for (int i = 0; i < cmd.length; i++) {
                if (i > 0) content.append("\", \"");
                content.append(cmd[i]);
            }
            content.append("\"]\n");
            return this;
        }

        public String build() {
            return content.toString();
        }
    }

    public static void main(String[] args) {
        Dockerfile df = new Dockerfile();
        String result = df
            .from("openjdk:17-slim")
            .copy("target/app.jar", "/app/app.jar")
            .run("useradd -m appuser")
            .expose(8080)
            .entrypoint("java", "-jar", "/app/app.jar")
            .build();
        System.out.println(result);
    }
}
