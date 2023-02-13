
Run mock server:
```shell
cd mockserver
source .venv/bin/activate
python3 main.py
```

Build plugin:
```shell
mvn package
```

To generate documentation, run the command:

```shell
mvn javadoc:javadoc
```

The generated documentation will be located in the `target/site/apidocs` directory.

Run Jenkins instance with plugin:
```shell
mvn hpi:run
```
