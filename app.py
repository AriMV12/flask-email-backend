from flask import Flask, request, jsonify
import requests

app = Flask(__name__)

@app.route('/')
def index():
    return "Servidor Flask en Render funcionando"

@app.route('/enviar_correo', methods=['POST'])
def enviar_correo():
    data = request.json

    email = data.get('to_email')
    producto = data.get('producto_nombre')
    cantidad = data.get('cantidad')
    total = data.get('total')

    if not all([email, producto, cantidad, total]):
        return jsonify({"error": "Faltan datos"}), 400

    payload = {
        "service_id": "service_4pzy4w8",
        "template_id": "template_z02d8qe",
        "user_id": "rnOObqazssZG2_vCP",
        "template_params": {
            "to_email": email,
            "producto_nombre": producto,
            "cantidad": cantidad,
            "total": total
        }
    }

    headers = {"Content-Type": "application/json"}

    try:
        response = requests.post("https://api.emailjs.com/api/v1.0/email/send", json=payload, headers=headers)
        if response.status_code == 200:
            return jsonify({"message": "Correo enviado correctamente"}), 200
        else:
            return jsonify({"error": response.text}), response.status_code
    except Exception as e:
        return jsonify({"error": str(e)}), 500
