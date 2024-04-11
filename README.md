# Springboot Redis Cache Demo

## what is cache mechanism ? why it's using ?
> The cache mechanism stores frequently accessed data in a temporary storage layer between the server and the database. When a client requests data, the server first checks the cache for a matching entry. If found, the data is returned directly from the cache, reducing the need for repeated queries to the database and improving system performance.

> JPA itself providing the 2 levels of Cache. <br/>

JPA First level Cache

> will cache the particular table / entity query execution within the same Transaction and that cache wont be avail in next Transaction or client Request. @Transactional annotation is used to mark the method to use same Transaction. Otherwise it'll create new Transaction for all the query execution. <br/>

JPA Second level Cache
> will be used to cache the particular entity so @Cacheable annotation is used at Entity level. So whenever a client requesting the same resource query execution will be cached after the initial one and this will be stored in Persistent Context memory. But Redis Cache will store / cache the entire response for a Client request. <br/>
> REF : </br> https://www.youtube.com/watch?v=AvW94hRknmA&list=PLaLGeHpx4nQmhr6TtLtcSk_rQ4j0q6Dvi&index=43  <br/>  https://www.youtube.com/watch?v=oHVs4gK0MtU


## why Redis Cache ?

## Limitations of Redis Cache

## Redis Interview Questions

-------------------------------------

## Redis Cache configration

```
open CMD and execute the below commands one by one.

curl -fsSL https://packages.redis.io/gpg | sudo gpg --dearmor -o /usr/share/keyrings/redis-archive-keyring.gpg

echo "deb [signed-by=/usr/share/keyrings/redis-archive-keyring.gpg] https://packages.redis.io/deb $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/redis.list

sudo apt-get update
sudo apt-get install redis

sudo service redis-server start

To verify
redis-cli <press enter>

127.0.0.1:6379> ping
PONG

> REF :
https://redis.io/docs/install/install-redis/install-redis-on-linux/#install-on-ubuntu-debian
https://redis.io/docs/install/install-redis/install-redis-on-windows/
``` 

## Springboot app with Redis Cache Implementation
> STEP-1:-
>
Created a Springboot maven JAR project with Web, JPA, Postgres , Redis and Lombok dependencies.

![image](https://github.com/VigneshbabuOfficial/springboot-redis-cache-demo/assets/70185865/878ae31a-aa0c-42fc-af00-617bcac8b5ed)

> step-2:-
>
Create an application.yaml file and apply these content.
```
spring:
  cache:
    type: redis
    host: localhost
    port: 6379
    redis:
      time-to-live: 60000
  datasource:
    url: jdbc:postgresql://localhost/practice_db
    username: postgres
    password: admin
    driver-class-name: org.postgresql.Driver
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

> step-3:- </br>
> Create all these classes </br>
>  [redis-crud-demo-working](https://github.com/VigneshbabuOfficial/springboot-redis-cache-demo/tree/redis-crud-demo-working)

> step-4:-
>

```JS
Open Postman tool and hit the endpoint  GET  http://localhost:8080/product.
Copy the ID and hit the endpoint GET http://localhost:8080/product/152 twice. We can see the log printed for one time. that means the second request got the response from the Cache memory.
Likewise try PUT and DELETE requests also.
```

## HOW TO START THE REDIS CACHE SERVER ?
> open ubuntu CLI -> enter this command `sudo service redis-server start`. enter the password followed by the command. <br/>
then enter this command -> `redis-cli` -> `ping`  -> PONG will be displayed.

> to list out all the keys -> KEYS *

