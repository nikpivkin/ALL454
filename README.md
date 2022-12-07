
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

Run Jenkins instance with plugin:
```shell
mvn hpi:run
```
