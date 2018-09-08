from flask import Flask, request

app = Flask(__name__)

@app.route('/')
def hello_world():
    """Print 'Hello, world!' as the response body."""
    return 'Hello, world!'

if __name__ == '__main__':
    app.run('0.0.0.0', 8000, debug=True)