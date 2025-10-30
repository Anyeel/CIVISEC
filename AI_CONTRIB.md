# 1. Uso de herramientas de IA
Marca las que hayas utilizado durante el desarrollo del proyecto:

Gemini

**Explica qué parte del trabajo se realizó con ayuda de la IA (máx. 5 líneas).**

Se utilizó Gemini para consultar la implementación de APIs específicas de Android como AlarmManager, 
osmdroid y el sistema de permisos en tiempo de ejecución. También ayudó a depurar errores de sintaxis 
y lógica, como los NullPointerException. El código generado fue posteriormente refactorizado y adaptado
a la arquitectura MVC del proyecto.

# 2. Fragmentos generados o modificados con IA
**Copia aquí los fragmentos relevantes que provinieron de la IA y explica qué cambios realizaste.**

## Fragmento 1: Programación de Alertas en Segundo Plano
Se solicitó a la IA una plantilla para programar tareas que se ejecuten en el futuro, incluso si la 
aplicación está cerrada. La IA proporcionó la estructura fundamental para usar AlarmManager.

```Java
// Código generado por IA para programar una alarma futura.
// Se utilizó para entender el funcionamiento de AlarmManager y PendingIntent.

private void programarNoticia(Context context, AlarmManager alarmManager,
                              long retardo, int codigo, int tituloId, int textoId) {
    
    // 1. El Intent define qué se va a ejecutar en el futuro.
    // Se le asigna una acción y se le añaden los datos necesarios.
    Intent intent = new Intent(context, AlertManager.class);
    intent.setAction("NOTICIA");
    intent.putExtra("TITULO", tituloId);
    intent.putExtra("TEXTO", textoId);

    // 2. El PendingIntent envuelve al Intent, permitiendo que otra app
    // (en este caso, el sistema de alarmas de Android) lo ejecute en nuestro nombre.
    // La flag IMMUTABLE es una medida de seguridad.
    PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context, codigo, intent, PendingIntent.FLAG_IMMUTABLE
    );

    // 3. Se calcula el momento exacto en el futuro y se le entrega la alarma
    // y el PendingIntent al sistema operativo.
    long momento = SystemClock.elapsedRealtime() + retardo;
    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, momento, pendingIntent);
}
```
Explicación y Cambios Realizados:
La estructura inicial de este método fue generada por la IA para entender cómo interactúan AlarmManager,
Intent y PendingIntent. Posteriormente, modifiqué el código para pasar los datos específicos de mi proyecto
(los IDs de los strings tituloId y textoId) a través de los putExtra. También adapté la lógica para usar 
SystemClock.elapsedRealtime() y setExact() para asegurar que las alarmas funcionen incluso si el dispositivo está en reposo.

## Fragmento 2: Solicitud de Permisos Múltiples en Tiempo de Ejecución
Se pidió a la IA una forma moderna de solicitar varios permisos de Android a la vez. 
La IA proporcionó la estructura basada en ActivityResultLauncher.

```Java
// Código de la IA para manejar la nueva API de solicitud de permisos múltiples.
// Se usó como plantilla para pedir todos los permisos necesarios desde la SplashActivity.

private final ActivityResultLauncher<String[]> requestPermissionsLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
            // Este bloque se ejecuta cuando el usuario responde al diálogo.
            Log.d(TAG, "Permisos respondidos, navegando a MainActivity");
            startMainActivity();
        });

private void checkAndRequestPermissions() {
    ArrayList<String> permissionsToRequest = new ArrayList<>();

    // Se comprueba cada permiso necesario según la versión de Android
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
    }
    // comprobaciones para NOTIFICATIONS y LOCATION

    if (permissionsToRequest.isEmpty()) {
        // Si no hay nada que pedir, continuamos.
        startMainActivity();
    } else {
        // Si faltan permisos, lanzamos el diálogo.
        requestPermissionsLauncher.launch(permissionsToRequest.toArray(new String[0]));
    }
}
```
Explicación y Cambios Realizados:
La IA generó la estructura base para usar ActivityResultLauncher con RequestMultiplePermissions. 
Yo modifiqué el método checkAndRequestPermissions para añadir la lógica específica de mi app: comprobar
los permisos de BLUETOOTH_CONNECT y POST_NOTIFICATIONS solo en las versiones de Android donde son
necesarios, y añadir la comprobación del permiso de ACCESS_FINE_LOCATION para el mapa.

Fragmento 3: Generación de Coordenadas Aleatorias para el Mapa
Se consultó a la IA sobre cómo generar puntos geográficos aleatorios dentro de un área determinada para simular la ubicación de los refugios en el mapa.

```Java
// Fragmento de la IA para generar coordenadas geográficas aleatorias
// dentro de un radio alrededor de un punto central.

private Set<String> generarRefugios(GeoPoint centro) {
    Set<String> ubicaciones = new HashSet<>();
    Random random = new Random();
    double radius = 0.1; // Radio en grados de latitud/longitud
    int count = 7; // Número de puntos a generar

    for (int i = 0; i < count; i++) {
        // La IA proporcionó la fórmula para desplazar la latitud y longitud
        // de forma aleatoria en un rango.
        double lat = centro.getLatitude() + (random.nextDouble() - 0.5) * radius * 2;
        double lon = centro.getLongitude() + (random.nextDouble() - 0.5) * radius * 2;
        ubicaciones.add(lat + "," + lon);
    }
    return ubicaciones;
}
```
Explicación y Cambios Realizados:
Se utilizó la IA para obtener la fórmula matemática para generar un punto aleatorio dentro de un área 
cuadrada alrededor de una coordenada central. A partir de esa fórmula base, adapté el código para generar
un número específico de puntos (7) y para guardar las coordenadas en el formato String "latitud,longitud", 
que es el que mi Controller utiliza para almacenar los refugios en SharedPreferences.
