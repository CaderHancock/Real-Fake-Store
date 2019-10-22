package gq.cader.realfakestoreserver.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "ORDERS")
@Data
public class Order {
    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @OneToOne(cascade= {CascadeType.ALL}, fetch = FetchType.EAGER)
    private  ShoppingCart shoppingCart;
    @ManyToOne(targetEntity = Address.class, fetch = FetchType.EAGER)
    private  Address address;
    @Column(name = "TIMESTAMP")
    private  Instant timeOrderReceived;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @Column(name = "CUSTOMER_ID")
    private Integer customerId;

    public Order(){}
    public Order(Integer customerId, ShoppingCart shoppingCart,
                 Address address, Instant instant){

        this.address = address;
        this.shoppingCart= shoppingCart;
        this.timeOrderReceived =   instant;
        this.status = OrderStatus.PENDING;
        this.customerId = customerId;
    }

    //TODO payment details, shipping info
}
