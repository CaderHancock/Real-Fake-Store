package gq.cader.realfakestoreserver.model.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@Entity
public class Customer {
    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer customerId;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @NonNull
    private ShoppingCart shoppingCart;

    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @Column(name = "ADDRESSES")
    @EqualsAndHashCode.Exclude
    @NonNull
    private Set<Address> addresses;

    // TODO Figure out what annotation needed to
    // force unique or one to one relationship
    private String email;

    @ElementCollection(targetClass = Order.class)
    @OneToMany(targetEntity = Order.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @NonNull
    private Set<Order> orders;

    public Customer() {
        shoppingCart = new ShoppingCart();
        addresses = new HashSet<>();
        orders = new HashSet<>();
    }

    public Customer(String firstName, String lastName, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
