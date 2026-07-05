package com.devops.docker;

public class MultiStageBuild {
    public static class Stage {
        private final String name;
        private final String baseImage;
        private final StringBuilder commands = new StringBuilder();

        public Stage(String name, String baseImage) {
            this.name = name;
            this.baseImage = baseImage;
        }

        public Stage copyFrom(String stage, String src, String dest) {
            commands.append("COPY --from=").append(stage).append(" ")
                    .append(src).append(" ").append(dest).append("\n");
            return this;
        }

        public String render() {
            return "FROM " + baseImage + " AS " + name + "\n" + commands;
        }
    }

    public static void main(String[] args) {
        Stage build = new Stage("builder", "maven:3.8-openjdk-17");
        Stage runtime = new Stage("runtime", "openjdk:17-slim");

        System.out.println(build.render());
        System.out.println(runtime.copyFrom("builder", "/app/target/app.jar", "/app/app.jar").render());
    }
}
