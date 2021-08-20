# DataCopierCore
API of the https://github.com/CSCfi/DataCopierEngine

This project uses Quarkus.
## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
mvn compile quarkus:dev
```

## Packaging and running the application

The application can be packaged using:
```shell script
mvn package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `ta
rget/quarkus-app/lib/` directory.

curl -v -d "@ekatask.json" -H "Content-Type: application/json" http://localhost:8080/v1/copy
content of the ekatask.json:
{
    "requester": "arska",
    "source": {
	"type": "IDA",
	"auth": {
	    "token": "puppua"
	},
	"param": {
	    "omistaja": "123",
	    "polku": "foo/bar"
	}
    },
      "destination": {
	  "type": "ALLAS",
	  "protocol": "S3",
	"auth": {
	    "accessKey": "puppua2",
	    "secretKey": "puppua3"
	},
	"param": {
	    "omistaja": "bucket_136",
	    "polku": "foo/bar2"
	}
      }
 }


## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```
## Provided Code

### YAML Config

Configure your application with YAML

[Related guide section...](https://quarkus.io/guides/config-reference#configurat
ion-examples)

The Quarkus application configuration is located in `src/main/resources/applicat
ion.yml`.

## Author

Pekka Järveläinen
