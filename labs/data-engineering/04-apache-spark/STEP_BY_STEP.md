# Step-by-Step

1. **Setup**: Add spark-core and spark-sql dependencies
2. **Initialize**: SparkSession.builder().appName().getOrCreate()
3. **Load**: spark.read().format().load()
4. **Transform**: filter, groupBy, agg, withColumn
5. **Write**: result.write().mode().format().save()
6. **Submit**: spark-submit --class Main --master yarn app.jar
