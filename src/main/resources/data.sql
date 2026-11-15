DELETE FROM support_orders;

INSERT INTO support_orders (external_reference, customer_id, category, description, channel, priority, status, created_at) VALUES
('MOB-1001', 'CUST-001', 'PAYMENT', 'El pago aparece como pendiente después de varios minutos en la app.', 'MOBILE_APP', 'MEDIUM', 'OPEN', NOW() - INTERVAL '1 hour'),
('MOB-1002', 'CUST-002', 'TECHNICAL', 'La aplicación se cierra inesperadamente al intentar subir una foto.', 'MOBILE_APP', 'HIGH', 'IN_PROGRESS', NOW() - INTERVAL '5 hours'),
('MOB-1003', 'CUST-003', 'ACCOUNT', 'No puedo cambiar mi contraseña desde la sección de perfil.', 'WEB', 'LOW', 'RESOLVED', NOW() - INTERVAL '2 days'),
('MOB-1004', 'CUST-001', 'OTHER', 'Solicitud de eliminación de cuenta y datos personales.', 'EMAIL', 'MEDIUM', 'CANCELLED', NOW() - INTERVAL '10 days'),
('MOB-1005', 'CUST-004', 'PAYMENT', 'Doble cobro en la transacción del día de ayer.', 'MOBILE_APP', 'HIGH', 'OPEN', NOW() - INTERVAL '30 minutes'),
('MOB-1006', 'CUST-005', 'TECHNICAL', 'Error 500 al consultar el historial de pedidos.', 'WEB', 'MEDIUM', 'OPEN', NOW() - INTERVAL '2 hours'),
('MOB-1007', 'CUST-006', 'ACCOUNT', 'Mi cuenta fue bloqueada sin razón aparente.', 'PHONE', 'HIGH', 'IN_PROGRESS', NOW() - INTERVAL '1 day'),
('MOB-1008', 'CUST-002', 'PAYMENT', 'El cupón de descuento no se aplica en el checkout.', 'MOBILE_APP', 'LOW', 'RESOLVED', NOW() - INTERVAL '3 days'),
('MOB-1009', 'CUST-007', 'TECHNICAL', 'La geolocalización no funciona en dispositivos Android.', 'MOBILE_APP', 'MEDIUM', 'OPEN', NOW() - INTERVAL '4 hours'),
('MOB-1010', 'CUST-008', 'OTHER', 'Consulta sobre los términos y condiciones del servicio.', 'EMAIL', 'LOW', 'RESOLVED', NOW() - INTERVAL '15 days'),
('MOB-1011', 'CUST-003', 'PAYMENT', 'Reembolso no procesado después de 10 días hábiles.', 'WEB', 'HIGH', 'IN_PROGRESS', NOW() - INTERVAL '6 hours'),
('MOB-1012', 'CUST-009', 'ACCOUNT', 'Actualización de datos bancarios fallida.', 'MOBILE_APP', 'MEDIUM', 'OPEN', NOW() - INTERVAL '1 hour'),
('MOB-1013', 'CUST-010', 'TECHNICAL', 'Las notificaciones push no llegan al dispositivo.', 'MOBILE_APP', 'MEDIUM', 'CANCELLED', NOW() - INTERVAL '20 days'),
('MOB-1014', 'CUST-004', 'PAYMENT', 'Factura electrónica no disponible para descarga.', 'WEB', 'LOW', 'OPEN', NOW() - INTERVAL '3 hours'),
('MOB-1015', 'CUST-005', 'ACCOUNT', 'Solicitud de cambio de correo electrónico principal.', 'EMAIL', 'MEDIUM', 'RESOLVED', NOW() - INTERVAL '5 days'),
('MOB-1016', 'CUST-006', 'TECHNICAL', 'La pantalla de login se queda en blanco en iOS 16.', 'MOBILE_APP', 'HIGH', 'OPEN', NOW() - INTERVAL '45 minutes'),
('MOB-1017', 'CUST-007', 'OTHER', 'Reporte de bug en la sección de ayuda.', 'WEB', 'LOW', 'IN_PROGRESS', NOW() - INTERVAL '8 hours'),
('MOB-1018', 'CUST-008', 'PAYMENT', 'Error al vincular nueva tarjeta de crédito.', 'MOBILE_APP', 'HIGH', 'OPEN', NOW() - INTERVAL '2 hours'),
('MOB-1019', 'CUST-009', 'TECHNICAL', 'El tiempo de carga de la página de inicio es muy alto.', 'WEB', 'MEDIUM', 'RESOLVED', NOW() - INTERVAL '12 days'),
('MOB-1020', 'CUST-010', 'ACCOUNT', 'Problema con la verificación de dos factores (2FA).', 'PHONE', 'HIGH', 'IN_PROGRESS', NOW() - INTERVAL '30 minutes');