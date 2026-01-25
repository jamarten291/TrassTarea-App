# App Gestión de Tareas

App de Android desarrollada en Java para crear, organizar y hacer seguimiento de tareas personales o de equipo. Proporciona una interfaz sencilla basada en Material Design para gestionar tareas con prioridades, fechas de vencimiento y estados (pendiente/completada).

---

## Características principales

- Crear, editar y eliminar tareas.
- Marcar tareas como completadas / pendientes.
- Establecer título, descripción, prioridad y fecha de vencimiento.
- Listado de tareas con orden por fecha/prioridad.
- Búsqueda y filtrado (por estado y prioridad).
- Persistencia local de datos (base de datos local).
- Interfaz adaptada a móviles (Material Design).

> [!NOTE] 
> Algunas funcionalidades pueden requerir configuración adicional o integración con bibliotecas externas (p. ej. Room para persistencia).

---

## Tecnologías y librerías (usadas / recomendadas)

- Lenguaje: Java
- IDE / Build: Android Studio, Gradle
- SDK: Android SDK (AndroidX)
- UI: Material Components, RecyclerView, ConstraintLayout
- Persistencia: SQLite con Room
- Arquitectura: ViewModel, LiveData
- Tests: JUnit, Espresso
- Control de versiones: Git / GitHub

Ajusta esta lista si tu proyecto ya integra otras dependencias específicas.

---

## Requisitos previos

- Android Studio (versión recomendada: Arctic Fox / Chipmunk o posterior)
- JDK 11+
- SDK Platforms y Tools instalados
- Emulador o dispositivo Android con depuración USB activada

---

## Instalación y ejecución

1. Clona el repositorio:
   ```
   git clone https://github.com/jamarten291/App-Gestion-Tareas.git
   ```
2. Abre Android Studio y selecciona "Open an existing project". Navega hasta la carpeta clonada.
3. Espera a que Gradle sincronice las dependencias.
4. Si es necesario, instala las SDK/Build tools que Gradle solicite.
5. Ejecuta la app en un emulador o dispositivo físico desde Android Studio (Run > Run 'app').

---

## Uso

- La pantalla principal muestra el listado de tareas.
- Usa el botón + (o FAB) para crear una nueva tarea: agrega título, descripción, prioridad y fecha de vencimiento.
- Toca una tarea para editarla o deslízala/usa el menú contextual para eliminarla.
- Marca la casilla o usa el gesto indicado para marcarla como completada.
- Usa la barra de búsqueda y filtros para encontrar tareas por texto, estado o prioridad.

---

## Estructura del proyecto (resumen)

- app/
  - src/main/java/… — código fuente (activities, adapters, viewmodels, repositorios)
  - src/main/res/… — layouts, drawables, strings, styles
  - src/androidTest/… — tests de UI
  - src/test/… — tests unitarios
- build.gradle — configuraciones de Gradle (project y módulo)
- README.md — este archivo

---

## Buenas prácticas realizadas

- Usar Room para la persistencia local y Repository pattern para separar la lógica de acceso a datos.
- Usar ViewModel + LiveData para mantener la UI reactiva y desacoplada.
- Añadir pruebas unitarias y de interfaz para la funcionalidad crítica.
- Localizar strings en `res/values/strings.xml` para facilitar traducciones.

---

## Cómo contribuir

1. Haz fork del repositorio.
2. Crea una rama con el prefijo `feature/` o `fix/` (p. ej. `feature/reminders`).
3. Realiza cambios y añade pruebas cuando corresponda.
4. Abre un Pull Request describiendo los cambios y el motivo.

Por favor abre Issues para reportar bugs o solicitar nuevas funcionalidades.

---

## Licencia

Este proyecto está bajo la licencia MIT. Cambia o añade la licencia que prefieras.

---

## Contacto

Autor: jamarten291  
Repositorio: https://github.com/jamarten291/App-Gestion-Tareas

---
