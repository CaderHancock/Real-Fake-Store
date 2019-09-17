INSERT INTO product (PRODUCT_ID, name, price,  description, imgPath, numInInventory, category) VALUES
 ('1', 'Box Fan', '100', 'The best fan', 'null', '1', 'HOME'),
  ('2', 'Clean Code', '10', 'By Uncle Bob', 'null', '100', 'BOOKS');
INSERT INTO SHOPPING_CART (ID) VALUES ('1'),('2');
INSERT INTO CUSTOMER (CUSTOMER_ID, FIRST_NAME, LAST_NAME, SHOPPING_CART_ID ) VALUES
    ('1', 'John', 'Doe', '1'),
    ('2', 'Jane', 'Doe', '2');
INSERT INTO SHOPPING_CART_PRODUCT_QUANTITY_MAP (SHOPPING_CART_ID, PRODUCT_QUANTITY_MAP, PRODUCT_QUANTITY_MAP_KEY ) VALUES
    ('1','1','1'),
    ('1','3','2'),
    ('2', '2', '2');
INSERT INTO ADDRESS (ADDRESS_ID, CITY, COUNTRY, POSTAL_CODE, STATE, STREET_ADDRESS ) VALUES
    ('1', 'AnyTown', 'USA', '12345', 'WA', '123 Fake St');
INSERT INTO CUSTOMER_ADDRESSES (CUSTOMER_CUSTOMER_ID, ADDRESSES_ADDRESS_ID ) VALUES
    ('1', '1');