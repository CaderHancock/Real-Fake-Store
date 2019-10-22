#!/bin/bash
curl -d '{"name":"Harry Potter","price":19.85,"description":"By JK Rowling","imgPath":"null","numInInventory":99,"category":"BOOKS","upc":"794827501844"}' -H "Content-Type: application/json" -X POST http://localhost:8081/api/products/
echo "\n"
curl -d '{"name":"Clean Code","price":39.85,"description":"By Richard Martin","imgPath":"null","numInInventory":99,"category":"BOOKS","upc":"794823251844"}' -H "Content-Type: application/json" -X POST http://localhost:8081/api/products/

echo "\n"
curl -d '{"name":"Box Fan","price":39.85,"description":"The Best Fan","imgPath":"null","numInInventory":29,"category":"HOME","upc":"794823251844"}' -H "Content-Type: application/json" -X POST http://localhost:8081/api/products/


echo "\n"
curl -d '{"name":"Hair Gel","price":9.80,"description":"Better than Elmers glue","imgPath":"null","numInInventory":29,"category":"HOME","upc":"794823251844"}' -H "Content-Type: application/json" -X POST http://localhost:8081/api/products/
echo "\n"
curl -X GET http://localhost:8081/api/customers/create/John/Doe/John.Doe@geocities.com
echo "n"
curl -X GET "http://localhost:8081/api/customers/5/addresses/add/123%20Fake%20St/AnyTown/12345/WA/USA"
echo "\n"

curl -X GET http://localhost:8081/api/customers/create/Jane/Doe/Jane.Doe@discountperscriptions.ru


