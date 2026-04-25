# Instrucciones para subir a GitHub

## 📦 Archivos que DEBES subir

```
pattern-state/
├── .gitignore              ✅ Subir
├── README.md               ✅ Subir
├── RESUMEN_EJECUTIVO.md    ✅ Subir
├── compile.bat             ✅ Subir
├── compile.sh              ✅ Subir
├── run.bat                 ✅ Subir
├── run.sh                  ✅ Subir
└── src/                    ✅ Subir (toda la carpeta)
    ├── auth/
    ├── model/
    ├── state/
    ├── service/
    ├── ui/
    └── main/
```

## ❌ Archivos que NO debes subir

```
out/                        ❌ NO subir (archivos compilados)
*.class                     ❌ NO subir (archivos compilados)
.idea/                      ❌ NO subir (configuración IDE)
*.iml                       ❌ NO subir (configuración IDE)
.vscode/                    ❌ NO subir (configuración IDE)
```

**Nota:** El archivo `.gitignore` ya está configurado para ignorar automáticamente estos archivos.

---

## 🚀 Pasos para subir a GitHub

### 1. Inicializar repositorio local
```bash
git init
git add .
git commit -m "Initial commit: Sistema de Gestión de Proyectos - Patrón State"
```

### 2. Crear repositorio en GitHub
1. Ve a https://github.com/new
2. Nombre sugerido: `project-management-state-pattern`
3. Descripción: `Sistema de gestión de proyectos implementando el patrón State en Java`
4. **NO** inicialices con README (ya tienes uno)
5. Crea el repositorio

### 3. Conectar y subir
```bash
git remote add origin https://github.com/TU_USUARIO/project-management-state-pattern.git
git branch -M main
git push -u origin main
```

---

## 📝 Descripción sugerida para GitHub

**Título:**
```
Sistema de Gestión de Proyectos - Patrón State
```

**Descripción:**
```
Sistema completo de gestión de proyectos de software que implementa el Patrón State 
para controlar el ciclo de vida de proyectos y tareas. Incluye 3 interfaces diferenciadas 
por rol (Gerente, Desarrollador, Cliente) con autenticación, aprobación de tareas, 
tareas paralelas/secuenciales y barra de progreso en tiempo real.

🎯 Características:
- 12 estados de proyecto + 6 estados de tarea
- Patrón State correctamente implementado
- Interfaz gráfica elegante con Swing
- Código limpio y optimizado
- Buenas prácticas de POO

📚 Proyecto académico - Patrones de Software
```

**Topics sugeridos:**
```
java
design-patterns
state-pattern
swing
project-management
software-engineering
academic-project
```

---

## ✅ Verificación antes de subir

Ejecuta estos comandos para verificar que todo está correcto:

```bash
# Ver qué archivos se van a subir
git status

# Ver qué archivos están siendo ignorados
git status --ignored

# Verificar que out/ está ignorado
git check-ignore out/
```

Si `out/` aparece en la lista de archivos ignorados, ¡todo está correcto!

---

## 🔄 Actualizar el repositorio después

Si haces cambios locales y quieres actualizarlos en GitHub:

```bash
git add .
git commit -m "Descripción de los cambios"
git push
```

---

## 📌 Estructura final en GitHub

Tu repositorio se verá así:

```
TU_USUARIO/project-management-state-pattern
│
├── 📄 README.md (se muestra automáticamente)
├── 📄 .gitignore
├── 📄 RESUMEN_EJECUTIVO.md
├── 🔧 compile.bat
├── 🔧 compile.sh
├── 🔧 run.bat
├── 🔧 run.sh
└── 📁 src/
    ├── 📁 auth/
    ├── 📁 model/
    ├── 📁 state/
    ├── 📁 service/
    ├── 📁 ui/
    └── 📁 main/
```

**Nota:** La carpeta `out/` NO aparecerá porque está en `.gitignore`.

---

## 🎓 Licencia sugerida

Si quieres agregar una licencia, crea un archivo `LICENSE` con:

```
MIT License

Copyright (c) 2026 [Tu Nombre]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

¡Listo para subir a GitHub! 🚀
