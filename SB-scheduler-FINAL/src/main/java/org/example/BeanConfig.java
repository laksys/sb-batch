package org.example;

import static java.lang.System.out;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class BeanConfig {
	@Bean
	ItemReader<Person> reader(DataSource dataSource) {
		JdbcCursorItemReader<Person> reader = new JdbcCursorItemReader<>();
		reader.setDataSource(dataSource);
		reader.setSql("SELECT * FROM person WHERE status = 'N'");
		reader.setRowMapper(new PersonMapper());
		return reader;
	}

	@Bean
	ItemProcessor<Person, Person> processor() {
		ItemProcessor<Person, Person> processor = new ItemProcessor<>() {
			@Override
			public Person process(Person item) throws Exception {
				Person person = new Person();
				person.setPid(item.getPid());
				person.setFirstName(item.getFirstName().toUpperCase());
				person.setLastName(item.getLastName().toUpperCase());
				person.setStatus('Y');
				return person;
			}
		};
		return processor;
	}

	@Bean
	ItemWriter<Person> writer(DataSource dataSource) {

		return new JdbcBatchItemWriterBuilder<Person>()
				.sql("UPDATE person SET firstname = :firstName, lastname = :lastName, status=:status WHERE pid=:pid")
				.dataSource(dataSource)
				.beanMapped()
				.build();
	}
	
	@Bean
	public Job importUserJob(JobRepository jobRepository,Step step) {
		out.println("---------job----------");
		return new JobBuilder("person", jobRepository)
	    //.listener(listener)
	    .start(step)
	    .build();
	}

	@Bean
	public Step step(
			ItemReader<Person> reader, ItemWriter<Person> writer, ItemProcessor<Person, Person> processor,
			JobRepository jobRepository, 
			DataSourceTransactionManager transactionManager
	       ) {
		
		out.println("------step---------");
		
		return new StepBuilder("step1x", jobRepository)
	    .<Person, Person> chunk(3, transactionManager)
	    .reader(reader)
	    .processor(processor)
	    .writer(writer)
	    .build();
	}
}
