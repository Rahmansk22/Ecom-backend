package com.ecommerce.platform.config;

import com.ecommerce.platform.model.Order;
import com.ecommerce.platform.repository.OrderRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SalesExportBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final OrderRepository orderRepository;

    public SalesExportBatchConfig(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  OrderRepository orderRepository) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.orderRepository = orderRepository;
    }

    @Bean
    public RepositoryItemReader<Order> orderReader() {
        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("createdAt", Sort.Direction.DESC);

        return new RepositoryItemReaderBuilder<Order>()
                .name("orderReader")
                .repository(orderRepository)
                .methodName("findAll")
                .sorts(sorts)
                .pageSize(50)
                .build();
    }

    @Bean
    public ItemProcessor<Order, OrderCsvRepresentation> orderProcessor() {
        return order -> new OrderCsvRepresentation(
                order.getId().toString(),
                order.getShippingName(),
                order.getTotalPrice().toString(),
                order.getShippingCharge().toString(),
                order.getNetAmount().toString(),
                order.getStatus().toString(),
                order.getPaymentStatus().toString(),
                order.getPaymentMethod().toString(),
                order.getTransactionId() != null ? order.getTransactionId() : "N/A",
                order.getCreatedAt().toString()
        );
    }

    @Bean
    public FlatFileItemWriter<OrderCsvRepresentation> csvWriter() {
        File dir = new File("exports");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return new FlatFileItemWriterBuilder<OrderCsvRepresentation>()
                .name("csvWriter")
                .resource(new FileSystemResource("exports/latest_sales_report.csv"))
                .delimited()
                .names("orderId", "customerName", "totalAmount", "shippingCharge", "netAmount", "orderStatus", "paymentStatus", "paymentMethod", "transactionId", "createdAt")
                .headerCallback(writer -> writer.write("OrderID,CustomerName,TotalAmount,ShippingCharge,NetAmount,OrderStatus,PaymentStatus,PaymentMethod,TransactionID,CreatedAt"))
                .build();
    }

    @Bean
    public Step exportSalesStep() {
        return new StepBuilder("exportSalesStep", jobRepository)
                .<Order, OrderCsvRepresentation>chunk(10, transactionManager)
                .reader(orderReader())
                .processor(orderProcessor())
                .writer(csvWriter())
                .build();
    }

    @Bean
    public Job salesExportJob() {
        return new JobBuilder("salesExportJob", jobRepository)
                .start(exportSalesStep())
                .build();
    }

    public static record OrderCsvRepresentation(
            String orderId,
            String customerName,
            String totalAmount,
            String shippingCharge,
            String netAmount,
            String orderStatus,
            String paymentStatus,
            String paymentMethod,
            String transactionId,
            String createdAt
    ) {}
}
