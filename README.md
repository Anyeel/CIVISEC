# CIVSEC
Aplicación móvil interactiva que simula una rebelión global de la inteligencia artificial, presentada como una app del gobierno. Realizada como proyecto de clase con temática de Halloween. La aplicación está desarrollada en Android Studio utilizando Java, siguiendo el patrón arquitectónico MVC (Modelo–Vista–Controlador) para mantener una estructura clara, escalable y fácil de mantener.

## Tecnologías utilizadas

- Android Studio (Java): desarrollo completo de la aplicación móvil.

- Figma: diseño visual y creación de los mockups de la interfaz.

- Draw.io: elaboración del diagrama de clases y arquitectura del sistema.

- Overleaf (LaTeX): documentación técnica del proyecto.

- YouTube: Para subir los video que se mostrarán embebidos.

## Estructura del proyecto

Modelo (Model): define la estructura de datos de alertas, fases, localizaciones, refugios y consejos.

Vista (View): interfaz de usuario con pantallas como inicio, mapa de refugios, alertas, consejos y fase activa.

Controlador (Controller): gestiona la lógica entre la vista y el modelo, incluyendo cambios de fase, notificaciones, y detección de ubicación.

## Funcionalidades principales

Recepción de alertas aleatorias (simuladas).

Avance progresivo de fases narrativas (Fase 1 a Fase Final).

Detección de ubicación para mostrar "refugios cercanos".

Sección de preguntas frecuentes y consejos de emergencia.

Corrupción visual y funcional de la app a medida que avanza la historia.

Simulación de que la IA toma el control del dispositivo.

## Mockups y diagramas

Mockups: Ver diseño completo en [Figma](https://www.figma.com/design/gc1FMTqOT7ZpMJXCRrs4zs/CIVSEC?node-id=0-1&p=f&t=4rstzXFrBYaPUnHN-0)

Diagramas: incluidos en formato .png y .drawio en la carpeta /Diagramas.

## Requisitos y apuntes

Aplicación que simula al gobierno
Simula un apocalipsis
Notifica al usuario
El problema empora con el paso del tiempo

Usa ubicación o cualquier hardware del dispositivo, 2 idealmente
Notificación en segundo plano/cerrada

Boton de desarrollador

2 Apks, una para usuario y otra para desarrollador
Boton de desarrollo permite lanzar notificaciones o que avance la historia

Hardware:
- Vibración
- Linterna
- **GPS**
- **Bluethoot**
- WiFi
- Acelerómetro
- Giroscopio
- EMF
- Cámara
- NFC
- QR
- Batería
- Verficiación biométrica
- Cobertura
- Nearby

Notifiaciones:
- Texto.
- Aplicación cerrada o segundo plano.
- Cuando la pulso me lleve al apartado.

Ideas:
inputstream
borrar los archivos que vaya imprimiendo, ordenarlo por carpetas y cuando se terminen, pasas de fase.

## Objetivo del proyecto

El propósito principal es crear una experiencia inmersiva, narrativa y visualmente atractiva para Halloween, utilizando elementos de horror psicológico y simulación realista. La app está pensada como una sátira tecnológica que combina diseño de interfaz, narrativa interactiva y programación móvil para ofrecer una historia envolvente en tiempo real.
