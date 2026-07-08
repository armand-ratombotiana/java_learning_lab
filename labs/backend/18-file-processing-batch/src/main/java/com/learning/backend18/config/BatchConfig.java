package com.learning.backend18.config;

import com.learning.backend18.model.Product;
import com.learning.backend18.processor.ProductProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {

    @Bean
    public FlatFileItemReader<Product> reader() {
        return new FlatFileItemReaderBuilder<Product>()
            .name("productItemReader")
            .resource(new ClassPathResource("input/products.csv"))
            .delimited()
            .names("id", "name", "price", "category")
            .linesToSkip(1)
            .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(Product.class);
            }})
            .build();
    }

    @Bean
    public ProductProcessor processor() {
        return new ProductProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Product> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Product>()
            .sql("INSERT INTO products (id, name, price, category) VALUES (:id, :name, :price, :category)")
            .dataSource(dataSource)
            .beanMapped()
            .build();
    }

    @Bean
    public Job importProductJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("importProductJob", jobRepository)
            .start(step1)
            .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                      FlatFileItemReader<Product> reader, ProductProcessor processor,
                      JdbcBatchItemWriter<Product> writer) {
        return new StepBuilder("step1", jobRepository)
            .<Product, Product>chunk(10, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .faultTolerant()
            .skipLimit(5)
            .skip(Exception.class)
            .build();
    }
}
