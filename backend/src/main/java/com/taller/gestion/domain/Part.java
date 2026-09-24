package com.taller.gestion.domain;
import jakarta.persistence.*; import java.math.BigDecimal;
@Entity public class Part { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(unique=true,nullable=false) private String sku; private String name; private int stock; private int minStock; private BigDecimal unitPrice; public Long getId(){return id;} public String getSku(){return sku;} public String getName(){return name;} public int getStock(){return stock;} public int getMinStock(){return minStock;} public boolean isLowStock(){return stock<=minStock;} }
