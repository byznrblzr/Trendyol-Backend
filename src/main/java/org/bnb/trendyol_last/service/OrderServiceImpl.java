package org.bnb.trendyol_last.service;

import net.sf.jasperreports.engine.*;
import org.bnb.trendyol_last.dto.OrderDTO;
import org.bnb.trendyol_last.dto.OrderRequestDTO;
import org.bnb.trendyol_last.exception.ErrorMessages;
import org.bnb.trendyol_last.exception.ResourceNotFoundException;
import org.bnb.trendyol_last.mapper.OrderMapper;
import org.bnb.trendyol_last.model.Customer;
import org.bnb.trendyol_last.model.Order;
import org.bnb.trendyol_last.model.Product;
import org.bnb.trendyol_last.repository.CustomerRepository;
import org.bnb.trendyol_last.repository.OrderRepository;
import org.bnb.trendyol_last.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;this.customerRepository = customerRepository;this.productRepository = productRepository;}

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(OrderMapper::toDTO).toList();
    }

    public OrderDTO getOrderById(long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_ORDER_NOT_FOUND + ": " + id));
        return OrderMapper.toDTO(order);
    }

    public OrderDTO createOrder(OrderRequestDTO requestOrder) {
        Customer customer = customerRepository.findById(requestOrder.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_CUSTOMER_NOT_FOUND + ": " + requestOrder.getCustomerId()));

        Product product = productRepository.findById(requestOrder.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_PRODUCT_NOT_FOUND + ": " + requestOrder.getProductId()));

        Order order = OrderMapper.toEntity(requestOrder, customer, product);
        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toDTO(savedOrder);
    }

    public OrderDTO updateOrder(long id, OrderRequestDTO updateOrder) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_ORDER_NOT_FOUND + ": " + id));

        Customer customer = customerRepository.findById(updateOrder.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_CUSTOMER_NOT_FOUND + ": " + updateOrder.getCustomerId()));

        Product product = productRepository.findById(updateOrder.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_PRODUCT_NOT_FOUND + ": " + updateOrder.getProductId()));

        OrderMapper.updateEntity(existingOrder, updateOrder, customer, product);
        Order updatedOrder = orderRepository.save(existingOrder);
        return OrderMapper.toDTO(updatedOrder);
    }

    public void deleteOrder(long id) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.ERROR_ORDER_NOT_FOUND + ": " + id));
        orderRepository.delete(existingOrder);
    }






    @Transactional
    public byte[] generateOrderPdf(Long orderId) {
        try {
            //order var mı? varsa devam et
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            ErrorMessages.ERROR_ORDER_NOT_FOUND + ": " + orderId
                    ));

            //oluşturduğum tasarım dokümanını oku
            //InputStream -> javada kullanılan byte byte okuyan araç
            // getClass() o anki nesne hangi classtan. classpathin başlangıcı için lazım
            //getResourceAsStream() dosyayı açar ve içeriği okunur hale getirir.
            //dosya yoksa null döner
            InputStream reportStream = getClass()
                    .getResourceAsStream("/reports/order_report.jrxml");

            if (reportStream == null) {
                throw new RuntimeException("order_report.jrxml dosyası bulunamadı.");
            }
            //JRXML dosyasını JasperReport nesnesine çeviriyoruz.
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            //PDF içine göndereceğimiz verileri tutacak Map oluşturuyoruz.
            //tasarımdaki isimlerle buradaki key aynı olmalı
            Map<String, Object> parameters = new HashMap<>();

            // Order information
            //Order bilgilerini PDF parameter'larına eklenir
            parameters.put("orderId", order.getId());
            parameters.put("orderDate", order.getOrderDate());
            parameters.put("city", order.getCity());
            parameters.put("deliveryStatus", order.getDeliveryStatus());

            // Customer information
            //Customer bilgilerini PDF'e eklenir
            //
            if (order.getCustomer() != null) {
                parameters.put("customerName", order.getCustomer().getName());
                parameters.put("customerAddress", order.getCustomer().getAddress());
                parameters.put("customerPhone", order.getCustomer().getPhone());
            }

            // Product information
            if (order.getProduct() != null) {
                parameters.put("productName", order.getProduct().getName());
                parameters.put("productSupplier", order.getProduct().getSupplier());
                parameters.put("productPrice", order.getProduct().getPrice());

                //resmi okumaya hazırlanıyorum
                //Eğer product'ın imagePath alanı varsa yani ürünün resmi varsa,
                //o dosyayı pathte bul ve okunabilir hale getir image nesnesinde tut
                InputStream image = null;

                if (order.getProduct().getImagePath() != null) {
                     // imagePath database'de şöyle tutuluyorsa uploads/products/product-1.jpg
                     //Paths.get(...) bu yolu dosya sisteminden bulur.
                    Path imagePath = Paths.get(order.getProduct().getImagePath());
                    //Dosya gerçekten var mı kontrol ediyoruz.
                    // Yoksa PDF üretimi patlamasın diye image null kalır.
                    if (Files.exists(imagePath)) {
                        image = Files.newInputStream(imagePath);
                    }
                }
                parameters.put("productImage", image);
            }

            //
            //jasperReport: Tasarım dosyasının derlenmiş hali
            //parameters:PDF içine basılacak veriler
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameters,
                    new JREmptyDataSource()
            );

            //Doldurulmuş Jasper raporunu PDF byte array'e çeviriyoruz.
            //Postman veya browser bunu PDF dosyası olarak alabilecek.
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (Exception e) {
            throw new RuntimeException("Order PDF oluşturulurken hata oluştu: " + e.getMessage(), e);
        }
    }
}