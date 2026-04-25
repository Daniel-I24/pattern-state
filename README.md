# Sistema de Gestión de Proyectos - Patrón State

Sistema completo de gestión de proyectos de software que implementa el **Patrón State** para controlar el ciclo de vida de proyectos y tareas. Cada rol (Gerente, Desarrollador, Cliente) tiene su propia interfaz adaptada a sus funciones.

---

## 🎯 Características principales

### Patrón State implementado en:
- **Proyectos**: 12 estados desde Propuesto hasta Archivado
- **Tareas**: 6 estados incluyendo aprobación del gerente

### Roles y permisos:
- **Gerente** (único en el sistema):
  - Crear proyectos
  - Asignar tareas a desarrolladores con orden de ejecución
  - Aprobar o rechazar tareas con justificación obligatoria
  - Avanzar el estado del proyecto
  - Habilitar siguiente grupo de tareas secuenciales

- **Desarrollador**:
  - Ver proyectos donde tiene tareas asignadas
  - Trabajar solo en tareas del grupo activo
  - Marcar tareas como listas para aprobación
  - Ver feedback del gerente en caso de rechazo

- **Cliente**:
  - Ver estado de todos los proyectos
  - Aprobar o solicitar correcciones cuando el proyecto llega a revisión
  - Ver progreso general y resumen de tareas

### Funcionalidades clave:
- ✅ Tareas paralelas y secuenciales (definidas por orden)
- ✅ Aprobación obligatoria del gerente antes de avanzar
- ✅ Justificación obligatoria en rechazos
- ✅ Barra de progreso en tiempo real
- ✅ Historial completo de cambios de estado
- ✅ Placeholders en todos los formularios
- ✅ Interfaz diferente para cada rol
- ✅ Datos compartidos en memoria entre todos los roles

---

## 🔐 Credenciales de acceso

### Gerente (único)
```
Correo:     gerente@demo.com
Contraseña: 1234
Nombre:     Ana Torres
```

### Desarrolladores
```
Correo:     dev1@demo.com
Contraseña: 1234
Nombre:     Luis Pérez

Correo:     dev2@demo.com
Contraseña: 1234
Nombre:     Sara Ruiz
```

### Cliente
```
Correo:     cliente@demo.com
Contraseña: 1234
Nombre:     María Gómez
```

---

## 🚀 Compilación y ejecución

### Opción 1: Scripts automáticos (recomendado)

**Windows:**
```bash
run.bat
```

**Linux/Mac:**
```bash
chmod +x run.sh
./run.sh
```

### Opción 2: Manual

**Compilar:**
```bash
javac -encoding UTF-8 -d out src/auth/*.java src/model/*.java src/state/task/*.java src/state/project/*.java src/service/*.java src/ui/*.java src/ui/shared/*.java src/ui/manager/*.java src/ui/developer/*.java src/ui/client/*.java src/main/*.java
```

**Ejecutar:**
```bash
java -cp out main.AppLauncher
```

**Nota:** La carpeta `out/` contiene los archivos compilados (.class) y no debe subirse a GitHub (ya está en .gitignore).

---

## 📁 Estructura del proyecto

```
src/
├── auth/
│   ├── Role.java                    # Enum de roles
│   ├── User.java                    # Modelo de usuario
│   ├── AuthService.java             # Autenticación (solo 1 gerente)
│   └── SessionManager.java          # Sesión activa
├── model/
│   ├── Project.java                 # Contexto del patrón State (proyectos)
│   ├── Task.java                    # Contexto del patrón State (tareas)
│   └── StateChange.java             # Registro de cambios de estado
├── state/
│   ├── project/
│   │   ├── ProjectState.java        # Interfaz State para proyectos
│   │   ├── BaseProjectState.java    # Clase base con comportamiento por defecto
│   │   ├── ProposedState.java       # Estado concreto
│   │   ├── UnderEvaluationState.java
│   │   ├── ApprovedState.java
│   │   ├── PlanningState.java
│   │   ├── InDevelopmentState.java
│   │   ├── InTestingState.java
│   │   ├── ClientReviewState.java
│   │   ├── InCorrectionsState.java
│   │   ├── AcceptedState.java
│   │   ├── DeployedState.java
│   │   ├── InMaintenanceState.java
│   │   └── ArchivedState.java
│   └── task/
│       ├── TaskState.java           # Interfaz State para tareas
│       ├── PendingTaskState.java    # Estado concreto
│       ├── InProgressTaskState.java
│       ├── UnderReviewTaskState.java
│       ├── PendingApprovalTaskState.java  # Espera aprobación del gerente
│       ├── CompletedTaskState.java
│       └── BlockedTaskState.java
├── service/
│   └── ProjectService.java          # Lógica de negocio y repositorio
├── ui/
│   ├── AppTheme.java                # Paleta de colores y estilos
│   ├── LoginPanel.java              # Pantalla de login
│   ├── RegisterPanel.java           # Pantalla de registro
│   ├── shared/
│   │   ├── ProjectProgressBar.java  # Barra de progreso reutilizable
│   │   └── HistoryPanel.java        # Historial de cambios reutilizable
│   ├── manager/
│   │   ├── ManagerDashboard.java    # Interfaz del gerente
│   │   ├── ManagerProjectPanel.java # Detalle de proyecto (gerente)
│   │   └── ManagerTaskPanel.java    # Gestión de tareas (gerente)
│   ├── developer/
│   │   ├── DeveloperDashboard.java  # Interfaz del desarrollador
│   │   └── DeveloperProjectPanel.java # Detalle de proyecto (dev)
│   └── client/
│       ├── ClientDashboard.java     # Interfaz del cliente
│       └── ClientProjectPanel.java  # Detalle de proyecto (cliente)
└── main/
    └── AppLauncher.java             # Punto de entrada
```

