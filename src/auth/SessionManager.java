package auth;

/**
 * Gestiona la sesión activa del usuario durante la ejecución.
 * Solo puede haber un usuario autenticado a la vez.
 */
public class SessionManager {

    private static SessionManager instancia;
    private User usuarioActivo;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instancia == null) {
            instancia = new SessionManager();
        }
        return instancia;
    }

    public void iniciarSesion(User usuario) {
        this.usuarioActivo = usuario;
    }

    public void cerrarSesion() {
        this.usuarioActivo = null;
    }

    public User getUsuarioActivo() {
        return usuarioActivo;
    }

    public boolean haySesionActiva() {
        return usuarioActivo != null;
    }

    public boolean esGerente() {
        return haySesionActiva() && usuarioActivo.getRol() == Role.GERENTE;
    }

    public boolean esDesarrollador() {
        return haySesionActiva() && usuarioActivo.getRol() == Role.DESARROLLADOR;
    }

    public boolean esCliente() {
        return haySesionActiva() && usuarioActivo.getRol() == Role.CLIENTE;
    }
}
