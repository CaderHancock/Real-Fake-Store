# Real Fake Store Server

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/217d14a6e7f74e8fa1b208990d1a4529)](https://www.codacy.com/manual/CaderHancock/realfakestoreserver?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=CaderHancock/realfakestoreserver&amp;utm_campaign=Badge_Grade)

A Spring Boot REST API Portfolio Project

Currently the controller package doesn't have much external functionality (Soon)

Eventually, I would like to write a pure kotlin android client to boogie with the backend, but for now I'm focusing on the M and C, V can come later.

Running at:

35.247.12.204:8081

Requires JDK 11 and Kafka

To build and launch, run build.sh in the project root. 

Things to checkout (If posting a new product or creating a new customer, comment out everything in data.sql)

GET

api/products

api/products/search/{caseInsensitiveString}
            
api/products/{productId}

api/customers

api/customers/{customerId}/addToCart/{productId}/{quantity}

api/customers/{customerId}

api/customers/{customerId}/addresses/add/{Street%20Address}/{City}/{PostalCode}/{State}/{Country}

api/customers/{customerId}/checkout

api/customers/create/{firstname}/{lastname}/{email}

POST 
       
/products/{product json}
 