---

## 🎨 Paleta de colores

- **Fondo principal**: Crema (#F5F0E8)
- **Paneles**: Crema claro (#FFFDF7)
- **Sidebar**: Café oscuro (#3D2B1F)
- **Acentos**: Café medio (#6B4C35) y claro (#8B7355)
- **Éxito**: Verde bosque (#4A7C59)
- **Error**: Sienna (#A0522D)
- **Aviso**: Beige dorado (#C8A96E)

---

## 🔄 Flujo de trabajo completo

1. **Gerente** crea un proyecto (estado: Propuesto)
2. **Gerente** lo evalúa → Aprueba → Inicia planificación
3. **Gerente** agrega tareas con orden de ejecución:
   - Orden 1: Tareas paralelas (se pueden trabajar al mismo tiempo)
   - Orden 2: Tareas secuenciales (esperan a que orden 1 esté aprobado)
4. **Gerente** inicia desarrollo
5. **Desarrollador** trabaja las tareas del grupo activo:
   - Inicia tarea → Trabaja → Marca como lista
6. **Gerente** revisa y aprueba o rechaza con justificación
7. Si rechaza, **Desarrollador** ve el feedback y corrige
8. Cuando todas las tareas del grupo están aprobadas, el siguiente grupo se desbloquea
9. **Gerente** pasa a pruebas (solo si todas las tareas están completadas)
10. **Gerente** envía al cliente
11. **Cliente** aprueba o solicita correcciones
12. **Gerente** despliega → Mantenimiento → Archiva

---

## ✨ Buenas prácticas implementadas

- ✅ Patrón State correctamente aplicado (sin if/switch en el contexto)
- ✅ Principio de Responsabilidad Única (SRP)
- ✅ Principio Abierto/Cerrado (OCP)
- ✅ Código limpio y modular
- ✅ Nombres descriptivos en inglés
- ✅ Comentarios en español
- ✅ Sin código muerto ni archivos innecesarios
- ✅ Separación de responsabilidades (UI, lógica, estado)
- ✅ Reutilización de componentes (ProgressBar, HistoryPanel)
- ✅ Validaciones de negocio (solo 1 gerente, tareas secuenciales, etc.)

---

## 📝 Notas importantes

- Solo puede existir **un gerente** en el sistema
- Las tareas con el **mismo orden son paralelas**
- Las tareas de **orden mayor esperan** a que todas las anteriores estén aprobadas
- El **gerente debe aprobar** cada tarea antes de que el desarrollador pueda avanzar
- La **justificación es obligatoria** al rechazar una tarea
- Los **datos se comparten en memoria** entre todos los roles en tiempo real
- Cada rol ve una **interfaz completamente diferente** adaptada a sus funciones

---

## 🎓 Caso de estudio

**Sistema de Gestión de Proyectos de Software**

Simula el ciclo de vida real de un proyecto de desarrollo de software desde la propuesta inicial hasta el mantenimiento en producción, pasando por planificación, desarrollo, pruebas, revisión del cliente y despliegue.

El patrón State permite que cada fase del proyecto tenga comportamientos específicos y transiciones controladas, evitando estados inválidos y garantizando que el flujo de trabajo se respete.

---

**Desarrollado como proyecto académico - Patrón State**
