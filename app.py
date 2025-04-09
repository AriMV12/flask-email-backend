from flask import Flask, request, jsonify
import logging
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
import os
from flask_cors import CORS

# Configurar logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app)  # Habilitar CORS para todas las rutas

@app.route('/')
def index():
    return "Servidor Flask en Render funcionando para Danary's Coffee"

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

        # Configuración del correo
        smtp_server = "smtp.gmail.com"
        port = 587
        
        # Obtener credenciales de variables de entorno
        sender_email = os.environ.get("EMAIL_USER")
        password = os.environ.get("EMAIL_PASSWORD")
        
        if not sender_email or not password:
            logger.error("Credenciales de email no configuradas")
            return jsonify({"error": "Error de configuración del servidor"}), 500

        # Crear mensaje
        message = MIMEMultipart("alternative")
        message["Subject"] = "¡Gracias por tu compra en Danary's Coffee!"
        message["From"] = sender_email
        message["To"] = email

        # Crear versión HTML del mensaje
        html = f"""
        <html>
        <body>
            <h1 style="color: #b35c27;">¡Gracias por tu compra en Danary's Coffee!</h1>
            <p>Hemos recibido tu pedido y está siendo procesado.</p>
            <h2>Detalles del pedido:</h2>
            <ul>
                <li><strong>Producto:</strong> {producto}</li>
                <li><strong>Cantidad:</strong> {cantidad}</li>
                <li><strong>Total:</strong> {total}</li>
            </ul>
            <p>Esperamos que disfrutes tu bebida ☕</p>
            <p>Atentamente,<br>El equipo de Danary's Coffee</p>
        </body>
        </html>
        """
        
        # Convertir a objeto MIMEText
        html_mime = MIMEText(html, "html")
        
        # Agregar HTML al mensaje
        message.attach(html_mime)
        
        # Enviar el correo
        server = None
        try:
            # Crear conexión segura con el servidor
            server = smtplib.SMTP(smtp_server, port)
            server.starttls()
            # Iniciar sesión
            server.login(sender_email, password)
            # Enviar correo
            server.sendmail(sender_email, email, message.as_string())
            logger.info(f"Correo enviado correctamente a {email}")
            return jsonify({"message": "Correo enviado correctamente"}), 200
        except Exception as e:
            logger.error(f"Error al enviar correo: {str(e)}")
            return jsonify({"error": f"Error al enviar correo: {str(e)}"}), 500
        finally:
            if server:
                server.quit()
            
    except Exception as e:
        logger.exception("Excepción al procesar la solicitud")
        return jsonify({"error": f"Error del servidor: {str(e)}"}), 500

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
