# CryptoRecommendation Service

This service is meant to provide crypto data insights

## Requirements

The entire list of requirements can be checked [here](src/main/resources/requirements/Crypto%20Recommendations%20Service.pdf)

## Solution Overview

1. Dynamically generate a [Spring Batch Job](src/main/java/com/leonardolariu/cryptorecommendation/config/job/SpringBatchConfig.java) containing
    * a series of steps for loading token data from each resource defined in [application.yml](src/main/resources/application.yml) - it is very scalable to add new tokens in the future (add a new token symbol and token resource, NO code changes required)
    * an additional step for [creating the lookup tables](src/main/java/com/leonardolariu/cryptorecommendation/config/job/ComputeLookupTablesTasklet.java) used for improving the performance of future RMQ (Range Minimum/ Maximum Queries) - to understand this solution, check [Medium post on Sparse Tables](https://medium.com/nybles/sparse-table-f3981fbb1bc8)
2. Create the [service](src/main/java/com/leonardolariu/cryptorecommendation/service/CryptoRecommendationService.java) logic for answering the RMQ in the requirements
3. Expose the [endpoints](src/main/java/com/leonardolariu/cryptorecommendation/controller/CryptoRecommendationController.java) for answering the RMQ
4. Safeguard our API by checking for blacklisted client IPs, missing params, invalid params, constraint violations, etc. before processing any request
5. Centralize the [exception handling](src/main/java/com/leonardolariu/cryptorecommendation/exception/ErrorControllerAdvice.java) logic
6. Create a documentation for our endpoints using OpenAPI 3.0
   * once you run the project locally, you can check the documentation by accessing http://localhost:8080/swagger-ui/index.html
7. Create a [Postman collection](src/main/resources/postman-collection/crypto-recommendation.postman_collection.json) for both the positive and negative scenarios - very handy for e2e manual testing/ debugging

## Solution Complexity
* Memory Complexity - M * O(NlogN), where M is the number of tokens, N is the maximum number of data points per token
* Time Complexity - O(1), constant time for the RMQ received on any of the exposed endpoints