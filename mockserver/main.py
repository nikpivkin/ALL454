from simple_http_server import route, server, JSONBody


@route("/", method=["POST"])
def index(json=JSONBody()):
    print(json)


server.start(port=9090)
