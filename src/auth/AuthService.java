package auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio de autenticación.
 * Solo puede existir un usuario con rol GERENTE en el sistema.
 * Los datos se almacenan en memoria durante la ejecución.
 */
public class AuthService {

    private final Map<String, User> usuariosPorCorreo = new HashMap<>();
    private final Map<String, User> usuariosPorId     = new HashMap<>();

    public AuthService() {
        cargarUsuariosPorDefecto();
    }

    /**
     * Usuarios de demo precargados para facilitar las pruebas.
     * Credenciales del gerente: gerente@demo.com / 1234
     */
    private void cargarUsuariosPorDefecto() {
        registrar("Ana Torres",  "gerente@demo.com",  "1234", Role.GERENTE);
        registrar("Luis Pérez",  "dev1@demo.com",     "1234", Role.DESARROLLADOR);
        registrar("Sara Ruiz",   "dev2@demo.com",     "1234", Role.DESARROLLADOR);
        registrar("María Gómez", "cliente@demo.com",  "1234", Role.CLIENTE);
    }

    /**
     * Registra un nuevo usuario aplicando las siguientes reglas:
     * - El correo no puede estar en uso.
     * - Solo puede existir un gerente en el sistema.
     *
     * @return el usuario creado, o null si no se pudo registrar
     */
    public User registrar(String nombre, String correo, String contrasena, Role rol) {
        String correoNorm = correo.trim().toLowerCase();

        if (usuariosPorCorreo.containsKey(correoNorm)) return null;
        if (rol == Role.GERENTE && yaExisteGerente())  return null;

        User usuario = new User(UUID.randomUUID().toString(), nombre, correoNorm, contrasena, rol);
        usuariosPorCorreo.put(correoNorm, usuario);
        usuariosPorId.put(usuario.getId(), usuario);
        return usuario;
    }

    /** Valida credenciales y retorna el usuario si son correctas. */
    public User iniciarSesion(String correo, String contrasena) {
        String correoNorm = correo.trim().toLowerCase();
        User usuario = usuariosPorCorreo.get(correoNorm);
        if (usuario != null && usuario.getContrasena().equals(contrasena)) {
            return usuario;
        }
        return null;
    }

    public boolean correoExiste(String correo) {
        return usuariosPorCorreo.containsKey(correo.trim().toLowerCase());
    }

    public boolean yaExisteGerente() {
        return usuariosPorCorreo.values().stream()
                .anyMatch(u -> u.getRol() == Role.GERENTE);
    }

    public User buscarPorId(String id) {
        return usuariosPorId.get(id);
    }

    /** Retorna solo los desarrolladores registrados (para asignar tareas). */
    public List<User> obtenerDesarrolladores() {
        List<User> devs = new ArrayList<>();
        for (User u : usuariosPorCorreo.values()) {
            if (u.getRol() == Role.DESARROLLADOR) devs.add(u);
        }
        return devs;
    }

    public Collection<User> obtenerTodos() {
        return usuariosPorCorreo.values();
    }
}
