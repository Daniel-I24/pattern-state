package auth;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio de autenticación: maneja el registro e inicio de sesión de usuarios.
 * Los datos se almacenan en memoria durante la ejecución del programa.
 */
public class AuthService {

    // Mapa de correo -> usuario para búsqueda rápida
    private final Map<String, User> usuariosPorCorreo = new HashMap<>();

    public AuthService() {
        cargarUsuariosPorDefecto();
    }

    /**
     * Usuarios de prueba para facilitar la demostración del sistema.
     */
    private void cargarUsuariosPorDefecto() {
        registrar("Ana Torres",    "gerente@demo.com",      "1234", Role.GERENTE);
        registrar("Luis Pérez",    "dev@demo.com",          "1234", Role.DESARROLLADOR);
        registrar("María Gómez",   "cliente@demo.com",      "1234", Role.CLIENTE);
    }

    /**
     * Registra un nuevo usuario si el correo no está en uso.
     * @return el usuario creado, o null si el correo ya existe
     */
    public User registrar(String nombre, String correo, String contrasena, Role rol) {
        String correoNormalizado = correo.trim().toLowerCase();
        if (usuariosPorCorreo.containsKey(correoNormalizado)) {
            return null;
        }
        User usuario = new User(UUID.randomUUID().toString(), nombre, correoNormalizado, contrasena, rol);
        usuariosPorCorreo.put(correoNormalizado, usuario);
        return usuario;
    }

    /**
     * Valida las credenciales y retorna el usuario si son correctas.
     * @return el usuario autenticado, o null si las credenciales son inválidas
     */
    public User iniciarSesion(String correo, String contrasena) {
        String correoNormalizado = correo.trim().toLowerCase();
        User usuario = usuariosPorCorreo.get(correoNormalizado);
        if (usuario != null && usuario.getContrasena().equals(contrasena)) {
            return usuario;
        }
        return null;
    }

    /**
     * Verifica si un correo ya está registrado en el sistema.
     */
    public boolean correoExiste(String correo) {
        return usuariosPorCorreo.containsKey(correo.trim().toLowerCase());
    }

    public Collection<User> obtenerTodosLosUsuarios() {
        return usuariosPorCorreo.values();
    }
}
