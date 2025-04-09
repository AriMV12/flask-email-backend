from flask import Flask, request, jsonify
import requests
import logging

# Configurar logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)

@app.route('/')
def index():
    return "Servidor Flask en Render funcionando"

@app.route('/enviar_correo', methods=['POST'])
def enviar_correo():
    try:
        # Registrar datos recibidos
        logger.info(f"Solicitud recibida: {request.data}")
        
        # Extraer datos JSON
        data = request.json
        if not data:
            logger.error("No se recibieron datos JSON")
            return jsonify({"error": "No se recibieron datos JSON"}), 400
            
        # Extraer campos
        email = data.get('to_email')
        producto = data.get('producto_nombre')
        cantidad = data.get('cantidad')
        total = data.get('total')
        
        logger.info(f"Datos extraídos: email={email}, producto={producto}, cantidad={cantidad}, total={total}")
        
        # Validar campos
        if not all([email, producto, cantidad, total]):
            missing = []
            if not email: missing.append("to_email")
            if not producto: missing.append("producto_nombre")
            if not cantidad: missing.append("cantidad")
            if not total: missing.append("total")
            logger.error(f"Faltan datos: {', '.join(missing)}")
            return jsonify({"error": f"Faltan datos: {', '.join(missing)}"}), 400

        # Construir payload para EmailJS
        payload = {
            "service_id": "service_4pzy4w8",
            "template_id": "template_z02d8qe",
            "user_id": "rnOObqazssZG2_vCP",
            "template_params": {
                "to_email": email,
                # Nombres de variables actualizados según tu plantilla
                "to_name": "",  # Si tienes este campo en tu plantilla
                "producto_nombre": producto,
                "cantidad": cantidad,
                "total": total
            }
        }
        
        headers = {"Content-Type": "application/json"}
        logger.info(f"Enviando solicitud a EmailJS: {payload}")
        
        # Enviar solicitud a EmailJS
        response = requests.post(
            "https://api.emailjs.com/api/v1.0/email/send", 
            json=payload, 
            headers=headers
        )
        
        # Registrar respuesta de EmailJS
        logger.info(f"Respuesta de EmailJS: Código={response.status_code}, Contenido={response.text}")
        
        if response.status_code == 200:
            return jsonify({"message": "Correo enviado correctamente"}), 200
        else:
            return jsonify({
                "error": f"Error de EmailJS: {response.text}",
                "status_code": response.status_code
            }), response.status_code
            
    except Exception as e:
        logger.exception("Excepción al procesar la solicitud")
        return jsonify({"error": f"Error del servidor: {str(e)}"}), 500

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
