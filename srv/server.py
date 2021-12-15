import socket
import threading

# Any unused port
PORT = 50256
# Local IPv6 address
SERVER = socket.gethostbyname(socket.gethostname())
# Server address
ADDR = (SERVER, PORT)
# Byte format
FORMAT = 'utf-8'
# Terminator
TERM = '\n'
# Server online
online = True


# Initialising socket data stream
server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.bind(ADDR)

def handle(client: socket.socket):
    """
    Method for handling a new connection
    """
    pass

def load():
    """
    Sets the server online and lets it listen for new connections which will then be handled in a separate thread
    """
    server.listen()
    while online:
        conn, addr = server.accept()
        thread = threading.Thread(target=handle, args=(conn,))
        thread.start()


if __name__ == '__main__':
    load()
