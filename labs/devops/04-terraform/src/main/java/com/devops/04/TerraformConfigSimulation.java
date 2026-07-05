package com.devops.terraform;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TerraformConfigSimulation {
    private final Map<String, String> state = new ConcurrentHashMap<>();

    public void resource(String type, String name, Map<String, String> config) {
        String key = type + "." + name;
        state.put(key, config.toString());
        System.out.println("Created resource: " + key);
    }

    public void output(String name, String value) {
        System.out.println("Output " + name + " = " + value);
    }

    public static void main(String[] args) {
        TerraformConfigSimulation tf = new TerraformConfigSimulation();

        tf.resource("aws_instance", "web", Map.of(
            "ami", "ami-0c55b159cbfafe1f0",
            "instance_type", "t2.micro"
        ));

        tf.resource("aws_s3_bucket", "data", Map.of(
            "bucket", "my-app-data-bucket",
            "acl", "private"
        ));

        tf.output("instance_id", "aws_instance.web.id");
        tf.output("bucket_arn", "aws_s3_bucket.data.arn");
    }
}
