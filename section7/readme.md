Docker cmd:
docker run -p 3307:3306 --name accountsdb -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=accountsdb -d mysql
To confirm pwd: docker exec -it accountsdb mysql -u root -p

docker run -p 3309:3306 --name loansdb -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=loansdb -d mysql
docker run -p 3308:3306 --name loansdb -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=cardsdb -d mysql
Connection string url:
jdbc:mysql://localhost:3307/accountsdb?allowPublicKeyRetrieval=true&useSSL=false
jdbc:mysql://localhost:3309/loansdb?allowPublicKeyRetrieval=true&useSSL=false
jdbc:mysql://localhost:3308/cardsdb?allowPublicKeyRetrieval=true&useSSL=false