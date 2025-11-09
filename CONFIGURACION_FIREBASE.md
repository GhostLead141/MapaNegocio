# Configuración de Firebase para la Aplicación Mapa

## Pasos para configurar Firebase Firestore

### 1. Crear un proyecto en Firebase Console

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Haz clic en "Agregar proyecto" o selecciona un proyecto existente
3. Sigue los pasos para crear el proyecto

### 2. Agregar una aplicación Android al proyecto

1. En la consola de Firebase, haz clic en el ícono de Android
2. Ingresa el nombre del paquete de la aplicación: `com.example.mapa`
3. Registra la aplicación
4. Descarga el archivo `google-services.json`

### 3. Configurar el archivo google-services.json

1. Coloca el archivo `google-services.json` descargado en la carpeta `app/` del proyecto
2. La estructura debería ser:
   ```
   app/
     google-services.json
     build.gradle.kts
     ...
   ```

### 4. Habilitar Firebase Authentication

1. En Firebase Console, ve a "Authentication"
2. Haz clic en "Comenzar" o "Get started"
3. Ve a la pestaña "Sign-in method"
4. Haz clic en "Email/Password"
5. Habilita el primer toggle (Email/Password)
6. Haz clic en "Guardar"

### 5. Configurar Firestore Database

1. En Firebase Console, ve a "Firestore Database"
2. Haz clic en "Crear base de datos"
3. Selecciona "Comenzar en modo de prueba" (para desarrollo)
4. Selecciona la ubicación de la base de datos
5. Haz clic en "Habilitar"

### 6. Configurar reglas de seguridad (Importante para producción)

En la pestaña "Reglas" de Firestore, configura las reglas según tus necesidades:

**Para desarrollo (modo prueba - permite lectura/escritura durante 30 días):**
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.time < timestamp.date(2024, 12, 31);
    }
  }
}
```

**Para producción (recomendado - solo lectura/escritura autenticada):**
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /businesses/{businessId} {
      allow read: if true; // Todos pueden leer
      allow write: if request.auth != null; // Solo usuarios autenticados pueden escribir
    }
  }
}
```

### 7. Estructura de datos en Firestore

La aplicación guarda los datos en la colección `businesses` con la siguiente estructura:

```json
{
  "name": "Nombre del negocio",
  "address": "Dirección del negocio",
  "service": "Tipo de servicio",
  "averageRating": 4.5,
  "reviews": [
    {
      "rating": 5.0,
      "comment": "Excelente servicio",
      "userEmail": "usuario@example.com"
    }
  ]
}
```

### 8. Verificar la configuración

1. Sincroniza el proyecto en Android Studio
2. Compila la aplicación
3. Ejecuta la aplicación
4. Agrega un negocio desde la aplicación
5. Verifica en Firebase Console que el negocio se haya guardado en Firestore

## Funcionalidades de Autenticación

La aplicación ahora incluye autenticación completa con Firebase:

- **Crear cuenta**: Los usuarios pueden crear una cuenta con email y contraseña
- **Iniciar sesión**: Los usuarios pueden iniciar sesión con sus credenciales
- **Verificación de sesión**: La aplicación verifica automáticamente si el usuario está autenticado
- **Cerrar sesión**: Los usuarios pueden cerrar sesión desde MainActivity
- **Persistencia de sesión**: Firebase mantiene la sesión del usuario incluso después de cerrar la app

## Notas importantes

- El archivo `google-services.json` debe estar en la carpeta `app/` (no en `app/src/main/`)
- Asegúrate de habilitar Email/Password en Firebase Authentication
- Asegúrate de que las reglas de Firestore permitan lectura y escritura durante el desarrollo
- Para producción, configura reglas de seguridad más restrictivas
- Los datos se sincronizan automáticamente entre la aplicación y Firebase
- Las contraseñas deben tener al menos 6 caracteres (requisito de Firebase)

## Solución de problemas

### Error: "google-services.json is missing"
- Asegúrate de que el archivo `google-services.json` esté en la carpeta `app/`
- Sincroniza el proyecto (File > Sync Project with Gradle Files)

### Error: "Permission denied"
- Verifica las reglas de seguridad de Firestore en Firebase Console
- Asegúrate de que las reglas permitan lectura/escritura

### Los datos no se guardan
- Verifica la conexión a internet
- Revisa los logs en Android Studio (Logcat) para ver errores de Firebase
- Verifica que el proyecto de Firebase esté correctamente configurado

### Error: "Email/Password sign-in method is not enabled"
- Ve a Firebase Console > Authentication > Sign-in method
- Habilita Email/Password
- Guarda los cambios

### Error: "Password should be at least 6 characters"
- Firebase requiere contraseñas de al menos 6 caracteres
- Asegúrate de validar la longitud de la contraseña en la aplicación

### Usuario no puede iniciar sesión
- Verifica que el usuario esté registrado en Firebase Authentication
- Revisa los logs en Android Studio para ver el error específico
- Verifica que Email/Password esté habilitado en Firebase Console

